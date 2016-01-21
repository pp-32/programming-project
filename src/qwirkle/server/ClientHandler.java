package qwirkle.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import qwirkle.Board;
import qwirkle.Game;
import qwirkle.HumanPlayer;
import qwirkle.Location;
import qwirkle.Move;
import qwirkle.Player;
import qwirkle.Protocol;
import qwirkle.Stone;

/**
 * Represents a client handler that handles traffic between the server and a connected client. 
 * @author Jerre
 *
 */
public class ClientHandler extends Thread {
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;
	private Game currentGame;
	private HumanPlayer currentPlayer;
	private QwirkleServer server;
	
	/**
	 * Creates a new instance of a client handler, using the given socket. 
	 * @param socket The socket that is used for read and write operations.
	 * @throws IOException 
	 */
	public ClientHandler(QwirkleServer server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	/**
	 * Gets the name of the client.
	 * @return the name.
	 */
	public String getClientName() {
		return clientName;
	}
	
	/**
	 * Starts to process all incoming traffic. 
	 */
	@Override
    public void run() {
		String line;
		try {
			while ((line = in.readLine()) != null) {
				processCommand(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	private void processCommand(String line) {
		System.out.println(line);
		try (Scanner scanner = new Scanner(line)) {
			switch (scanner.next()) {
			case Protocol.CLIENT_JOINREQUEST:
				clientName = scanner.next();
				// TODO: check if name exists or not.
				acceptJoinRequest();
				break;
			case Protocol.CLIENT_GAMEREQUEST:
				handleGameRequestCommand(scanner);
				break;
			case Protocol.CLIENT_SETMOVE:
				handleSetMoveCommand(scanner);
				break;
			case Protocol.CLIENT_DOTRADE:
				handleDoTradeCommand(scanner);
				break;
			}
		}		
	}

	private void handleDoTradeCommand(Scanner scanner) {
		List<Stone> stones = new ArrayList<Stone>();
		int amount = scanner.nextInt();

		Board board = currentGame.getBoard();
		for (int i = 0; i < amount; i++) {
			stones.add(board.pickStone());
			board.placeStoneInBag(Stone.fromScanner(scanner));
		}
		
		giveStones(stones);
	}

	private void handleSetMoveCommand(Scanner scanner) {
		List<Move> moves = new ArrayList<Move>();
		int amount = scanner.nextInt();
		
		for (int i = 0; i < amount; i++) {
			moves.add(Move.fromScanner(scanner));
		}
	
		server.broadcastMove(getCurrentGame(), this, moves);
	}

	private void handleGameRequestCommand(Scanner scanner) {
		int desiredPlayers = scanner.nextInt();
		server.requestGame(this, desiredPlayers);
	}

	/**
	 * Notifies the client is accepted by the server.
	 */
	public void acceptJoinRequest() {
		try {
			out.write(Protocol.SERVER_ACCEPTREQUEST + " 0 0 0 0");
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Notifies a game is started with the given players.
	 * @param names The names of the players the client will be playing against.
	 */
	public void startGame(List<String> names) {
		try {
			out.write(Protocol.SERVER_STARTGAME);
			for (String name : names) {
				out.write(" ");
				out.write(name);
			}
			
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gives stones to the client.
	 * @param stones The stones to give.
	 */
	public void giveStones(List<Stone> stones) {
		
		currentPlayer.getStones().addAll(stones);
		
		try {
			out.write(Protocol.SERVER_GIVESTONES);
			out.write(" ");
			out.write(Integer.toString(stones.size()));
			
			for (Stone s : stones) {
				out.write(" ");
				out.write(Stone.shapeToString(s.getShape()));
				out.write(" ");
				out.write(Stone.colorToString(s.getColor()));
			}
			
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Requests the client to make a move.
	 */
	public void requestMove() {
		// TODO: implement body.
	}
	
	/**
	 * Notifies the client a move has been made by the given player.
	 * @param name The name of the player that made the move.
	 * @param score The score the player has earned using the move. 
	 * @param stonePlacements The placements of the stones.
	 */
	public void notifyMove(String name, int score, List<Move> stonePlacements) {

		try {
			out.write(Protocol.SERVER_NOTIFYMOVE);
			out.write(" ");
			out.write(name);
			out.write(" ");
			out.write(Integer.toString(score));
			out.write(" ");
			out.write(Integer.toString(stonePlacements.size()));
			
			for (Move move : stonePlacements) {
				out.write(" ");
				out.write(Stone.shapeToString(move.getStone().getShape()));
				out.write(" ");
				out.write(Stone.colorToString(move.getStone().getColor()));
				out.write(" ");
				out.write(Integer.toString(move.getLocation().getX()));
				out.write(" ");
				out.write(Integer.toString(move.getLocation().getY()));
			}
			
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Notifies the client a player has traded a specific amount of stones. 
	 * @param name The name of the player that traded stones.
	 * @param stoneCount The amount of stones that were traded.
	 */
	public void notifyTrade(String name, int stoneCount) {
		// TODO: implement body.
	}
	
	/**
	 * Notifies the client the game is over.
	 * @param players The list of players in the game. 
	 */
	public void notifyGameOver(List<Player> players) {
		// TODO: implement body.
	}
	
	/**
	 * Sends the client an invalid command was sent or an error occured.
	 * @param reason The reason of the error that occured
	 */
	public void sendInvalidCommandError(String reason) {
		// TODO: implement body.
	}
	
	/**
	 * Notifies the client a specific player disconnected from the game.
	 * @param name The name of the player that disconnected.
	 */
	public void notifyConnectionLost(String name) {
		// TODO: implement body.
	}

	public Game getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}

	public HumanPlayer getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(HumanPlayer  currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
}
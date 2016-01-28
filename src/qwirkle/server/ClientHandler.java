package qwirkle.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import qwirkle.Board;
import qwirkle.Game;
import qwirkle.Move;
import qwirkle.MoveResult;
import qwirkle.Player;
import qwirkle.Protocol;
import qwirkle.Stone;

/**
 * Represents a client handler that handles traffic between the server and a connected client. 
 * @author Jerre
 *
 */
public class ClientHandler extends Thread {
	
	private Lock makeMoveLock = new ReentrantLock();
	private Condition moveMade = makeMoveLock.newCondition();
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;
	private Game currentGame;
	private ClientPlayer currentPlayer;
	private QwirkleServer server;
	private boolean supportsChatMessages;
	
	/**
	 * Creates a new instance of a client handler, using the given socket. 
	 * @param socket The socket that is used for read and write operations.
	 * @throws IOException Occurs when the socket I/O streams cannot be created.
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
	 * Gets a value indicating whether the client supports chat messages.
	 * @return True if the client supports chat messages, false otherwise.
	 */
	public boolean getSupportsChatMessages() {
		return supportsChatMessages;
	}
	
	/**
	 * Gets the game the client is currently playing.
	 * @return the game.
	 */
	public Game getCurrentGame() {
		return currentGame;
	}

	/**
	 * Sets the game the client is currently playing.
	 * @param currentGame the game.
	 */
	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}

	/**
	 * Gets the current player the client is currently using.
	 * @return the player.
	 */
	public ClientPlayer getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Sets the current player the client is currently using.
	 * @param currentPlayer
	 */
	public void setCurrentPlayer(ClientPlayer  currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	/**
	 * Starts to process all incoming traffic. 
	 */
	@Override
    public void run() {
		String line;
		try {
			while ((line = in.readLine()) != null) {
				System.out.println(String.format("[%s (%s)]: %s", 
								getClientName(), 
								socket.getInetAddress(),
								line));
				processCommand(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally { 
			shutdown();
		}
    }

	/**
	 * Processes a single response from the server. 
	 * @param line The command of the server.
	 */
	private void processCommand(String line) {
		try (Scanner scanner = new Scanner(line)) {
			switch (scanner.next()) {
				case Protocol.CLIENT_JOINREQUEST:
					handleJoinRequestCommand(scanner);
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
				case Protocol.CLIENT_CHAT:
					handleChatCommand(scanner);
					break;
				default:
					sendInvalidCommandError("Invalid command!");
					shutdown();
					break;
			}
		}
	}

	private void handleJoinRequestCommand(Scanner scanner) {
		String name = scanner.next(); 
		if (server.getClientByName(name) == null) {
			this.clientName = name; 
			this.supportsChatMessages = scanner.nextInt() == 1;
			acceptJoinRequest();
		} else {
			sendInvalidCommandError("Username " + name + " already exists!");
		}
	}

	private void handleChatCommand(Scanner scanner) {
		// TODO: more elegant way of skipping the space.
		server.broadcastChatMessage(this, scanner.nextLine().substring(1));		
	}

	private void handleGameRequestCommand(Scanner scanner) {
		int desiredPlayers = scanner.nextInt();
		server.requestGame(this, desiredPlayers);
	}

	private void handleSetMoveCommand(Scanner scanner) {
		List<Move> moves = new ArrayList<Move>();
		int amount = scanner.nextInt();
		Board board = currentGame.getBoard();
		
		for (int i = 0; i < amount; i++) {
			moves.add(Move.fromScanner(scanner));
			if (i < amount - 1) {
				scanner.next();
			}
		}

		MoveResult result = getCurrentPlayer().placeStones(board, moves);
		if (!result.isSuccessful()) {
			sendInvalidCommandError("Invalid or illegal move!");
			shutdown();
			return;
		}
		
		List<Stone> newStones = new ArrayList<Stone>();
		for (int i = 0; i < amount && board.canPickStone(); i++) {
			newStones.add(board.pickStone());
		}
		if (newStones.size() > 0) {
			giveStones(newStones);
		}
		
		server.broadcastMove(this, result);
		this.moveMade();
	}

	private void handleDoTradeCommand(Scanner scanner) {
		int amount = scanner.nextInt();

		if (amount < 0 || amount > 6) {
			sendInvalidCommandError("Invalid amount of stones.");
			shutdown();
			return;
		}
		
		Board board = currentGame.getBoard();
		for (int i = 0; i < amount; i++) {
			Stone s = Stone.fromScanner(scanner);

			if (i < amount - 1) {
				scanner.next();
			}
			
			if (!currentPlayer.getStones().contains(s)) {
				sendInvalidCommandError("Stone " + s + " is not in " + clientName + "'s hand.");
				shutdown();
				return;
			}
			
			board.placeStoneInBag(s);
			currentPlayer.getStones().remove(s);
		}

		giveStones(currentGame.getBoard().pickStones(amount));
		server.broadcastTrade(this, amount);
		this.moveMade();
	}

	/**
	 * Notifies the client is accepted by the server.
	 */
	public synchronized void acceptJoinRequest() {
		
		try {
			out.write(Protocol.SERVER_ACCEPTREQUEST + " " + getClientName() + " 1 0 0 0");
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}
	}

	/**
	 * Notifies a game is started with the given players.
	 * @param names The names of the players the client will be playing against.
	 */
	public synchronized void startGame(List<String> names) {
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
			shutdown();
		}
	}

	/**
	 * Gives stones to the client.
	 * @param stones The stones to give.
	 */
	public synchronized void giveStones(List<Stone> stones) {
		
		currentPlayer.getStones().addAll(stones);
		
		try {
			out.write(Protocol.SERVER_GIVESTONES);
			out.write(" ");
			out.write(Integer.toString(stones.size()));
			out.write(" ");
			
			for (int i = 0; i < stones.size(); i++) {
				Stone s = stones.get(i);
				out.write(Stone.shapeToString(s.getShape()));
				out.write(" ");
				out.write(Stone.colorToString(s.getColor()));
				
				if (i < stones.size() - 1) {
					out.write(" | ");
				}
			}
			
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}
	}
	
	/**
	 * Requests the client to make a move.
	 */
	public synchronized void requestMove() {
		try {
			out.write(Protocol.SERVER_MOVEREQUEST);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}
	}
	
	/**
	 * Notifies the client the move was made.
	 */
	public void moveMade() {
		makeMoveLock.lock();
		try {
			moveMade.signalAll();
		} finally {
			makeMoveLock.unlock();
		}
	}
	
	/**
	 * Blocks the current thread until the move was made.
	 * @throws InterruptedException Occurs when the waiting is interrupted.
	 */
	public void waitForMove() throws InterruptedException {
		makeMoveLock.lock();
		try {
			moveMade.await();
		} finally {
			makeMoveLock.unlock();
		}		
	}
	
	/**
	 * Notifies the client a move has been made by the given player.
	 * @param name The name of the player that made the move.
	 * @param score The score the player has earned using the move. 
	 * @param stonePlacements The placements of the stones.
	 */
	public synchronized void notifyMove(String name, int score, List<Move> stonePlacements) {

		try {
			out.write(Protocol.SERVER_NOTIFYMOVE);
			out.write(" ");
			out.write(name);
			out.write(" ");
			out.write(Integer.toString(score));
			out.write(" ");
			out.write(Integer.toString(stonePlacements.size()));
			out.write(" ");
			
			for (int i = 0; i < stonePlacements.size(); i++) {
				Move move = stonePlacements.get(i);
				out.write(Stone.shapeToString(move.getStone().getShape()));
				out.write(" ");
				out.write(Stone.colorToString(move.getStone().getColor()));
				out.write(" ");
				out.write(Integer.toString(move.getLocation().getX()));
				out.write(" ");
				out.write(Integer.toString(move.getLocation().getY()));
				
				if (i < stonePlacements.size() - 1) {
					out.write(" | ");
				}
			}
			
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}
	}
	
	/**
	 * Notifies the client a player has traded a specific amount of stones. 
	 * @param name The name of the player that traded stones.
	 * @param stoneCount The amount of stones that were traded.
	 */
	public synchronized void notifyTrade(String name, int stoneCount) {
		try {
			out.write(Protocol.SERVER_NOTIFYTRADE);
			out.write(" ");
			out.write(name);
			out.write(" ");
			out.write(Integer.toString(stoneCount));
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}
	}
	
	/**
	 * Notifies the client the game is over.
	 * @param players The list of players in the game. 
	 */
	public synchronized void notifyGameOver(List<Player> players) {
		try {
			out.write(Protocol.SERVER_GAMEOVER);
			out.write(" ");

			for (int i = 0; i < players.size(); i++) {
				Player p = players.get(i);
				out.write(p.getName());
				out.write(" ");
				out.write(Integer.toString(p.getScore()));
				if (i < players.size() - 1) {
					out.write(" | ");
				}
			}
			
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}
	}
	
	/**
	 * Sends the client an invalid command was sent or an error occured.
	 * @param reason The reason of the error that occured
	 */
	public void sendInvalidCommandError(String reason) {
		try {
			out.write(Protocol.SERVER_INVALIDCOMMAND);
			out.write(" ");
			out.write(reason);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}
	}
	
	/**
	 * Notifies the client a specific player disconnected from the game.
	 * @param name The name of the player that disconnected.
	 */
	public synchronized void notifyConnectionLost(String name) {
		try {
			out.write(Protocol.SERVER_CONNECTIONLOST);
			out.write(" ");
			out.write(name);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}
	}

	/**
	 * Notifies the client a player has sent a message.
	 * @param sender The sender of the message.
	 * @param message The message that was sent.
	 */
	public synchronized void notifyChatMessage(ClientHandler sender, String message) {
		try {
			out.write(Protocol.SERVER_CHAT);
			out.write(" ");
			out.write(sender.getClientName());
			out.write(" ");
			out.write(message);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();
		}		
	}
	
	/**
	 * Closes the connection with the client.
	 */
	public void shutdown() {

		System.out.println(getClientName() + " shut down.");
		server.broadcastConnectionLost(this);
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

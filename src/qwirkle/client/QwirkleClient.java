package qwirkle.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import qwirkle.Game;
import qwirkle.Move;
import qwirkle.MoveResult;
import qwirkle.OpenHandPlayer;
import qwirkle.Player;
import qwirkle.Protocol;
import qwirkle.RemotePlayer;
import qwirkle.Stone;
import qwirkle.View;

/**
 * Represents a client that is connected to the server.
 * @author Jerre
 *
 */
public class QwirkleClient extends Observable implements Observer {
	
	public static final String USAGE = "Usage: java " + QwirkleClient.class.toString() 
								+ " <host> <port> [-naive]";
	
	public static void main(String[] args) {
		
		if (args.length < 2) {
			System.out.println(USAGE);
			return;
		}
		
		// parse host address.
		InetAddress host;
		try {
			host = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.err.println("ERROR: no valid hostname!");
			return;
		}
		
		// parse port.
		int port;
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("ERROR: no valid portnummer!");
			return;
		}

		// parse player type (if available)
		PlayerType playerType = PlayerType.HUMAN;
		
		if (args.length > 2) {
			switch (args[2]) {
				case "-naive":
					playerType = PlayerType.NAIVE;
					break;
				case "-smart":
					playerType = PlayerType.SMART;
					break;
			}
		}
		
		// start client.
		QwirkleClient client;
		try {
			client = new QwirkleClient(host, port, playerType);
		} catch (IOException e) {
			System.err.println("ERROR: Could not create client. " + e.getMessage());
			return;
		}
		
		client.run();
	}
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private Game game;
	private View view;
	private String clientName;
	private OpenHandPlayer player;
	private boolean myTurn;
	private PlayerType playerType;
	private boolean supportsChat;
	
	public QwirkleClient(InetAddress host, int port, PlayerType playerType) throws IOException {
		this.playerType = playerType;
		this.socket = new Socket(host, port);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.view = new TUIView(this);
		this.addObserver(view);
	}
	
	/**
	 * Gets the game the client is currently playing.
	 * @return The game.
	 */
	public Game getCurrentGame() {
		return game;
	}
	
	/**
	 * Determines whether the client should make a move or not.
	 * @return True if its the client's turn, false otherwise.
	 */
	public boolean getIsMyTurn() {
		return myTurn;
	}
	
	/**
	 * Waits for the server to respond. 
	 * @return the response of the server.
	 * @throws IOException 
	 */
	private String readResponse() throws IOException {
		return in.readLine();
	}
	
	/**
	 * Starts to process all incoming traffic. 
	 */
	public void run() {
		new Thread(view).start();
		try {
			while (true) {
				String response = readResponse();
				// TODO: remove next println:
				System.out.println("[server]: " + response);
				processResponse(response);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void processResponse(String response) {
		try (Scanner scanner = new Scanner(response)) {
			switch (scanner.next()) {
				case Protocol.SERVER_ACCEPTREQUEST:
					handleAcceptRequest(scanner);
					break;
				case Protocol.SERVER_STARTGAME:
					handleStartGameCommand(scanner);
					break;			
				case Protocol.SERVER_GIVESTONES:
					handleGiveStonesCommand(scanner);
					break;
				case Protocol.SERVER_NOTIFYMOVE:
					handleNotifyMoveCommand(scanner);
					break;
				case Protocol.SERVER_NOTIFYTRADE:
					// TODO;
					break;
				case Protocol.SERVER_MOVEREQUEST:
					handleMoveRequest(scanner);
					break;
				case Protocol.SERVER_CHAT:
					handleChatCommand(scanner);
					break;
				case Protocol.SERVER_GAMEOVER:
					handleGameOverCommand(scanner);
					break;
				case Protocol.SERVER_INVALIDCOMMAND:
					handleInvalidCommand(scanner);
					break;
			}
		}
	}

	private void handleInvalidCommand(Scanner scanner) {
		view.showError(scanner.nextLine());
	}

	private void handleAcceptRequest(Scanner scanner) {
		supportsChat = scanner.nextInt() == 1;
		
		setChanged();
		notifyObservers("acceptedrequest");
	}

	private void handleGameOverCommand(Scanner scanner) {
		// TODO: replace with view notification.
		
		System.out.println("GAME OVER");
		
		while (scanner.hasNext()) {
			String name = scanner.next();
			int score = scanner.nextInt();

			System.out.println("Player: " + name);
			System.out.println("Score: " + score);
			
			// ignore next pipe.
			if (scanner.hasNext()) {
				scanner.next();
			}
		}
		
	}

	private void handleChatCommand(Scanner scanner) {
		//
	}

	private void handleNotifyMoveCommand(Scanner scanner) {
		List<Move> moves = new ArrayList<Move>();
		
		String name = scanner.next();
		int score = scanner.nextInt();
		int amount = scanner.nextInt();
		
		for (int i = 0; i < amount; i++) {
			moves.add(Move.fromScanner(scanner));
		}
				
		for (Player p : game.getPlayers()) {
			if (p.getName().equals(name)) {
				RemotePlayer remotePlayer = (RemotePlayer) p;
				remotePlayer.notifyPlacedStones(game.getBoard(), moves, score);
				break;
			}
		}
	}

	private void handleGiveStonesCommand(Scanner scanner) {
		List<Stone> stones = new ArrayList<Stone>();
		int amount = scanner.nextInt();
		
		for (int i = 0; i < amount; i++) {
			stones.add(Stone.fromScanner(scanner));
			game.getBoard().pickStone();
		}
		
		player.giveStones(stones);
	}

	private void handleStartGameCommand(Scanner scanner) {
		List<Player> players = new ArrayList<Player>();
		while (scanner.hasNext()) {
			String name = scanner.next();
			if (name.equals(clientName)) {
				switch (playerType) {
					case HUMAN:
						player = new HumanPlayer(name);
						break;
					case NAIVE:
						player = new ComputerPlayer(name, new NaiveStrategy());
						break;
					case SMART:
						player = new ComputerPlayer(name, new SmartStrategy());
						break;
				}
				players.add(player);
			} else { 
				RemotePlayer remote = new RemotePlayer(name);
				remote.setHandSize(6);
				players.add(remote); 
			}
		}
		
		game = new Game(players);
		game.getBoard().addObserver(view);
		
		for (Player p : game.getPlayers()) {
			p.addObserver(view);
			p.addObserver(this);
			
			if (p instanceof RemotePlayer) {
				game.getBoard().pickStones(6);
				
			}
		}
		
		setChanged();
		notifyObservers("gamestarted");
		
	}
	
	private void handleMoveRequest(Scanner scanner) {
		myTurn = true;
		player.makeMove(game.getBoard());
	}
	
	/**
	 * Requests to join the server.
	 * @param playerName The name of the joining player.
	 */
	public synchronized void requestJoin(String playerName) {
		this.clientName = playerName;
		try {
			out.write(Protocol.CLIENT_JOINREQUEST + " " + playerName + " 0 0 0 0");
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Requests the server to create a game with a given amount of players.
	 * @param playerCount The number of players. 
	 */
	public synchronized void requestGame(int playerCount) {
		try {
			out.write(Protocol.CLIENT_GAMEREQUEST + " " + playerCount);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Requests the server to make a move using the given stones and their placements.
	 * @param stonePlacements The stones to place.
	 */
	public synchronized void setMove(List<Move> stonePlacements) {
		myTurn = false;
		
		try {
			out.write(Protocol.CLIENT_SETMOVE);
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
	 * Requests the server to trade the given stones.
	 * @param stones The stones to trade.
	 */
	public synchronized void doTrade(List<Stone> stones) {
		myTurn = false;
		
		try {
			out.write(Protocol.CLIENT_DOTRADE);
			out.write(" ");
			out.write(Integer.toString(stones.size()));
			
			for (Stone s: stones) {
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

	public synchronized void sendChatMessage(String message) {
		if (!supportsChat) {
			view.showError("Server doesn't support chat messages!");
			return;
		}
		
		try {
			out.write(Protocol.CLIENT_CHAT);
			out.write(" ");
			out.write(message);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the connection with the server.
	 */
	public void shutDown() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public OpenHandPlayer getPlayer() {
		return player;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof MoveResult) {
			MoveResult result = (MoveResult) arg;
			setMove(result.getMoves());
		} else if (arg instanceof List<?>) {
			// TODO: more robust check
			doTrade((List<Stone>)arg);
		}
		
	}
}

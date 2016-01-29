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
	
	//@ private invariant USAGE != null;
	public static final String USAGE = "Usage: java " + QwirkleClient.class.toString() 
								+ " <host> <port> [<playertype>]";
	
	//@ requires args != null;
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
				case "-human":
					playerType = PlayerType.HUMAN;
					break;
				case "-naive":
					playerType = PlayerType.NAIVE;
					break;
				case "-smart":
					playerType = PlayerType.SMART;
					break;
				default:
					System.out.println("Invalid player type!");
					return;
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

	//@ private invariant socket != null;
	private Socket socket;
	//@ private invariant in != null;
	private BufferedReader in;
	//@ private invariant out != null;
	private BufferedWriter out;
	//@ private invariant view != null;
	private View view;
	
	private String clientName;
	private OpenHandPlayer player;
	private PlayerType playerType;
	private Game game;
	private boolean myTurn;
	private boolean supportsChat;
	private boolean isShuttingDown;
	
	/**
	 * Creates a new client.
	 * @param host The host of the server.
	 * @param port The port number to use for communication with the server..
	 * @param playerType The player type.
	 * @throws IOException Occurs when the creation of the communication socket fails.
	 */
	//@ requires host != null && 0 <= port && port <= 65536;
	public QwirkleClient(InetAddress host, int port, PlayerType playerType) throws IOException {
		this.playerType = playerType;
		this.socket = new Socket(host, port);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.view = new TUIView(this);
		this.addObserver(view);
	}

	/**
	 * Gets the client's name.
	 * @return the name, or null if no name was accepted yet.
	 */
	//@ pure
	public String getClientName() {
		return clientName;
	}
	
	//@ pure
	public boolean getSupportsChat() {
		return supportsChat;
	}
	
	/**
	 * Gets the game the client is currently playing.
	 * @return The game.
	 */
	//@ pure
	public Game getCurrentGame() {
		return game;
	}

	/**
	 * Gets the player the client is currently playing as.
	 * @return The player.
	 */
	//@ requires getCurrentGame() != null;
	//@ ensures getCurrentGame().getPlayers().contains(\result);
	//@ ensures \result.getName() == getClientName();
	//@ pure
	public OpenHandPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Determines whether the client should make a move or not.
	 * @return True if its the client's turn, false otherwise.
	 */
	//@ requires getCurrentGame() != null;
	//@ pure
	public boolean getIsMyTurn() {
		return myTurn;
	}
		
	/**
	 * Starts to process all incoming traffic. 
	 */
	public void run() {
		new Thread(view).start();
		try {
			while (true) {
				String response = in.readLine();
				processResponse(response);
			}
		} catch (IOException e) {
			if (!isShuttingDown) {
				e.printStackTrace();
				shutDown();
			}
		}
	}
	
	//@ requires response != null;
	private void processResponse(String response) {
		try (Scanner scanner = new Scanner(response)) {
			if (!scanner.hasNext()) {
				return;
			}
			
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
					handleNotifyTradeCommand(scanner);
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
				case Protocol.SERVER_CONNECTIONLOST:
					handleConnectionLostCommand(scanner);
					break;
			}
		}
	}

	//@ requires scanner != null;
	private void handleConnectionLostCommand(Scanner scanner) {
		String name = scanner.next();
		view.notifyConnectionLost(name);
	}

	//@ requires scanner != null;
	private void handleInvalidCommand(Scanner scanner) {
		view.showError(scanner.nextLine());
	}

	//@ requires scanner != null;
	private void handleAcceptRequest(Scanner scanner) {
		clientName = scanner.next();
		supportsChat = scanner.nextInt() == 1;
		
		setChanged();
		notifyObservers("acceptedrequest");
	}

	//@ requires getCurrentGame() != null && scanner != null;
	private void handleGameOverCommand(Scanner scanner) {		
		List<Player> ranking = new ArrayList<Player>(); 
		while (scanner.hasNext()) {
			String name = scanner.next();
			int score = scanner.nextInt();

			Player p = game.getPlayerByName(name);
			p.setScore(score);
			ranking.add(p);
			
			// ignore next pipe.
			if (scanner.hasNext()) {
				scanner.next();
			}
		}

		view.notifyGameOver(ranking);
		
		setChanged();
		notifyObservers("gameover");
	}

	//@ requires scanner != null;
	private void handleChatCommand(Scanner scanner) {
		String sender = scanner.next();
		String message = scanner.nextLine().substring(1);
		view.showChatMessage(sender, message);
	}

	//@ requires getCurrentGame() != null && scanner != null;
	private void handleNotifyMoveCommand(Scanner scanner) {
		List<Move> moves = new ArrayList<Move>();
		
		String name = scanner.next();
		int score = scanner.nextInt();
		int amount = scanner.nextInt();
		
		for (int i = 0; i < amount; i++) {
			moves.add(Move.fromScanner(scanner));
			if (i < amount - 1) {
				scanner.next();
			}
		}
				
		Player sender = game.getPlayerByName(name);		
		if (sender instanceof RemotePlayer) { 
			RemotePlayer remotePlayer = (RemotePlayer) sender;
			remotePlayer.notifyPlacedStones(game.getBoard(), moves, score);
		}		
		view.notifyMove(sender, moves, score);
	}

	//@ requires getCurrentGame() != null && scanner != null;
	private void handleNotifyTradeCommand(Scanner scanner) {
		String name = scanner.next();
		int stones = scanner.nextInt();
		Player sender = game.getPlayerByName(name);		
		view.notifyTrade(sender, stones);
	}

	//@ requires getCurrentGame() != null && scanner != null;
	private void handleGiveStonesCommand(Scanner scanner) {
		List<Stone> stones = new ArrayList<Stone>();
		int amount = scanner.nextInt();
		
		for (int i = 0; i < amount; i++) {
			stones.add(Stone.fromScanner(scanner));
			
			if (game.getBoard().canPickStone()) {
				game.getBoard().pickStone();
			}
			
			if (i < amount - 1) {
				scanner.next();
			}
		}
		
		player.giveStones(stones);
	}

	//@ requires scanner != null;
	//@ ensures getCurrentGame() != null;
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

	//@ requires getCurrentGame() != null && scanner != null;
	private void handleMoveRequest(Scanner scanner) {
		myTurn = true;
		player.makeMove(game.getBoard());
	}
	
	/**
	 * Requests to join the server.
	 * @param playerName The name of the joining player.
	 */
	//@ requires playerName != null;
	//@ ensures getClientName() == playerName;
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
	//@ requires 1 <= playerCount && playerCount <= 4;
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
	 * @param moves The stones to place.
	 */
	//@ requires getCurrentGame() != null;
	//@ requires moves.size() > 0 && getCurrentGame().getBoard().checkMoves(moves);
	//@ ensures getIsMyTurn() == false;
	public synchronized void setMove(List<Move> moves) {
		myTurn = false;
		
		try {
			out.write(Protocol.CLIENT_SETMOVE);
			out.write(" ");
			out.write(Integer.toString(moves.size()));
			out.write(" ");
			
			for (int i = 0; i < moves.size(); i++) {
				Move move = moves.get(i);
				out.write(Stone.shapeToString(move.getStone().getShape()));
				out.write(" ");
				out.write(Stone.colorToString(move.getStone().getColor()));
				out.write(" ");
				out.write(Integer.toString(move.getLocation().getX()));
				out.write(" ");
				out.write(Integer.toString(move.getLocation().getY()));
				
				if (i < moves.size() - 1) {
					out.write(" | ");
				}
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
	//@ requires getCurrentGame() != null && stones != null && stones.size() > 0;
	//@ ensures getIsMyTurn() == false;
	public synchronized void doTrade(List<Stone> stones) {
		myTurn = false;
		
		try {
			out.write(Protocol.CLIENT_DOTRADE);
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
		}
	}

	/**
	 * Sends a chat message to the server.
	 * @param message
	 */
	//@ requires message != null;
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
		isShuttingDown = true;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof MoveResult) {
			MoveResult result = (MoveResult) arg;
			setMove(result.getMoves());
		} else if (arg instanceof List<?>) {
			// TODO: more robust check
			doTrade((List<Stone>) arg);
		}
		
	}
}

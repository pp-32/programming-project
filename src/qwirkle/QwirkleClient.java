package qwirkle;

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
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

/**
 * Represents a client that is connected to the server.
 * @author Jerre
 *
 */
public class QwirkleClient extends Observable {
	
	public static final String USAGE = "Usage: java " + QwirkleClient.class.toString() + " <host> <port>";
	
	public static void main(String[] args) {
		
		if (args.length != 2) {
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
		
		// create socket.
		QwirkleClient client;
		try {
			client = new QwirkleClient(host, port);
		} catch (IOException e) {
			System.err.println("ERROR: Could not create socket. " + e.getMessage());
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

	// TODO: remove.
	public QwirkleClient() {
		view = new TUIView(this);
	}
	
	public QwirkleClient(InetAddress host, int port) throws IOException {
		socket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		view = new TUIView(this);
		addObserver(view);
	}
	
	public Game getCurrentGame() {
		return game;
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
				System.out.println(response);
				processResponse(response);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void processResponse(String response) {
		try (Scanner scanner = new Scanner(response)) {
			switch(scanner.next()) {
			case Protocol.SERVER_ACCEPTREQUEST:
				// TODO accept
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
			}
		}
	}
	
	private void handleNotifyMoveCommand(Scanner scanner) {
		List<Move> moves = new ArrayList<Move>();
		
		String name = scanner.next();
		int score = scanner.nextInt();
		int amount = scanner.nextInt();
		
		for (int i = 0; i < amount; i++) {
			moves.add(Move.fromScanner(scanner));
		}
		
		game.getBoard().placeStones(moves);
		
		for (Player p : game.getPlayers()) {
			if (p.getName().equals(name)) {
				p.setScore(p.getScore() + score);
				break;
			}
		}
	}

	private void handleGiveStonesCommand(Scanner scanner) {
		List<Stone> stones = new ArrayList<Stone>();
		int amount = scanner.nextInt();
		
		for (int i = 0; i < amount; i++) {
			stones.add(Stone.fromScanner(scanner));
		}
		
		game.getHumanPlayer().giveStones(stones);
	}

	private void handleStartGameCommand(Scanner scanner) {
		List<Player> players = new ArrayList<Player>();
		while (scanner.hasNext()) {
			String name = scanner.next();
			if (name.equals(clientName)) {
				players.add(new HumanPlayer(name));
			} else { 
				players.add(new Player(name)); 
			}
		}
		
		game = new Game(players);
		game.getBoard().addObserver(view);
		game.getHumanPlayer().addObserver(view);
		setChanged();
		notifyObservers(game);
	}
	
	/**
	 * Requests to join the server.
	 * @param playerName The name of the joining player.
	 */
	public void requestJoin(String playerName) {
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
	public void requestGame(int playerCount) {
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
	public void setMove(List<Move> stonePlacements) {
	
		if (game.getBoard().checkMoves(stonePlacements)) {
			game.getHumanPlayer().placeStones(game.getBoard(), stonePlacements);
			
		} else {
			view.showError("Invalid move!");
			return;
		}
		
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
	public void doTrade(List<Stone> stones) {
		game.getHumanPlayer().stones.removeAll(stones);
		
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
}

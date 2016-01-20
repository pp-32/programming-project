package qwirkle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * Represents a client that is connected to the server.
 * @author Jerre
 *
 */
public class QwirkleClient extends Thread {
	
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

	// TODO: remove.
	public QwirkleClient() {
		view = new TUIView(this);
	}
	
	public QwirkleClient(InetAddress host, int port) throws IOException {
		socket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		view = new TUIView(this);
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
	@Override
	public void run() {
		new Thread(view).start();
		try {
			while (true) {
				String response = readResponse();
				new ServerHandler(response, this).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Requests to join the server.
	 * @param playerName The name of the joining player.
	 */
	public void requestJoin(String playerName) {
		try {
			out.write(Protocol.CLIENT_JOINREQUEST + " " + playerName + " 0 0 0 0");
			out.newLine();
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Requests the server to make a move using the given stones and their placements.
	 * @param stonePlacements The stones to place.
	 */
	public void setMove(List<Move> stonePlacements) {

		try {
			out.write(Protocol.CLIENT_SETMOVE);
			out.write(" ");
			out.write(stonePlacements.size());
			out.write(" ");
			
			for (Move move : stonePlacements) {
				out.write(shapeToId(move.getStone().getShape()));
				out.write(" ");
				out.write(colorToId(move.getStone().getColor()));
				out.write(" ");
				out.write(move.getLocation().getX());
				out.write(" ");
				out.write(move.getLocation().getY());
			}
			
			out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int shapeToId(StoneShape shape) {
		int shapeId = 0;
		switch (shape) {
		case CIRCLE:
			shapeId = Protocol.CIRCLE;
			break;
		case CROSS:
			shapeId = Protocol.CROSS;
			break;
		case DIAMOND:
			shapeId = Protocol.DIAMOND;
			break;
		case CLUBS:
			shapeId = Protocol.CLUBS;
			break;
		case RECTANGLE:
			shapeId = Protocol.RECTANGLE;
			break;
		case STAR:
			shapeId = Protocol.STAR;
			break;
		}
		return shapeId;
	}
	
	private int colorToId(StoneColor color) {
		int colorId = 0;
		switch (color) {
		case BLUE:
			colorId = Protocol.BLUE;
			break;
		case GREEN:
			colorId = Protocol.GREEN;
			break;
		case ORANGE:
			colorId = Protocol.ORANGE;
			break;
		case PURPLE:
			colorId = Protocol.PURPLE;
			break;
		case RED:
			colorId = Protocol.RED;
			break;
		case YELLOW:
			colorId = Protocol.YELLOW;
			break;
		}
		return colorId;
	}
	
	/**
	 * Requests the server to trade the given stones.
	 * @param stones The stones to trade.
	 */
	public void doTrade(List<Stone> stones) {
		// TODO: Implement body.
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

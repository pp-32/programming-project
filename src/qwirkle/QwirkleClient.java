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
		
		new QwirkleClient().run();
		
//		if (args.length != 2) {
//			System.out.println(USAGE);
//			return;
//		}
//		
//		// parse host address.
//		InetAddress host;
//		try {
//			host = InetAddress.getByName(args[1]);
//		} catch (UnknownHostException e) {
//			System.err.println("ERROR: no valid hostname!");
//			return;
//		}
//		
//		// parse port.
//		int port;
//		try {
//			port = Integer.parseInt(args[2]);
//		} catch (NumberFormatException e) {
//			System.err.println("ERROR: no valid portnummer!");
//			return;
//		}
//		
//		// create socket.
//		QwirkleClient client;
//		try {
//			client = new QwirkleClient(host, port);
//		} catch (IOException e) {
//			System.err.println("ERROR: Could not create socket. " + e.getMessage());
//			return;
//		}
//		
//		client.start();
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
		// TODO: Implement body.
	}
	
	/**
	 * Requests the server to create a game with a given amount of players.
	 * @param playerCount The number of players. 
	 */
	public void requestGame(int playerCount) {
		// TODO: Implement body.
	}
	
	/**
	 * Requests the server to make a move using the given stones and their placements.
	 * @param stonePlacements The stones to place.
	 */
	public void setMove(List<Move> stonePlacements) {
		
		
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

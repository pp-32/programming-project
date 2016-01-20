package qwirkle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Represents a server running the Qwirkle game.
 * @author Jerre
 *
 */
public class QwirkleServer extends Thread {

	public static final String USAGE = "Usage: java " + QwirkleServer.class.toString() + " <port>";
	
	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println(USAGE);
			return;
		}
		
		// parse port.
		int port;
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("ERROR: no valid portnummer!");
			return;
		}
		
		// create socket.
		QwirkleServer server;
		try {
			server = new QwirkleServer(port);
		} catch (IOException e) {
			System.err.println("ERROR: Could not create socket. " + e.getMessage());
			return;
		}
		
		server.run();
	}
	
	private ServerSocket socket;
	private int port;
	private ServerSocket serverSocket;
	private List<ClientHandler> connectedClients;
	
	public QwirkleServer(int port) throws IOException {
		this.port = port;
		this.socket = new ServerSocket(port);
	}
	
	public int getPort() {
		return port;
	}

	/**
	 * Starts listening to clients.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Socket client = socket.accept();
				System.out.println("Client connected!");
				ClientHandler handler = new ClientHandler(client);
				handler.start();
			} catch (IOException e)  {
				e.printStackTrace();
			}
		}
	}


}

package qwirkle;

import java.net.ServerSocket;
import java.util.List;

/**
 * Represents a server running the Qwirkle game.
 * @author Jerre
 *
 */
public class QwirkleServer extends Thread {

	private int port;
	private ServerSocket serverSocket;
	private List<ClientHandler> connectedClients;
	
	public QwirkleServer(int port) {
		this.port = port;
		
	}
	
	public int getPort() {
		return port;
	}

	/**
	 * Starts listening to clients.
	 */
	@Override
	public void run() {
		
	}


}

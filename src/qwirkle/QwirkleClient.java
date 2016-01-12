package qwirkle;

import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Represents a client that is connected to the server.
 * @author Jerre
 *
 */
public class QwirkleClient extends Thread {
	
	private Socket socket;

	public QwirkleClient(Socket socket) {
		this.socket = socket;
		
	}
	
	private String readResponse() {
		// TODO: read from input stream.
	}
	
	/**
	 * Starts to process all incoming traffic. 
	 */
	@Override
	public void run() {
		while (true) {
			String response = readResponse();
			new ServerHandler(response, this).start();
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
	public void setMove(Map<Stone, Location> stonePlacements) {
		// TODO: Implement body.
	}
	
	/**
	 * Requests the server to trade the given stones.
	 * @param stones The stones to trade.
	 */
	public void doTrade(List<Stone> stones) {
		// TODO: Implement body.
	}

}

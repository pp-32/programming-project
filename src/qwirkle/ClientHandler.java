package qwirkle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
	
	/**
	 * Creates a new instance of a client handler, using the given socket. 
	 * @param socket The socket that is used for read and write operations.
	 * @throws IOException 
	 */
	public ClientHandler(Socket socket) throws IOException {
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
				break;
			}
		}		
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
		// TODO: implement body.
	}

	/**
	 * Gives stones to the client.
	 * @param stones The stones to give.
	 */
	public void giveStones(List<Stone> stones) {
		// TODO: implement body.
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
	public void notifyMove(String name, int score, Map<Stone, Location> stonePlacements) {
		// TODO: implement body.
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
}

package qwirkle.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import qwirkle.Board;
import qwirkle.Game;
import qwirkle.MoveResult;
import qwirkle.Player;
import qwirkle.ScoreComparator;

/**
 * Represents a server running the Qwirkle game.
 * @author Jerre
 *
 */
public class QwirkleServer extends Thread implements Observer {

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
		
		try {
			server.run();
		} finally {
			server.close();
		}
	}
	
	private ServerSocket socket;
	private int port;
	private ServerSocket serverSocket;
	private List<ClientHandler> connectedClients;
	private List<GameRequest> pendingRequests;
	private List<Game> currentGames;
	
	/**
	 * Creates a new Qwirkle server using the given port.
	 * @param port The port.
	 * @throws IOException Occurs when the socket could not be created on the given port.
	 */
	public QwirkleServer(int port) throws IOException {
		this.connectedClients = new ArrayList<ClientHandler>();
		this.pendingRequests = new ArrayList<GameRequest>();
		this.currentGames = new ArrayList<Game>();
		this.port = port;
		this.socket = new ServerSocket(port);
	}
	
	/**
	 * Gets the port the server socket is using.
	 * @return the port.
	 */
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
				ClientHandler handler = new ClientHandler(this, client);
				connectedClients.add(handler);
				handler.start();
			} catch (IOException e)  {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends a request for a game with a given amount of players.   
	 * @param clientHandler The client requesting the game.
	 * @param desiredPlayers The amount of players in the game.
	 */
	public void requestGame(ClientHandler clientHandler, int desiredPlayers) {
		
		GameRequest request = null;
		for (GameRequest r : pendingRequests) {
			if (r.getDesiredPlayers() == desiredPlayers) {
				request = r;
				break;
			}
		}
		
		if (request == null) {
			request = new GameRequest(desiredPlayers);
			pendingRequests.add(request);
		}
		
		request.getClients().add(clientHandler);
		
		if (request.isFull()) {
			pendingRequests.remove(request);
			Game game = request.createGame();
			currentGames.add(game);
			
			game.getBoard().addObserver(this);
			game.addObserver(this);
			new Thread(game).start();
		}
	}

	/**
	 * Gets a connected client by its name.
	 * @param name the name of the client.
	 * @return The client, or null if no client exists with the specified name.
	 */
	public ClientHandler getClientByName(String name) {
		for (ClientHandler handler : connectedClients) {
			if (name.equals(handler.getClientName())) {
				return handler;
			}
		}
		return null;
	}
	
	/**
	 * Broadcasts to everyone in the game the client has made a move. 
	 * @param sender The client that made the move.
	 * @param moves The moves the client made.
	 */
	public void broadcastMove(ClientHandler sender, MoveResult result) {
		Game game = sender.getCurrentGame();
		for (ClientHandler client : connectedClients) {
			if (client.getCurrentGame() == game) { // client != sender
				client.notifyMove(sender.getClientName(), result.getScore(), result.getMoves());
			}
		}
	}

	/**
	 * Broadcasts to everyone in the game the client has made a trade.
	 * @param sender The client that made the trade.
	 * @param amount The amount of stones that were traded.
	 */
	public void broadcastTrade(ClientHandler sender, int amount) {
		Game game = sender.getCurrentGame();
		for (ClientHandler client : connectedClients) {
			if (client.getCurrentGame() == game) { // client != sender
				client.notifyTrade(sender.getClientName(), amount);
			}
		}
	}

	/**
	 * Broadcasts to everyone in the game the connection
	 * with a client was lost.
	 * @param sender The disconnected client. 
	 */
	public void broadcastConnectionLost(ClientHandler sender) {
		
		// remove client from connected clients 
		connectedClients.remove(sender);
		
		// remove client from pending requests.
		for (GameRequest request : pendingRequests) {
			if (request.getClients().contains(sender)) {
				request.getClients().remove(sender);
				break;
			}
		}
		
		// notify clients in same game.
		Game game = sender.getCurrentGame();
		for (ClientHandler client : connectedClients) {
			if (client.getCurrentGame() == game) {
				client.notifyConnectionLost(sender.getClientName());
			}
		}
		
		currentGames.remove(game);
	}

	/**
	 * Broadcasts to everyone in the same game a chat message.
	 * @param sender The sender of the chat message.
	 * @param message The message.
	 */
	public void broadcastChatMessage(ClientHandler sender, String message) {
		Game game = sender.getCurrentGame();
		for (ClientHandler client : connectedClients) {
			if (client.getCurrentGame() == game) {
				client.notifyChatMessage(sender, message);
			}
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Game && arg1 instanceof String) {
			Game game = (Game) arg0;
			switch ((String) arg1) {
				case "gameover":
					List<Player> sortedList = new ArrayList<Player>();
					sortedList.addAll(game.getPlayers());
					sortedList.sort(new ScoreComparator());
					
					for (Player p : game.getPlayers()) {
						ClientHandler client = getClientByName(p.getName());
						client.notifyGameOver(sortedList);
					}
					
					currentGames.remove(game);
					break;
			}
		}
		
	}
	
	/**
	 * Closes the server.
	 */
	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

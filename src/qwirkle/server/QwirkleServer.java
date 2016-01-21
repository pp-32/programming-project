package qwirkle.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import qwirkle.Board;
import qwirkle.Game;
import qwirkle.Move;

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
		
		server.run();
	}
	
	private ServerSocket socket;
	private int port;
	private ServerSocket serverSocket;
	private List<ClientHandler> connectedClients;
	private List<GameRequest> pendingRequests;
	private List<Game> currentGames;
	
	public QwirkleServer(int port) throws IOException {
		this.connectedClients = new ArrayList<ClientHandler>();
		this.pendingRequests = new ArrayList<GameRequest>();
		this.currentGames = new ArrayList<Game>();
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
				ClientHandler handler = new ClientHandler(this, client);
				connectedClients.add(handler);
				handler.start();
			} catch (IOException e)  {
				e.printStackTrace();
			}
		}
	}

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
			Game game = request.createAndStartGame();
			currentGames.add(game);
			
			game.getBoard().addObserver(this);
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Board) {
			System.out.println(((Board)arg0).toString());
		}
		
	}

	public void broadcastMove(Game game, ClientHandler sender, List<Move> moves) {
		game.getBoard().placeStones(moves);
		for (ClientHandler client : connectedClients) {
			if (client.getCurrentGame() == game) {
				int score = 0;
				// TODO: calculate score
				client.notifyMove(sender.getClientName(), score, moves);
			}
		}
		
	}

}

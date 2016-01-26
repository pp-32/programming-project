package qwirkle.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qwirkle.Game;
import qwirkle.Player;
import qwirkle.Stone;
/**
 * Represents a request for a Qwirkle game. 
 * @author Jerre
 *
 */
public class GameRequest {
	private int desiredPlayers;
	private List<ClientHandler> clients;
	
	/**
	 * Creates a new request.
	 * @param desiredPlayers The amount of players to play with.
	 */
	public GameRequest(int desiredPlayers) {
		this.clients = new ArrayList<ClientHandler>();
		this.desiredPlayers = desiredPlayers;
	}
	
	/**
	 * Gets the amount of players the game should be played with.
	 * @return the amount of players.
	 */
	public int getDesiredPlayers() {
		return desiredPlayers;
	}

	/**
	 * Gets the clients that accepted this request.
	 * @return The clients.
	 */
	public List<ClientHandler> getClients() {
		return clients;
	}

	/**
	 * Determines whether no more clients can accept this game.
	 * @return True if no more clients can accept, false otherwise.
	 */
	public boolean isFull() {
		return getClients().size() == getDesiredPlayers();
	}
	
	/**
	 * Creates a new game with the connected players and starts it. 
	 * @return The game.
	 */
	public Game createGame() {
		Map<String, ClientPlayer> players = new HashMap<String, ClientPlayer>();
		
		for (ClientHandler client : clients) {
			players.put(client.getClientName(), new ClientPlayer(client));
		}
		
		Game game = new Game(new ArrayList<Player>(players.values()));
		
		for (ClientHandler client : clients) {
			client.setCurrentGame(game);
			client.setCurrentPlayer(players.get(client.getClientName()));
			client.startGame(new ArrayList<String>(players.keySet()));
			
			List<Stone> stones = new ArrayList<Stone>();
			for (int i = 0; i < 6; i++) {
				stones.add(game.getBoard().pickStone());
			}
			client.giveStones(stones);
		}
		
		return game;
	}
}

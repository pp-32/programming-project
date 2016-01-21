package qwirkle.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qwirkle.Game;
import qwirkle.HumanPlayer;
import qwirkle.Player;
import qwirkle.Stone;

public class GameRequest {
	private int desiredPlayers;
	private List<ClientHandler> clients;
	
	public GameRequest(int desiredPlayers) {
		this.clients = new ArrayList<ClientHandler>();
		this.desiredPlayers = desiredPlayers;
	}
	
	public int getDesiredPlayers() {
		return desiredPlayers;
	}

	public List<ClientHandler> getClients() {
		return clients;
	}

	public boolean isFull() {
		return getClients().size() == getDesiredPlayers();
	}
	
	public Game createAndStartGame() {
		Map<String, HumanPlayer> players = new HashMap<String, HumanPlayer>();
		
		for (ClientHandler client : getClients()) {
			players.put(client.getClientName(), new HumanPlayer(client.getClientName()));
		}
		
		Game game = new Game(new ArrayList<Player>(players.values()));
		
		for (ClientHandler client : getClients()) {
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

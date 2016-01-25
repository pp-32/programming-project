package qwirkle.server;

import java.util.List;

import qwirkle.Board;
import qwirkle.OpenHandPlayer;
import qwirkle.Player;
import qwirkle.Stone;

public class ClientPlayer extends OpenHandPlayer {

	private ClientHandler client;
	
	public ClientPlayer(ClientHandler client) {
		super(client.getClientName());
		this.client = client;
	}
	
	@Override
	public void makeMove(Board board) {
		client.requestMove();
		try {
			client.waitForMove();
		} catch (InterruptedException e) {
			e.printStackTrace();
			client.shutdown();
		}
	}

}

package qwirkle.server;

import qwirkle.Board;
import qwirkle.OpenHandPlayer;

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

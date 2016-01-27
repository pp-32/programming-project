package qwirkle.client;

import qwirkle.Board;
import qwirkle.OpenHandPlayer;

public class ComputerPlayer extends OpenHandPlayer {

	
	private Strategy strategy;

	public ComputerPlayer(String theName, Strategy strategy) {
		super(theName);
		this.strategy = strategy;
	}

	@Override
	public void makeMove(Board board) {
		strategy.makeMove(this, board);
	}

}

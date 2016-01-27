package qwirkle.client;

import qwirkle.Board;
import qwirkle.OpenHandPlayer;

/**
 * Represents a computer player in the Qwirkle game.
 * @author Jerre
 *
 */
public class ComputerPlayer extends OpenHandPlayer {

	private Strategy strategy;

	/**
	 * Creates a new instance of the ComputerPlayer.
	 * @param theName The name of the player.
	 * @param strategy The strategy the computer should use.
	 */
	public ComputerPlayer(String theName, Strategy strategy) {
		super(theName);
		this.strategy = strategy;
	}

	/**
	 * Gets the strategy the computer player uses.
	 * @return The strategy.
	 */
	public Strategy getStrategy() {
		return strategy;
	}

	@Override
	public void makeMove(Board board) {
		setChanged();
		notifyObservers("turnstarted");
		strategy.makeMove(this, board);
	}

}

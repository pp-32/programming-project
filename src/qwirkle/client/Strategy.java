package qwirkle.client;

import qwirkle.Board;

/**
 * Represents a strategy used by a computer player.
 * @author Jerre
 *
 */
public interface Strategy {
	
	/**
	 * Gets the name of the strategy.
	 * @return The name.
	 */
	public String getName();
	
	/**
	 * Performs the next move according to the strategy.
	 * @param player The player that should make the move.
	 * @param board The board the player should make the move on.
	 */
	public void makeMove(ComputerPlayer player, Board board);
	
}

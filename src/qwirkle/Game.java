package qwirkle;

import java.util.List;

public class Game {

	// -- Instance variables -----------------------------------------

	/**
	 * The board.
	 */
	private Board board;

	/**
	 * The players of the game.
	 */
	private List<Player> players;

	/**
	 * Index of the current player.
	 */
	private int current;

	/**
	 * Creates a new <code>Game</code> object.
	 */
	public Game() {
		board = new Board();
	}

	/**
	 * Starts the Qwirkle game. Asks after each ended game if the user wants to
	 * continue. Continues until the user does not want to play anymore.
	 */
	public void start() {

	}

	public void waitForTurn() {
		// 
	}
	
	/**
	 * Prints the game situation.
	 */
	private void update() {

	}
	
	/**
	 * 
	 */
	private void nextPlayer() {
		
	}
	
	/**
	 * Resets the game. 
	 */
	public void reset() {
		
	}
	
	/**
	 * Prints the score.
	 */
	public void updateScore() {
		
	}
	
	/**
	 * Returns true if the game is over. This is the case if a player has used
	 * up all his/her stones or if the bag is empty and no more moves can be done.
	 * 
	 * @return true if the game is over
	 */
	public boolean gameOver() {
		return false;
	}
	
	/**
	 * Checks if a player has won. A player wins if he/she has the highest score.
	 * @return
	 */
	public boolean isWinner() {
		return false;
	}

}

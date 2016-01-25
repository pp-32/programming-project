package qwirkle;

import java.util.Observable;

/**
 * Represents a player in the Qwirkle game.
 * @author Jerre
 *
 */
public abstract class Player extends Observable {

	private String name;
	private int score;

	/**
	 * Creates a new <code>Player</code> object.
	 * 
	 * @param theName
	 */

	public Player(String theName) {
		this.name = theName;
	}

	/**
	 * Returns the name of the player.
	 * 
	 * @return the player's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the current score the player has achieved during the game.
	 * @return The score.
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Updates the current score to a new value the player has achieved during the game.
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
		setChanged();
		notifyObservers("score");
	} 
	
	/**
	 * Gets the amount of stones the player has.
	 * @return The amount of stones.
	 */
	public abstract int getHandSize();
	
	/**
	 * Instructs the player to make a move.
	 * @param board The board the player should make a move on.
	 */
	public abstract void makeMove(Board board);

}

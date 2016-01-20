package qwirkle;

import java.util.List;

public abstract class Player {

	private String name;
	private int score;
	protected List<Stone> stones;

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
	 * Adds a stone to a line with stones that all have the same property (shape or color).
	 */
	public void placeStone() {

	}

	/**
	 * Picks a stone from the bag.
	 */
	public void pickStone() {

	}

	/**
	 * Swaps a stone for a different stone.
	 */
	public void discardStone() {

	}

	/*
	public void makeMove() {

	}*/

}

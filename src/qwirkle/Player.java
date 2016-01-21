package qwirkle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Player extends Observable {

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
		this.stones = new ArrayList<Stone>();
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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
		setChanged();
		notifyObservers("score");
	}

	/*
	public void makeMove() {

	}*/

}

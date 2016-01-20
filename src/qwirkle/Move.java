package qwirkle;

/** 
 * Represents a stone placement in the Qwirkle game.
 * @author Jerre
 *
 */
public class Move {
	private Stone stone;
	private Location location;

	/**
	 * Creates a new move.
	 * @param stone The stone to place. 
	 * @param location The location of the stone to place.
	 */
	public Move(Stone stone, Location location) {
		this.stone = stone;
		this.location = location;
	
	}

	/**
	 * Gets the stone to be placed.
	 * @return the stone.
	 */
	public Stone getStone() {
		return stone;
	}

	/**
	 * Gets the location of the stone to be placed.
	 * @return the location.
	 */
	public Location getLocation() {
		return location;
	}
}

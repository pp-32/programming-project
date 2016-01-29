package qwirkle;

import java.util.Scanner;

/** 
 * Represents a stone placement in the Qwirkle game.
 * @author Jerre
 *
 */
public class Move {
	
	//@ private invariant stone != null;
	private Stone stone;
	
	//@ private invariant location != null;
	private Location location;

	/**
	 * Creates a new move.
	 * @param stone The stone to place. 
	 * @param x The x-location of the stone to place.
	 * @param y The y-location of the stone to place.
	 */
	public Move(Stone stone, int x, int y) {
		this(stone, new Location(x, y));
	}
	
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
	//@ pure
	public Stone getStone() {
		return stone;
	}

	/**
	 * Gets the location of the stone to be placed.
	 * @return the location.
	 */
	//@ pure
	public Location getLocation() {
		return location;
	}
	
	@Override
	public String toString() {
		return getStone().toString() + " -> " + getLocation();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Move)) {
			return false;
		}
		Move other = (Move) obj;
		return other.getStone().equals(this.getStone()) 
			&& other.getLocation().equals(this.getLocation());
	}
	
	@Override
	public int hashCode() {
		return this.getStone().hashCode() ^ this.getLocation().hashCode();
	}
	
	/**
	 * Reads a move from a scanner.
	 * @param scanner The scanner to read from.
	 * @return The move.
	 */
	public static Move fromScanner(Scanner scanner) {
		return new Move(Stone.fromScanner(scanner), Location.fromScanner(scanner));
	}
}

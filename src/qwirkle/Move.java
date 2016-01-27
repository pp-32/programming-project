package qwirkle;

import java.util.Scanner;

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
	
	@Override
	public String toString() {
		return getStone().toString() + " -> " + getLocation();
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

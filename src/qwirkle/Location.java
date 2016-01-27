package qwirkle;

import java.util.Scanner;

/**
 * Represents a location on the Qwirkle board.
 * @author Jerre
 *
 */
public class Location {
	private int x;
	private int y;

	/**
	 * Creates a new location.
	 * @param x The x-coordinate.
	 * @param y The y-coordinate.
	 */
	public Location(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	/**
	 * Gets the X-coordinate of the location.
	 * @return the X-coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the X-coordinate of the location.
	 * @param x the new X-coordinate.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the Y-coordinate of the location.
	 * @return the Y-coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the Y-coordinate of the location.
	 * @param y the new Y-coordinate.
	 */
	public void setY(int y) {
		this.y = y;
	}
		
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	/**
	 * Adds a vector to the location.
	 * @param directionVector the vector to add.
	 */
	public void add(Location directionVector) {
		setX(getX() + directionVector.getX());
		setY(getY() + directionVector.getY());
	}

	/**
	 * Subtracts a vector from the location.
	 * @param directionVector the vector to subtract.
	 */
	public void subtract(Location directionVector) {
		setX(getX() - directionVector.getX());
		setY(getY() - directionVector.getY());
	}
	
	/**
	 * Creates a deep copy of the location.
	 * @return The copy.
	 */
	public Location deepCopy() {
		return new Location(getX(), getY());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Location)) {
			return false;
		}
		
		Location other = (Location) obj;
		return this.getX() == other.getX() && this.getY() == other.getY();
	}
	
	@Override
	public int hashCode() {
		return getX() ^ getY();
	}
	
	/**
	 * Reads a location from a scanner.
	 * @param scanner The scanner to read from.
	 * @return The location.
	 */
	public static Location fromScanner(Scanner scanner) {
		return new Location(scanner.nextInt(), scanner.nextInt());
	}
}

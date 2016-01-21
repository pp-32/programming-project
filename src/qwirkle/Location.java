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
	
	public Location deepCopy() {
		return new Location(getX(), getY());
	}

	public static Location fromScanner(Scanner scanner) {
		return new Location(scanner.nextInt(), scanner.nextInt());
	}
}

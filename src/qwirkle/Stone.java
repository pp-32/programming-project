package qwirkle;

import java.util.Scanner;

/**
 * Represents a stone in the Qwirkle game.
 * 
 * @author Jerre
 *
 */
public class Stone {
	private StoneShape shape;
	private StoneColor color;

	/**
	 * Creates a new stone with the specified shape and color.
	 * 
	 * @param shape
	 *            The shape of the stone.
	 * @param color
	 *            The color of the stone.
	 */
	public Stone(StoneShape shape, StoneColor color) {
		this.shape = shape;
		this.color = color;
	}

	/**
	 * Gets the shape of the stone.
	 * 
	 * @return The shape.
	 */
	public StoneShape getShape() {
		return shape;
	}

	/**
	 * Gets the color of the stone.
	 * 
	 * @return The color.
	 */
	public StoneColor getColor() {
		return color;
	}

	@Override
	public String toString() {
		String shapeString = null;
		switch (shape) {
			case CIRCLE:
				shapeString = "1";
				break;
			case CROSS:
				shapeString = "2";
				break;
			case DIAMOND:
				shapeString = "3";
				break;
			case CLUBS:
				shapeString = "4";
				break;
			case RECTANGLE:
				shapeString = "5";
				break;
			case STAR:
				shapeString = "6";
				break;
		}
		String colorString = null;
		switch (color) {
			case BLUE:
				colorString = "B";
				break;
			case GREEN:
				colorString = "G";
				break;
			case ORANGE:
				colorString = "O";
				break;
			case PURPLE:
				colorString = "P";
				break;
			case RED:
				colorString = "R";
				break;
			case YELLOW:
				colorString = "Y";
				break;
		}

		return shapeString + colorString;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Stone)) {
			return false;
		}
		Stone stone = (Stone) obj;
		return stone.getShape() == this.getShape() 
			&& stone.getColor() == this.getColor();	
	}
	
	@Override
	public int hashCode() {
		return getShape().hashCode() ^ getColor().hashCode();
	}
	
	/**
	 * Reads a stone from a scanner.
	 * @param scanner The scanner to read from.
	 * @return The stone that was read.
	 */
	public static Stone fromScanner(Scanner scanner) {
		return new Stone(idToShape(scanner.nextInt()), idToColor(scanner.nextInt()));
	}
	
	/**
	 * Formats a shape to a string according to the protocol.
	 * @param shape The shape to convert.
	 * @return The formatted shape.
	 */
	public static String shapeToString(StoneShape shape) {
		int shapeId = 0;
		switch (shape) {
			case CIRCLE:
				shapeId = Protocol.CIRCLE;
				break;
			case CROSS:
				shapeId = Protocol.CROSS;
				break;
			case DIAMOND:
				shapeId = Protocol.DIAMOND;
				break;
			case CLUBS:
				shapeId = Protocol.CLUBS;
				break;
			case RECTANGLE:
				shapeId = Protocol.RECTANGLE;
				break;
			case STAR:
				shapeId = Protocol.STAR;
				break;
		}
		return Integer.toString(shapeId);
	}
	
	/**
	 * Converts a formatted shape ID to a shape.
	 * @param id The shape ID.
	 * @return The shape.
	 */
	public static StoneShape idToShape(int id) {
		switch (id) {
			case Protocol.CIRCLE:
				return StoneShape.CIRCLE;
			case Protocol.CROSS:
				return StoneShape.CROSS;
			case Protocol.DIAMOND:
				return StoneShape.DIAMOND;
			case Protocol.CLUBS:
				return StoneShape.CLUBS;
			case Protocol.RECTANGLE:
				return StoneShape.RECTANGLE;
			case Protocol.STAR:
				return StoneShape.STAR;
		}
		// TODO: change to exception?
		return StoneShape.CIRCLE;
	}
	
	/**
	 * Formats a color to a string according to the protocol.
	 * @param color The color to format.
	 * @return The formatted color.
	 */
	public static String colorToString(StoneColor color) {
		int colorId = 0;
		switch (color) {
			case BLUE:
				colorId = Protocol.BLUE;
				break;
			case GREEN:
				colorId = Protocol.GREEN;
				break;
			case ORANGE:
				colorId = Protocol.ORANGE;
				break;
			case PURPLE:
				colorId = Protocol.PURPLE;
				break;
			case RED:
				colorId = Protocol.RED;
				break;
			case YELLOW:
				colorId = Protocol.YELLOW;
				break;
		}
		return Integer.toString(colorId);
	}
	
	/**
	 * Converts a color ID to an actual color.
	 * @param id The color ID.
	 * @return The converted color.
	 */
	public static StoneColor idToColor(int id) {
		switch (id) {
			case Protocol.BLUE:
				return StoneColor.BLUE;
			case Protocol.GREEN:
				return StoneColor.GREEN;
			case Protocol.ORANGE:
				return StoneColor.ORANGE;
			case Protocol.PURPLE:
				return StoneColor.PURPLE;
			case Protocol.RED:
				return StoneColor.RED;
			case Protocol.YELLOW:
				return StoneColor.YELLOW;
		}
		// TODO: change to exception?
		return StoneColor.RED;
	}
}

package qwirkle;

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
}

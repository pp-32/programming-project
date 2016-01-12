package qwirkle;

/**
 * Represents a stone in the Qwirkle game.
 * @author Jerre
 *
 */
public class Stone {
	private StoneShape shape;
	private StoneColor color;
	
	/**
	 * Creates a new stone with the specified shape and color. 
	 * @param shape The shape of the stone.
	 * @param color The color of the stone.
	 */
	public Stone(StoneShape shape, StoneColor color) {
		this.shape = shape;
		this.color = color;
	}
	
	/**
	 * Gets the shape of the stone.
	 * @return The shape.
	 */
	public StoneShape getShape() {
		return shape;
	}

	/**
	 * Gets the color of the stone.
	 * @return The color.
	 */
	public StoneColor getColor() {
		return color;
	}
	
	
}

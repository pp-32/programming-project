package qwirkle;

/**
 * Represents a rectangle.
 * @author Jerre
 *
 */
public class Rectangle {
	
	private Location topLeft;
	private Location bottomRight;

	public Rectangle(Location topLeft, Location bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}
	
	public Rectangle(int x1, int y1, int x2, int y2) {
		this(new Location(x1, y1), new Location(x2, y2));
	}

	/**
	 * Gets the top left corner of the rectangle.
	 * @return the location of the corner.
	 */
	public Location getTopLeft() {
		return topLeft;
	}

	/**
	 * Gets the bottom right corner of the rectangle.
	 * @return the location of the corner.
	 */
	public Location getBottomRight() {
		return bottomRight;
	}
	
	/**
	 * Gets the width of the rectangle.
	 * @return the width.
	 */
	public int getWidth() {
		return bottomRight.getX() - topLeft.getX();
	}
	
	/**
	 * Gets the height of the rectangle.
	 * @return the height.
	 */
	public int getHeight() {
		return bottomRight.getY() - topLeft.getY();
	}
	
	/** 
	 * Determines whether the rectangle contains the given point.
	 * @param point the point to check.
	 * @return True if the rectangle contains the point, false otherwise.
	 */
	public boolean containsPoint(Location point) {
		return point.getX() >= topLeft.getX()
			&& point.getX() <= bottomRight.getX()
			&& point.getY() <= topLeft.getY()
			&& point.getY() >= bottomRight.getY();
	}
	
	public String toString() {
		return topLeft.toString() + " => " + bottomRight.toString();
	}

	public void inflate(int i) {
		topLeft.setX(topLeft.getX() - i);
		topLeft.setY(topLeft.getY() + i);
		bottomRight.setX(bottomRight.getX() + i);
		bottomRight.setY(bottomRight.getY() - i);
	}
	
	public Rectangle deepCopy() {
		return new Rectangle(topLeft.deepCopy(), bottomRight.deepCopy());
	}
}

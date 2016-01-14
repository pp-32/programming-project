package qwirkle;

public class Rectangle {
	
	private Location topLeft;
	private Location bottomRight;

	public Rectangle(Location topLeft, Location bottomRight) {
		this.setTopLeft(topLeft);
		this.setBottomRight(bottomRight);
	}
	
	public Rectangle(int x1, int y1, int x2, int y2) {
		this(new Location(x1, y1), new Location(x2, y2));
	}

	public Location getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(Location topLeft) {
		this.topLeft = topLeft;
	}

	public Location getBottomRight() {
		return bottomRight;
	}

	public void setBottomRight(Location bottomRight) {
		this.bottomRight = bottomRight;
	}
	
	public int getWidth() {
		return bottomRight.getX() - topLeft.getX();
	}
	
	public String toString() {
		return topLeft.toString() + " => " + bottomRight.toString();
	}
	
	public int getHeight() {
		return bottomRight.getY() - topLeft.getY();
	}
}

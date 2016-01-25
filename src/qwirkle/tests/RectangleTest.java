package qwirkle.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import qwirkle.Location;
import qwirkle.Rectangle;

public class RectangleTest {

	private static final int TOP_X = 0;
	private static final int TOP_Y = 1;
	private static final int BOTTOM_X = 2;
	private static final int BOTTOM_Y = -1;

	private Rectangle rectangle;
	
	@Before
	public void setup() {
		rectangle = new Rectangle(TOP_X, TOP_Y, BOTTOM_X, BOTTOM_Y);
	}
	
	@Test
	public void testInitialCorners() {
		assertEquals(new Location(TOP_X, TOP_Y), rectangle.getTopLeft());
		assertEquals(new Location(BOTTOM_X, BOTTOM_Y), rectangle.getBottomRight());
	}

	@Test
	public void testInitialSize() {
		assertEquals(BOTTOM_X - TOP_X, rectangle.getWidth());
		assertEquals(BOTTOM_Y - TOP_Y, rectangle.getHeight());
	}
	
	@Test
	public void testContainsPoint() {
		assertTrue(rectangle.containsPoint(new Location(TOP_X, TOP_Y - 1)));
		assertFalse(rectangle.containsPoint(new Location(TOP_X, TOP_Y + 1)));
	}
	
	@Test
	public void testEquals() {
		assertTrue(rectangle.equals(new Rectangle(TOP_X, TOP_Y, BOTTOM_X, BOTTOM_Y)));
		assertFalse(rectangle.equals(new Rectangle(TOP_X, TOP_Y, BOTTOM_X, BOTTOM_Y + 1)));
	}
	
	@Test
	public void testInflate() {
		rectangle.inflate(3);
		assertEquals(new Location(TOP_X - 3, TOP_Y + 3), rectangle.getTopLeft());
		assertEquals(new Location(BOTTOM_X + 3, BOTTOM_Y - 3), rectangle.getBottomRight());
	}
	
	@Test
	public void testDeepCopy() {
		Rectangle copy = rectangle.deepCopy();
		assertEquals(rectangle, copy);
		copy.getTopLeft().setX(3);
		assertNotEquals(rectangle, copy);
	}
}

package qwirkle.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import qwirkle.Location;

public class LocationTest {

	private static final int START_X = 1;
	private static final int START_Y = 2;
	
	private Location location;
	
	@Before
	public void setup() {
		location = new Location(START_X, START_Y); 
	}
	
	@Test
	public void testInitial() {
		assertEquals(START_X, location.getX());
		assertEquals(START_Y, location.getY());
	}
	
	@Test
	public void testUpdateX() {
		assertEquals(START_X, location.getX());
		location.setX(3);
		assertEquals(3, location.getX());
	}
	
	@Test
	public void testUpdateY() {
		assertEquals(START_Y, location.getY());
		location.setY(4);
		assertEquals(4, location.getY());
	}

	@Test
	public void testEquals() {
		Location other = new Location(START_X, START_Y);
		Location other2 = new Location(START_X, START_Y + 1);
		assertTrue(location.equals(other));
		assertFalse(location.equals(other2));
		assertFalse(location.equals(null));
	}
	
	@Test
	public void testCopy() {
		Location copy = location.deepCopy();
		assertEquals(location.getX(), copy.getX());
		assertEquals(location.getY(), copy.getY());
		copy.setX(5);
		assertEquals(START_X, location.getX());
		assertEquals(5, copy.getX());
	}
	
	@Test
	public void testAdd() {
		location.add(new Location(3, 4));
		assertEquals(START_X + 3, location.getX());
		assertEquals(START_Y + 4, location.getY());
	}
	
	@Test
	public void testSubtract() {
		location.subtract(new Location(3, 4));
		assertEquals(START_X - 3, location.getX());
		assertEquals(START_Y - 4, location.getY());
	}
}

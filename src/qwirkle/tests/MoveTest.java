package qwirkle.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import qwirkle.Location;
import qwirkle.Move;
import qwirkle.Stone;
import qwirkle.StoneColor;
import qwirkle.StoneShape;

public class MoveTest {

	private Location location;
	private Stone stone;
	private Move move;
	
	@Before
	public void setup() {
		stone = new Stone(StoneShape.DIAMOND, StoneColor.BLUE);
		location = new Location(1, 2);
		move = new Move(stone, location);
	}
	
	@Test
	public void testGetLocation() {
		assertEquals(location, move.getLocation());
	}
	
	@Test
	public void testGetStone() {
		assertEquals(stone, move.getStone());
	}
	
	@Test
	public void testToString() {
		String str = move.toString();
		assertTrue(str.contains(stone.toString()));
		assertTrue(str.contains(location.toString()));
	}

}

package qwirkle.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import qwirkle.Stone;
import qwirkle.StoneColor;
import qwirkle.StoneShape;

public class StoneTest {

	private static final StoneShape SHAPE = StoneShape.DIAMOND;
	private static final StoneShape SHAPE2 = StoneShape.CIRCLE;
	private static final StoneColor COLOR = StoneColor.BLUE;
	private Stone stone;
	
	@Before
	public void setup() {
		stone = new Stone(SHAPE, COLOR);
	}
	
	@Test
	public void testInitial() {
		assertEquals(SHAPE, stone.getShape());
		assertEquals(COLOR, stone.getColor());
	}

	@Test
	public void testEquals() {
		assertEquals(stone, new Stone(SHAPE, COLOR));
		assertNotEquals(stone, new Stone(SHAPE2, COLOR));
	}

}

package qwirkle.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import qwirkle.Protocol;
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

	@Test
	public void testIdToColor() {
		assertEquals(StoneColor.BLUE, Stone.idToColor(Protocol.BLUE));
		assertEquals(StoneColor.GREEN, Stone.idToColor(Protocol.GREEN));
		assertEquals(StoneColor.ORANGE, Stone.idToColor(Protocol.ORANGE));
		assertEquals(StoneColor.PURPLE, Stone.idToColor(Protocol.PURPLE));
		assertEquals(StoneColor.RED, Stone.idToColor(Protocol.RED));
		assertEquals(StoneColor.YELLOW, Stone.idToColor(Protocol.YELLOW));
	}

	@Test
	public void testColorToId() {
		assertEquals(Stone.colorToString(StoneColor.BLUE), Integer.toString(Protocol.BLUE));
		assertEquals(Stone.colorToString(StoneColor.GREEN), Integer.toString(Protocol.GREEN));
		assertEquals(Stone.colorToString(StoneColor.ORANGE), Integer.toString(Protocol.ORANGE));
		assertEquals(Stone.colorToString(StoneColor.PURPLE), Integer.toString(Protocol.PURPLE));
		assertEquals(Stone.colorToString(StoneColor.RED), Integer.toString(Protocol.RED));
		assertEquals(Stone.colorToString(StoneColor.YELLOW), Integer.toString(Protocol.YELLOW));
	}

	@Test
	public void testIdToShape() {
		assertEquals(StoneShape.CIRCLE, Stone.idToShape(Protocol.CIRCLE));
		assertEquals(StoneShape.CLUBS, Stone.idToShape(Protocol.CLUBS));
		assertEquals(StoneShape.CROSS, Stone.idToShape(Protocol.CROSS));
		assertEquals(StoneShape.DIAMOND, Stone.idToShape(Protocol.DIAMOND));
		assertEquals(StoneShape.RECTANGLE, Stone.idToShape(Protocol.RECTANGLE));
		assertEquals(StoneShape.STAR, Stone.idToShape(Protocol.STAR));
	}

	@Test
	public void testShapeToString() {
		assertEquals(Stone.shapeToString(StoneShape.CIRCLE), 
					 Integer.toString(Protocol.CIRCLE));
		assertEquals(Stone.shapeToString(StoneShape.CLUBS), 
					 Integer.toString(Protocol.CLUBS));
		assertEquals(Stone.shapeToString(StoneShape.CROSS), 
					 Integer.toString(Protocol.CROSS));
		assertEquals(Stone.shapeToString(StoneShape.DIAMOND), 
					 Integer.toString(Protocol.DIAMOND));
		assertEquals(Stone.shapeToString(StoneShape.RECTANGLE), 
					 Integer.toString(Protocol.RECTANGLE));
		assertEquals(Stone.shapeToString(StoneShape.STAR), 
					 Integer.toString(Protocol.STAR));
	}
	
}

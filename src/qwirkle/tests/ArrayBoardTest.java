/**
 * 
 */
package qwirkle.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import qwirkle.ArrayBoard;
import qwirkle.Move;
import qwirkle.Stone;
import qwirkle.StoneColor;
import qwirkle.StoneShape;

/**
 * @author Jerre
 *
 */
public class ArrayBoardTest {

	private ArrayBoard board;
	private Stone stone = new Stone(StoneShape.DIAMOND, StoneColor.BLUE);
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		board = new ArrayBoard();
		board.placeStone(new Move(stone, 0, 0));
	}

	@Test
	public void placeStoneTest() {
		assertEquals(stone, board.getField(0, 0));
	}

	@Test
	public void placeStoneBefore() {
		assertEquals(0, board.getDimensions().getTopLeft().getX());
		board.placeStone(new Move(stone, -1, 0));
		assertEquals(-1, board.getDimensions().getTopLeft().getX());
		assertEquals(stone, board.getField(-1, 0));
	}

	@Test
	public void placeStoneAfter() {
		assertEquals(0, board.getDimensions().getBottomRight().getX());
		board.placeStone(new Move(stone, 1, 0));
		assertEquals(1, board.getDimensions().getBottomRight().getX());
		assertEquals(stone, board.getField(1, 0));
	}
	
	@Test
	public void placeStoneAbove() {
		assertEquals(0, board.getDimensions().getTopLeft().getX());
		assertEquals(0, board.getDimensions().getTopLeft().getY());
		board.placeStone(new Move(stone, 0, 1));
		assertEquals(0, board.getDimensions().getTopLeft().getX());
		assertEquals(1, board.getDimensions().getTopLeft().getY());
		assertEquals(stone, board.getField(0, 1));
	}
	
	@Test
	public void placeStoneBelow() {
		assertEquals(0, board.getDimensions().getBottomRight().getX());
		assertEquals(0, board.getDimensions().getBottomRight().getY());
		board.placeStone(new Move(stone, 0, -1));
		assertEquals(0, board.getDimensions().getBottomRight().getX());
		assertEquals(-1, board.getDimensions().getBottomRight().getY());
		assertEquals(stone, board.getField(0, -1));
	}
	
	@Test
	public void testCanPickStone() {
		assertTrue(board.canPickStone());
		board.pickStones(3 * 6 * 6);
		assertFalse(board.canPickStone());
	}
	
	@Test
	public void testCanPickStones() {
		assertTrue(board.canPickStones(3 * 6 * 6));
		board.pickStone();
		assertFalse(board.canPickStones(3 * 6 * 6));
	}
	
	@Test
	public void testPickStones() {
		assertEquals(3 * 6 * 6, board.getStoneCount());
		board.pickStones(5);
		assertEquals(3 * 6 * 6 - 5, board.getStoneCount());
	}
	
	@Test
	public void testToString() {
		board.placeStone(new Move(stone, 0, 0));
		assertTrue(board.toString().contains(stone.toString()));
	}
}

/**
 * 
 */
package qwirkle.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import qwirkle.ArrayBoard;
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
		board.placeStone(stone, 0, 0);
	}

	@Test
	public void placeStoneTest() {
		assertEquals(stone, board.getField(0, 0));
	}

	@Test
	public void placeStoneBefore() {
		assertEquals(0, board.getDimensions().getTopLeft().getX());
		board.placeStone(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), -1, 0);
		assertEquals(-1, board.getDimensions().getTopLeft().getX());
	}

	@Test
	public void placeStoneAfter() {
		assertEquals(0, board.getDimensions().getBottomRight().getX());
		board.placeStone(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 1, 0);
		assertEquals(1, board.getDimensions().getBottomRight().getX());
	}
	
	@Test
	public void placeStoneAbove() {
		assertEquals(0, board.getDimensions().getTopLeft().getX());
		assertEquals(0, board.getDimensions().getTopLeft().getY());
		board.placeStone(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, 1);
		assertEquals(0, board.getDimensions().getTopLeft().getX());
		assertEquals(1, board.getDimensions().getTopLeft().getY());
	}
	
	@Test
	public void placeStoneBelow() {
		assertEquals(0, board.getDimensions().getBottomRight().getX());
		assertEquals(0, board.getDimensions().getBottomRight().getY());
		board.placeStone(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, -1);
		assertEquals(0, board.getDimensions().getBottomRight().getX());
		assertEquals(-1, board.getDimensions().getBottomRight().getY());
	}
}

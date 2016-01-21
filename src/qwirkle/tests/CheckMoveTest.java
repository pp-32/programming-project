package qwirkle.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import qwirkle.ArrayBoard;
import qwirkle.Board;
import qwirkle.Move;
import qwirkle.Stone;
import qwirkle.StoneColor;
import qwirkle.StoneShape;

public class CheckMoveTest {

	private Board board;
	
	@Before
	public void setUp() throws Exception {
		board = new ArrayBoard();
	}

	@Test
	public void testInitial() {
		/* Board:
		 *     |    |   
		 *     |  x |   
		 *     |    |  
		 */
		Stone stone = new Stone(StoneShape.DIAMOND, StoneColor.BLUE);
		
		assertTrue(board.checkMove(new Move(stone, 0, 0)));
		assertFalse(board.checkMove(new Move(stone, 0, 1)));
	}
	
	@Test
	public void testOccupied() {
		/* Board:
		 *     |    |   
		 *     | 3Bx|   
		 *     |    |  
		 */
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0));
		assertFalse(board.checkMove(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 0, 0)));
	}

	@Test
	public void testSingleOnRight() {
		/* Board:
		 *     |    |   
		 *     | 3B | x   
		 *     |    |  
		 */
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0));
		Stone second = new Stone(StoneShape.DIAMOND, StoneColor.RED);
		assertTrue(board.checkMove(new Move(second, 1, 0)));
		assertFalse(board.checkMove(new Move(second, 2, 0)));
		assertFalse(board.checkMove(new Move(second, 1, 1)));
	}

	@Test
	public void testMultipleShapeOnRight() {
		/* Board:
		 *     |    |    |
		 *     | 3B | 3R | x      
		 *     |    |    | 
		 */
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 0));
		Stone third = new Stone(StoneShape.DIAMOND, StoneColor.GREEN);
		Stone fourth = new Stone(StoneShape.CIRCLE, StoneColor.GREEN);
		assertTrue(board.checkMove(new Move(third, 2, 0)));
		assertFalse(board.checkMove(new Move(fourth, 2, 0)));
	}

	@Test
	public void testDuplicateOnRight() {
		/* Board:
		 *     |    |    |
		 *     | 3B | 3R | x      
		 *     |    |    | 
		 */
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 0));
		assertFalse(board.checkMove(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 2, 0)));
	}
	
	@Test
	public void testMaximumLength() {
		/* Board:
		 *    
		 *  | 3B | 3R | 3G | 3O | 3Y | 3P | x      
		 *     
		 */
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 0));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.GREEN), 2, 0));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 3, 0));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.YELLOW), 4, 0));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.PURPLE), 5, 0));
		assertFalse(board.checkMove(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 6, 0)));
	}

	@Test
	public void testJoinTwoUnknownSequences() {
		/*
		 * Board:
		 * 
		 *  3B |  x | 3O
		 *  2B |    | 2O
		 *  1B | 1R | 1O
		 *  
		 */

		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, 0));
		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 1, 0));
		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.ORANGE), 2, 0));
		board.placeStone(new Move(new Stone(StoneShape.CROSS, StoneColor.BLUE), 0, 1));
		board.placeStone(new Move(new Stone(StoneShape.CROSS, StoneColor.ORANGE), 2, 1));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 2));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 2, 2));

		assertTrue(board.checkMove(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 2)));
	}
	
	@Test
	public void testJoinTwoSequences() {
		/*
		 * Board:
		 * 
		 *  1B | 1R | 1O |
		 *  2B |    | 2O |
		 *  3B |  x | 3O | 3P 
		 *  
		 */

		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, 0));
		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 1, 0));
		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.ORANGE), 2, 0));
		board.placeStone(new Move(new Stone(StoneShape.CROSS, StoneColor.BLUE), 0, 1));
		board.placeStone(new Move(new Stone(StoneShape.CROSS, StoneColor.ORANGE), 2, 1));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 1));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 2, 1));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.PURPLE), 2, 2));

		assertTrue(board.checkMove(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 2)));
	}
	
	@Test
	public void testJoinHorizontalAndVertical() {
		/*
		 * Board:
		 * 
		 *     |    | 3O |
		 *     |    | 2O |
		 *  1B | 1R | x  |
		 */

		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, 0));
		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 1, 0));

		board.placeStone(new Move(new Stone(StoneShape.CROSS, StoneColor.ORANGE), 2, 1));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 2, 2));

		assertTrue(board.checkMove(new Move(new Stone(StoneShape.CIRCLE, StoneColor.ORANGE), 2, 0)));
	}
}

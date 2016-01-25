package qwirkle.tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import qwirkle.ArrayBoard;
import qwirkle.Board;
import qwirkle.Location;
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
		
		Move[] setup = {
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 0)
		};
		board.placeStones(Arrays.asList(setup));
		
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
		Move[] setup = {
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 0)
		};
		board.placeStones(Arrays.asList(setup));
		assertFalse(board.checkMove(new Move(
						new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 
						new Location(2, 0))));
	}
	
	@Test
	public void testMaximumLength() {
		/* Board:
		 *    
		 *  | 3B | 3R | 3G | 3O | 3Y | 3P | x      
		 *     
		 */
		
		Move[] setup = {
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.GREEN), 2, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 3, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.YELLOW), 4, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.PURPLE), 5, 0),
		};
		board.placeStones(Arrays.asList(setup));
		assertFalse(board.checkMove(new Move(
						new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 
						new Location(6, 0))));
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

		Move[] setup = {
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 1, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.ORANGE), 2, 0),
			new Move(new Stone(StoneShape.CROSS, StoneColor.BLUE), 0, 1),
			new Move(new Stone(StoneShape.CROSS, StoneColor.ORANGE), 2, 1),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 2),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 2, 2),
		};
		board.placeStones(Arrays.asList(setup));
		assertTrue(board.checkMove(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 2)));
		assertFalse(board.checkMove(new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 1, 2)));
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

		Move[] setup = {
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 1, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.ORANGE), 2, 0),
			new Move(new Stone(StoneShape.CROSS, StoneColor.BLUE), 0, 1),
			new Move(new Stone(StoneShape.CROSS, StoneColor.ORANGE), 2, 1),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 1),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 2, 1),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.PURPLE), 2, 2),
		};
		board.placeStones(Arrays.asList(setup));
		assertTrue(board.checkMove(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 2)));
		assertFalse(board.checkMove(new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 1, 2)));
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

		Move[] setup = {
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 1, 0),
	
			new Move(new Stone(StoneShape.CROSS, StoneColor.ORANGE), 2, 1),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 2, 2),
		};
		board.placeStones(Arrays.asList(setup));
		
		assertTrue(board.checkMove(new Move(
						new Stone(StoneShape.CIRCLE, StoneColor.ORANGE), 
						new Location(2, 0))));
		assertFalse(board.checkMove(new Move(
						new Stone(StoneShape.CLUBS, StoneColor.ORANGE), 
						new Location(2, 0))));
	}

	@Test
	public void testJoinOrthogonalSequences() {
		
		/*
		 * Board
		 * 
		 *     |  x |    |
		 *  1B | 1R | 1O |
		 *     |  x |    |
		 *     | 3R | 3O | 3G
		 *     |  x |    |
		 */
		
		Move[] setup = {
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), -1, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 0, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.ORANGE), 1, 0),

			new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 0, -2),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 1, -2),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.GREEN), 2, -2),	
		};
		board.placeStones(Arrays.asList(setup));
		
		Move[] moves = {
			new Move(new Stone(StoneShape.STAR, StoneColor.RED), 0, 1),
			new Move(new Stone(StoneShape.CLUBS, StoneColor.RED), 0, -1),
			new Move(new Stone(StoneShape.RECTANGLE, StoneColor.RED), 0, -3),
		};
		assertTrue(board.checkMoves(Arrays.asList(moves)));
	}
	
	@Test
	public void testJoinOrthogonalSequences2() {
		
		/*
		 * Board
		 * 
		 *    | 1B |   |    |
		 *  x | 1R | x | 3R | x
		 *    | 1O |   | 3O |
		 *    |    |   | 3G |
		 *     
		 */
		
		Move[] setup = {
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, 1),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 0, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.ORANGE), 0, -1),

			new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 2, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 2, -1),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.GREEN), 2, -2),	
		};
		board.placeStones(Arrays.asList(setup));
		
		Move[] moves = {
			new Move(new Stone(StoneShape.STAR, StoneColor.RED), -1, 0),
			new Move(new Stone(StoneShape.CLUBS, StoneColor.RED), 1, 0),
			new Move(new Stone(StoneShape.RECTANGLE, StoneColor.RED), 3, 0),
		};
		assertTrue(board.checkMoves(Arrays.asList(moves)));
	}
	
	@Test
	public void testPerpendicularSequences() {
		/*
		 * Board
		 *    |  x | 1G |
		 *    |  x | 2G |
		 *    |    | 3G | 
		 */
		Move[] setup = {
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.GREEN), 0, 1),
			new Move(new Stone(StoneShape.CLUBS, StoneColor.GREEN), 0, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.GREEN), 0, -1),
		};
		board.placeStones(Arrays.asList(setup));
		
		Move[] moves = {
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), -1, 1),
			new Move(new Stone(StoneShape.CLUBS, StoneColor.BLUE), -1, 0),
		};
		
		assertTrue(board.checkMoves(Arrays.asList(moves)));
	}
}

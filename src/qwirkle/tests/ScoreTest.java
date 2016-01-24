package qwirkle.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import qwirkle.ArrayBoard;
import qwirkle.Board;
import qwirkle.Move;
import qwirkle.Stone;
import qwirkle.StoneColor;
import qwirkle.StoneShape;

public class ScoreTest {

	private Board board;
	
	@Before
	public void setUp() throws Exception {
		board = new ArrayBoard();
	}

	@Test
	public void testSingle() {
		/* Board:
		 *     |    |   
		 *     |  x |   
		 *     |    |  
		 */
		List<Move> moves = new ArrayList<Move>();
		moves.add(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0));
		assertEquals(1, board.calculateScore(moves));
	}

	@Test
	public void testSingleOnRight() {
		/* Board:
		 *     |    |   
		 *     | 3B | x  
		 *     |    |  
		 */
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0));
		
		List<Move> moves = new ArrayList<Move>();
		moves.add(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 0));
		assertEquals(2, board.calculateScore(moves));
	}

	@Test
	public void testMultipleOnRight() {
		/* Board:
		 *     |    |    |
		 *     | 3B | 3R |  x
		 *     |    |    |
		 */
		

		Move[] setup = { 
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 0)
		};
		board.placeStones(Arrays.asList(setup));
		
		List<Move> moves = new ArrayList<Move>();
		moves.add(new Move(new Stone(StoneShape.DIAMOND, StoneColor.GREEN), 2, 0));
		assertEquals(3, board.calculateScore(moves));
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
		
		List<Move> moves = new ArrayList<Move>();
		moves.add(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 2));
		assertEquals(3, board.calculateScore(moves));
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
		assertEquals(5 + 3 + 3, board.calculateScore(Arrays.asList(moves)));
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
		assertEquals(5 + 3 + 3, board.calculateScore(Arrays.asList(moves)));
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
		
		assertEquals(2 + 2 + 2, board.calculateScore(Arrays.asList(moves)));
	}
}

package qwirkle.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
	public void testMultipleShapeOnRight() {
		/* Board:
		 *     |    |    |
		 *     | 3B | 3R |  x
		 *     |    |    |
		 */
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0));;
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 0));
		
		List<Move> moves = new ArrayList<Move>();
		moves.add(new Move(new Stone(StoneShape.DIAMOND, StoneColor.GREEN), 1, 0));
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

		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, 0));
		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.RED), 1, 0));
		board.placeStone(new Move(new Stone(StoneShape.CIRCLE, StoneColor.ORANGE), 2, 0));
		board.placeStone(new Move(new Stone(StoneShape.CROSS, StoneColor.BLUE), 0, 1));
		board.placeStone(new Move(new Stone(StoneShape.CROSS, StoneColor.ORANGE), 2, 1));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 2));
		board.placeStone(new Move(new Stone(StoneShape.DIAMOND, StoneColor.ORANGE), 2, 2));

		List<Move> moves = new ArrayList<Move>();
		moves.add(new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 2));
		assertEquals(3, board.calculateScore(moves));
	}

}

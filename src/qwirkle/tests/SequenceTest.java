package qwirkle.tests;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import qwirkle.ArrayBoard;
import qwirkle.Board;
import qwirkle.Location;
import qwirkle.Move;
import qwirkle.Sequence;
import qwirkle.SequenceDirection;
import qwirkle.SequenceType;
import qwirkle.Stone;
import qwirkle.StoneColor;
import qwirkle.StoneShape;

public class SequenceTest {

	private Board board;
	
	@Before
	public void setup() {
		board = new ArrayBoard();
	}

	@Test
	public void testSingle() {
		/* Board:
		 *     |    |   
		 *     | 3B | 
		 *     |    |  
		 */
		Move move = new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0);
		board.placeStone(move);
		
		Sequence sequence = new Sequence(board, new Location(0, 0), SequenceDirection.UNKNOWN);
		assertEquals(1, sequence.getMoves().size());
		assertEquals(move, sequence.getMoves().get(0));
		assertEquals(SequenceType.UNKNOWN, sequence.getType());
		assertEquals(SequenceDirection.UNKNOWN, sequence.getDirection());
		assertTrue(sequence.isValid());
	}

	@Test
	public void testHorizontalShapes() {
		/* Board:
		 *     |    |    |
		 *     | 3B | 3R |      
		 *     |    |    | 
		 */
		
		Move[] setup = {
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 1, 0)
		};
		List<Move> setupList = Arrays.asList(setup);
		board.placeStones(setupList);

		Sequence sequence = new Sequence(board, new Location(0, 0), SequenceDirection.HORIZONTAL);
		assertEquals(2, sequence.getLength());
		assertTrue(sequence.getMoves().containsAll(setupList));
		assertEquals(SequenceType.SHAPE, sequence.getType());
		assertEquals(SequenceDirection.HORIZONTAL, sequence.getDirection());
		assertTrue(sequence.isValid());
	}

	@Test
	public void testHorizontalColor() {
		/* Board:
		 *     |    |    |
		 *     | 3B | 1B |      
		 *     |    |    | 
		 */
		
		Move[] setup = {
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 1, 0)
		};
		List<Move> setupList = Arrays.asList(setup);
		board.placeStones(setupList);

		Sequence sequence = new Sequence(board, new Location(0, 0), SequenceDirection.HORIZONTAL);
		assertEquals(2, sequence.getLength());
		assertTrue(sequence.getMoves().containsAll(setupList));
		assertEquals(SequenceType.COLOR, sequence.getType());
		assertEquals(SequenceDirection.HORIZONTAL, sequence.getDirection());
		assertTrue(sequence.isValid());
	}
	
	@Test
	public void testVerticalShapes() {
		/* Board:
		 *     |    |    |
		 *     | 3B |    |      
		 *     | 3R |    | 
		 */
		
		Move[] setup = {
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.RED), 0, -1)
		};
		List<Move> setupList = Arrays.asList(setup);
		board.placeStones(setupList);

		Sequence sequence = new Sequence(board, new Location(0, 0), SequenceDirection.VERTICAL);
		assertEquals(2, sequence.getLength());
		assertTrue(sequence.getMoves().containsAll(setupList));
		assertEquals(SequenceType.SHAPE, sequence.getType());
		assertEquals(SequenceDirection.VERTICAL, sequence.getDirection());
		assertTrue(sequence.isValid());
	}

	@Test
	public void testDuplicate() {
		/* Board:
		 *     |    |    |
		 *     | 3B | 1B | 1B     
		 *     |    |    | 
		 */
		
		Move[] setup = {
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 1, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 2, 0)
		};
		List<Move> setupList = Arrays.asList(setup);
		board.placeStones(setupList);

		Sequence sequence = new Sequence(board, new Location(0, 0), SequenceDirection.HORIZONTAL);
		assertFalse(sequence.isValid());
	}

	@Test
	public void testInvalidType() {
		/* Board:
		 *     |    |    |
		 *     | 3B | 1B | 1O
		 *     |    |    | 
		 */
		
		Move[] setup = {
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 1, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.ORANGE), 2, 0)
		};
		List<Move> setupList = Arrays.asList(setup);
		board.placeStones(setupList);

		Sequence sequence = new Sequence(board, new Location(0, 0), SequenceDirection.HORIZONTAL);
		assertFalse(sequence.isValid());
	}

	@Test
	public void testValidJoin() {
		/* Board:
		 *     |    |    |
		 *     | 3B | 1B | 2B     
		 *     |    |    | 
		 */
		
		Move[] setup = {
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 1, 0),
			new Move(new Stone(StoneShape.CLUBS, StoneColor.BLUE), 2, 0)
		};
		List<Move> setupList = Arrays.asList(setup);
		board.placeStones(setupList);

		Sequence sequence = new Sequence(board, new Location(1, 0), SequenceDirection.HORIZONTAL);
		assertEquals(3, sequence.getLength());
		assertTrue(sequence.getMoves().containsAll(setupList));
		assertEquals(SequenceType.COLOR, sequence.getType());
		assertTrue(sequence.isValid());
	}

	@Test
	public void testInvalidJoin() {
		/* Board:
		 *     |    |    |
		 *     | 3B | 1B | 1B     
		 *     |    |    | 
		 */
		
		Move[] setup = {
			new Move(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 1, 0),
			new Move(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 2, 0)
		};
		List<Move> setupList = Arrays.asList(setup);
		board.placeStones(setupList);

		Sequence sequence = new Sequence(board, new Location(1, 0), SequenceDirection.HORIZONTAL);
		assertFalse(sequence.isValid());
	}
	
	@Test
	public void testDetectHorizontalDirection() {
		assertEquals(SequenceDirection.HORIZONTAL, 
					 Sequence.getDirectionFromVector(new Location(1, 0)));
		assertEquals(SequenceDirection.HORIZONTAL, 
					 Sequence.getDirectionFromVector(new Location(-1, 0)));
	}
	
	@Test
	public void testDetectVerticalDirection() {
		assertEquals(SequenceDirection.VERTICAL, 
					 Sequence.getDirectionFromVector(new Location(0, 1)));
		assertEquals(SequenceDirection.VERTICAL, 
					 Sequence.getDirectionFromVector(new Location(0, -1)));
	}
}

package qwirkle;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides an implementation of Board using a 2D array as back-end.
 * @author Jerre
 *
 */
public class ArrayBoard extends Board {

	private static final int BOARD_DIMENSION = 200;
	
	private Stone[][] field;
	private Rectangle dimensions;

	private List<Location> occupied;
	
	/**
	 * Creates a new array board.
	 */
	public ArrayBoard() {
		reset();
	}
	
	@Override
	public Board deepCopy() { 
		ArrayBoard copy = new ArrayBoard();
		
		for (int i = 0; i < BOARD_DIMENSION; i++) {
			System.arraycopy(this.field[i], 0, copy.field[i], 0, BOARD_DIMENSION);
		}
		
		for (Location l : occupied) {
			copy.occupied.add(l);
		}
		
		return copy;
	}

	@Override
	public void reset() {
		occupied = new ArrayList<Location>();
		field = new Stone[BOARD_DIMENSION][BOARD_DIMENSION];
		dimensions = new Rectangle(0, 0, 0, 0);
	}

	@Override
	public Rectangle getDimensions() {
		return dimensions;
	}

	@Override
	public Stone getField(int x, int y) {
		return field[x + BOARD_DIMENSION / 2][y + BOARD_DIMENSION / 2];
	}

	@Override
	public void placeStone(Move move) {
		int x = move.getLocation().getX();
		int y = move.getLocation().getY();
		
		field[x + BOARD_DIMENSION / 2][y + BOARD_DIMENSION / 2] = move.getStone();
		occupied.add(move.getLocation().deepCopy());
		
		// update dimensions when needed:
		if (x < dimensions.getTopLeft().getX()) {
			dimensions.getTopLeft().setX(x);
		} else if (x > dimensions.getBottomRight().getX()) {
			dimensions.getBottomRight().setX(x);
		}
		
		if (y > dimensions.getTopLeft().getY()) {
			dimensions.getTopLeft().setY(y);
		} else if (y < dimensions.getBottomRight().getY()) {
			dimensions.getBottomRight().setY(y);
		}
	}

	@Override
	public List<Location> getOccupiedFields() {
		return occupied;
	}
}

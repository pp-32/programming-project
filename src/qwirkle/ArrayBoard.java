package qwirkle;

public class ArrayBoard extends Board {

	private static final int BOARD_DIMENSION = 100;
	
	private Stone[][] field;
	private Rectangle dimensions;
	
	public ArrayBoard() {
		reset();
	}
	
	@Override
	public Board deepCopy() { 
		ArrayBoard copy = new ArrayBoard();
		for (int i = 0; i < BOARD_DIMENSION; i++) {
			System.arraycopy(this.field[i], 0, copy.field[i], 0, BOARD_DIMENSION);
		}
		return copy;
	}

	@Override
	public void reset() {
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
	public void placeStone(Stone stone, int x, int y) {
		field[x + BOARD_DIMENSION / 2][y + BOARD_DIMENSION / 2] = stone;

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
	
	
	public static void main(String[] args) {
		Board board = new ArrayBoard();
		board.placeStone(new Stone(StoneShape.DIAMOND, StoneColor.BLUE), 0, 0);
		board.placeStone(new Stone(StoneShape.DIAMOND, StoneColor.RED), 2, 0);
		board.placeStone(new Stone(StoneShape.DIAMOND, StoneColor.YELLOW), 1, 0);
		board.placeStone(new Stone(StoneShape.DIAMOND, StoneColor.GREEN), -1, 0);
		board.placeStone(new Stone(StoneShape.CIRCLE, StoneColor.BLUE), 0, 1);
		board.placeStone(new Stone(StoneShape.STAR, StoneColor.BLUE), 0, -1);
		
		System.out.println(board.toString());
	}


}

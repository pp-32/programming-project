package qwirkle;

public class ArrayBoard extends Board {

	private static final int BOARD_DIMENSION = 100;
	
	private Stone[][] field;
	
	public ArrayBoard() {
		field = new Stone[BOARD_DIMENSION][BOARD_DIMENSION];
	}
	
	@Override
	public Board deepCopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Stone getField(int x, int y) {
		return field[x + BOARD_DIMENSION / 2][y + BOARD_DIMENSION / 2];
	}

	@Override
	public void placeStone(Stone stone, int x, int y) {
		// TODO Auto-generated method stub
		field[x + BOARD_DIMENSION / 2][y + BOARD_DIMENSION / 2] = stone;
	}


}

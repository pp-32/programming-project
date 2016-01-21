package qwirkle;

import java.util.List;

public class HumanPlayer extends Player {

	public HumanPlayer(String theName) {
		super(theName);
	}

	public List<Stone> getStones() {
		return this.stones;
	}
	
	public void placeStones(Board board, List<Move> moves) {
		for (Move m : moves) {
			this.stones.remove(m.getStone());
		}
		board.placeStones(moves);
		setChanged();
		notifyObservers("stones");
	}

	public void giveStones(List<Stone> stones) {
		this.stones.addAll(stones);
		setChanged();
		notifyObservers("stones");		
	}
}

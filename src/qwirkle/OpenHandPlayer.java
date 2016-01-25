package qwirkle;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player that reveals his/her hand to the public.
 * @author Jerre
 *
 */
public abstract class OpenHandPlayer extends Player {

	private List<Stone> hand;
	
	public OpenHandPlayer(String theName) {
		super(theName);
		hand = new ArrayList<Stone>();
	}

	/**
	 * Gets the hand of the player.
	 * @return the stones in the hand of the player.
	 */
	public List<Stone> getStones() {
		return this.hand;
	}
	
	/**
	 * Places stones from the hand of the player onto the board.
	 * @param board The board the place the stones on.
	 * @param moves The moves the player should perform.
	 */
	public void placeStones(Board board, List<Move> moves) {
		for (Move m : moves) {
			this.getStones().remove(m.getStone());
		}
		setScore(getScore() + board.calculateScore(moves));
		board.placeStones(moves);
		setChanged();
		notifyObservers("stones");
	}

	/**
	 * Gives stones to the player.
	 * @param stones The stones to give.
	 */
	public void giveStones(List<Stone> stones) {
		this.getStones().addAll(stones);
		setChanged();
		notifyObservers("stones");		
	}
	
	@Override
	public int getHandSize() {
		return this.hand.size();
	}
}

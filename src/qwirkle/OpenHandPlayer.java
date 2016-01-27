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
	
	/**
	 * Creates a new OpenHandPlayer instance.
	 * @param theName The name of the player.
	 */
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
	
	@Override
	public int getHandSize() {
		return this.hand.size();
	}
	
	/**
	 * Places stones from the hand of the player onto the board.
	 * @param board The board the place the stones on.
	 * @param moves The moves the player should perform.
	 */
	public MoveResult placeStones(Board board, List<Move> moves) {
		if (!board.checkMoves(moves)) {
			return new MoveResult(moves, 0);
		}

		int score = board.calculateScore(moves);
		
		for (Move m : moves) {
			this.getStones().remove(m.getStone());
		}
		
		setScore(getScore() + score);
		board.placeStones(moves);

		MoveResult result = new MoveResult(moves, score);
		setChanged();
		notifyObservers(result);
		return result;
	}

	/**
	 * Removes the stones from the hand and notifies the observers 
	 * of the player the player wants to trade. 
	 * @param stonesToTrade The stones to trade.
	 */
	public void performTrade(List<Stone> stonesToTrade) {
		// Do not use removeAll(stonesToTrade), it will remove all occurrences of each stone.
		for (Stone s : stonesToTrade) {
			this.hand.remove(s);
		}
		setChanged();
		notifyObservers(stonesToTrade);
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
}

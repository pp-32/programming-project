package qwirkle;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a remote player of which hand is unknown.
 * @author Jerre
 *
 */
public class RemotePlayer extends Player {

	private Lock lock = new ReentrantLock();
	private Condition moveMade = lock.newCondition();
	
	private int handSize;

	/**
	 * Creates a new remote player.
	 * @param theName The name of the player.
	 */
	public RemotePlayer(String theName) {
		super(theName);
	}

	@Override
	public int getHandSize() {
		return handSize;
	}
	
	/**
	 * Updates the amount of stones in the hand of the player.
	 * @param value The amount of stones
	 */
	public void setHandSize(int value) {
		handSize = value;
	}

	@Override
	public void makeMove(Board board) {	
		lock.lock();
		try {
			moveMade.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}		
	}

	/**
	 * Notifies the remote player has placed stones onto the board.
	 * @param board The board the player has placed stones on.
	 * @param moves The moves the player has made.
	 * @param score The score the player has gained.
	 */
	public void notifyPlacedStones(Board board, List<Move> moves, int score) {
		board.placeStones(moves);
		setScore(getScore() + score);
		int size = getHandSize() - moves.size();
		for (int i = 0; i < moves.size() && board.canPickStone(); i++) {
			board.pickStone();
			size++;
		}
		setHandSize(size);
		
		lock.lock();
		try {
			moveMade.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Notifies the player has traded stones.
	 */
	public void notifyTraded() {
		moveMade.signalAll();
	}
	
}

package qwirkle;

import java.util.ArrayList;
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

	public void notifyPlacedStones(Board board, List<Move> moves, int score) {
		board.placeStones(moves);
		setScore(getScore() + score);
		int handSize = getHandSize() - moves.size();
		for (int i = 0; i < moves.size() && board.canPickStone(); i++) {
			board.pickStone();
			handSize++;
		}
		setHandSize(handSize);
		
		lock.lock();
		try {
			moveMade.signalAll();
		} finally {
			lock.unlock();
		}		

		
	}
	
	public void notifyTraded() {
		moveMade.signalAll();
	}
	
}

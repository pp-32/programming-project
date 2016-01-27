package qwirkle.client;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import qwirkle.Board;
import qwirkle.Move;
import qwirkle.MoveResult;
import qwirkle.OpenHandPlayer;
import qwirkle.Stone;

/**
 * Represents a human player in the Qwirkle game.
 * @author Jerre
 *
 */
public class HumanPlayer extends OpenHandPlayer {

	private Lock lock = new ReentrantLock();
	private Condition moveMade = lock.newCondition();
	
	/**
	 * Creates a new instance of the HumanPlayer.
	 * @param theName The name of the player.
	 */
	public HumanPlayer(String theName) {
		super(theName);
	}

	@Override
	public void makeMove(Board board) {
		setChanged();
		notifyObservers("turnstarted");
		lock.lock();
		try {
			moveMade.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void performTrade(List<Stone> stonesToTrade) {
		lock.lock();
		try {
			super.performTrade(stonesToTrade);
			moveMade.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public MoveResult placeStones(Board board, List<Move> moves) {

		lock.lock();
		try {
			MoveResult result = super.placeStones(board, moves);
			moveMade.signalAll();
			return result;
		} finally {
			lock.unlock();
		}
	}
}

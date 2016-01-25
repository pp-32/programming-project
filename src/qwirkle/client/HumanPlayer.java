package qwirkle.client;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import qwirkle.Board;
import qwirkle.OpenHandPlayer;

public class HumanPlayer extends OpenHandPlayer {

	private Lock lock = new ReentrantLock();
	private Condition moveMade = lock.newCondition();
	
	public HumanPlayer(String theName) {
		super(theName);
	}

	@Override
	public void makeMove(Board board) {
		setChanged();
		notifyObservers("turnstarted");
		try {
			moveMade.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

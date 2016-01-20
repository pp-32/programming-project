package qwirkle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TUIView implements View {

	private final Lock lock = new ReentrantLock();
	private final Condition gameStarted = lock.newCondition(); 
	
	private QwirkleClient client;
	private Game game;
	

	public TUIView(QwirkleClient client) {
		this.client = client;
	}

	public void run() {
		// Create scanner to obtain user input
		Scanner user_input = new Scanner(System.in);
		String playerName;
		// Obtain user input
		System.out.print("Enter your name: ");
		playerName = user_input.nextLine();
		// Output information
		System.out.print("Your name is: " + playerName);
		client.requestJoin(playerName);
		
		lock.lock();
		try {
			gameStarted.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		
		//String playerCount;
		//System.out.print("");

		try (Scanner scanner = new Scanner(System.in)) {
			boolean continueLoop = true;
			while (continueLoop) {
				System.out.print("Command:");
				continueLoop = scanner.hasNextLine() && processCommand(scanner.nextLine());
			}
		}
	}

	private boolean processCommand(String command) {

		boolean continueLoop = true;
		try (Scanner commandScanner = new Scanner(command)) {
			switch (commandScanner.next()) {
			case "place":
				handlePlaceCommand(commandScanner);
				break;
			case "trade":
				handleTradeCommand(commandScanner);
				break;
			case "exit":
				continueLoop = false;
				break;
			}
		}
		return continueLoop;
	}

	private void handleTradeCommand(Scanner commandScanner) {
		List<Stone> stones = new ArrayList<Stone>();

		// trade <aantal stenen>
		int stonesCount = Integer.parseInt(commandScanner.next());

		for (int i = 0; i < stonesCount; i++) {
			int stoneIndex = Integer.parseInt(commandScanner.next());
		}

		// TODO: ask controller for hand and retrieve stones from there.

	}

	private void handlePlaceCommand(Scanner commandScanner) {
		List<Move> moves = new ArrayList<Move>();

		// place <aantal stenen> {<index> <x> <y> ....}
		int stonesCount = Integer.parseInt(commandScanner.next());

		for (int i = 0; i < stonesCount; i++) {
			int stoneIndex = Integer.parseInt(commandScanner.next());
			int x = Integer.parseInt(commandScanner.next());
			int y = Integer.parseInt(commandScanner.next());
			Location location = new Location(x, y);

			// TODO: ask controller for hand and retrieve stones from there.

			Move move = new Move(null, location);
			moves.add(move);
		}

		client.setMove(moves);
		System.out.println(moves.size());
	}

	public void updateView() {

	}

	@Override
	public void buildView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
		if (arg0 == client) {
			if (arg1 instanceof Game) {
				this.game = (Game)arg1;
				gameStarted.signalAll();
			}
		}
	}
}

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

	private Lock lock = new ReentrantLock();
	private Condition gameStarted = lock.newCondition();
	
	private QwirkleClient client;
	

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
		System.out.println("");
		client.requestJoin(playerName);
		
		// TODO: let the tui know that the join request is accepted
		
		int playerCount;
		System.out.print("Enter the desired amount of players: ");
		playerCount = Integer.parseInt(user_input.nextLine());
		System.out.print("The number of players is: " + playerCount);
		System.out.println("");
		client.requestGame(playerCount);
		
		// TODO: let the tui know that the game request is accepted
		System.out.println("Waiting for game...");
		 
		lock.lock();
		try {
			gameStarted.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		
		System.out.println("Game joined!");

		System.out.println(client.getCurrentGame().getBoard().toString());
		printStones(client.getCurrentGame().getHumanPlayer().getStones());
		
		
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
			stones.add(client.getCurrentGame().getHumanPlayer().getStones().get(stoneIndex));
		}

		client.doTrade(stones);

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

			Move move = new Move(client.getCurrentGame().getHumanPlayer().getStones().get(stoneIndex), location);
			moves.add(move);
		}

		client.setMove(moves);
	}

	public void updateView() {

	}

	@Override
	public void buildView() {
		// TODO Auto-generated method stub

	}
	
	private void printStones(List<Stone> stones) {
		for (int i = 0; i < stones.size(); i++) {
			Stone s = stones.get(i);
			System.out.print(s.toString());
			if (i < stones.size() - 1) {
				System.out.print(" ");
			}
		}
		System.out.println();
	}

	@Override
	public void update(Observable arg0, Object arg1) {		
		if (arg1 instanceof Game) {
			lock.lock();
			try {
				gameStarted.signalAll();
			} finally {
				lock.unlock();
			}
		} else if (arg0 instanceof Board) {
			System.out.println(((Board)arg0).toString());
		} else if (arg0 instanceof HumanPlayer) {
			switch ((String)arg1) {
			case "stones":
				printStones(((HumanPlayer)arg0).getStones());
				break;
			}
		}
	}

	@Override
	public void showError(String reason) {
		System.out.println(reason);
		
	}
}

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
		Scanner userInput = new Scanner(System.in);

		boolean inputValid = false;

		System.out.print("Enter your name: ");
		while (!inputValid) {
			try {
				String playerName = userInput.nextLine();
				System.out.print("Your name is: " + playerName);
				System.out.println("");
				client.requestJoin(playerName);
				inputValid = true;
			} catch (Exception e1) {
				System.out.println("You didn't enter a valid name. Please try again");
			}
		}

		// TODO: let the tui know that the join request is accepted

		System.out.print("Enter the desired amount of players: ");
		boolean inputValid2 = false;
		while (!inputValid2) {
			try {
				int playerCount = Integer.parseInt(userInput.nextLine());
				if (playerCount <= 4) {
					System.out.print("The number of players is: " + playerCount);
					System.out.println("");
					client.requestGame(playerCount);
					inputValid2 = true;
				} else {
					System.out.println("You didn't enter a valid amount of players. Please try again.");
					inputValid2 = false;
				}
			} catch (NumberFormatException e1) {
				System.out.println("You didn't enter a valid amount of players. Please try again.");
			}
		}

		// TODO: let the tui know that the game request is accepted
		System.out.println("Waiting for game...");

		lock.lock();
		try {
			gameStarted.await();
			System.out.println("Game joined!");
			System.out.println(client.getCurrentGame().getBoard().toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		try (Scanner scanner = new Scanner(System.in)) {
			boolean continueLoop = true;
			while (continueLoop) {
				// TODO Try/catch Block
				System.out.print("Place, trade or exit? ");
				continueLoop = scanner.hasNextLine() && processCommand(scanner.nextLine());
			}
		}
	}

	private boolean processCommand(String command) {

		boolean continueLoop = true;
		try (Scanner commandScanner = new Scanner(command)) {
			switch (commandScanner.nextLine()) {
				case "place":
					handlePlaceCommand();
					break;
				case "trade":
					handleTradeCommand(commandScanner);
					break;
				case "exit":
					continueLoop = false;
					break;
				default:
					System.out.println("You didn't enter a valid command. Please try again.");
					break;
			}
		}
		return continueLoop;
	}

	private void handleTradeCommand(Scanner commandScanner) {
		if (!client.getIsMyTurn()) {
			showError("Wait for your turn");
			return;
		}
		
		List<Stone> stones = new ArrayList<Stone>();

		System.out.print("How many stones do you want to trade? ");

		int stonesCount = readNextInt(0, 6);

		for (int i = 0; i < stonesCount; i++) {
			System.out.print("Enter the index of the stone you want to trade. ");
			int stoneIndex = readNextInt(0, 5);
			stones.add(client.getCurrentGame().getHumanPlayer().getStones().get(stoneIndex));
		}

		client.doTrade(stones);
	}

	private void handlePlaceCommand() {
		if (!client.getIsMyTurn()) {
			showError("Wait for your turn");
			return;
		}
		
		List<Move> moves = new ArrayList<Move>();

		System.out.print("How many stones do you want to place? ");

		int stonesCount = readNextInt(0, 6);

		for (int i = 0; i < stonesCount; i++) {
			System.out.print("Which stone do you want to place? ");
			int stoneIndex = readNextInt(0, 5);

			System.out.print("Enter the desired x-location for stone " + stoneIndex + " ");
			int x = readNextInt(-1000, 1000);

			System.out.print("Enter the desired y-location for stone " + stoneIndex + " ");
			int y = readNextInt(-1000, 1000);

			Location location = new Location(x, y);
			System.out.print("You placed a stone on (" + x + ", " + y + ")");
			System.out.println("");

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
		System.out.print("This is your hand: ");
		System.out.println("");
		for (int i = 0; i < stones.size(); i++) {
			System.out.print("[" + i + "] ");
		}
		System.out.println("");
		for (int i = 0; i < stones.size(); i++) {
			Stone s = stones.get(i);
			System.out.print(s.toString() + " ");
			if (i < stones.size() - 1) {
				System.out.print(" ");
			}
		}
		System.out.println();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (!(arg1 instanceof String)) {
			return;
		}
		
		lock.lock();
		try {
			switch ((String) arg1) {
				case "gamestarted":
					gameStarted.signalAll();
					break;
				case "placedstone":
					System.out.println(((Board) arg0).toString());
					break;
				case "turnstarted":
					System.out.println("Your turn has started!");
					break;
				case "stones":
					HumanPlayer hp = (HumanPlayer) arg0;
					printStones(hp.getStones());
					break;
				case "score":
					Player p = (Player) arg0;
					System.out.println("Player " + p.getName() + " has now " + p.getScore() + " score.");
					break;
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void showError(String reason) {
		System.out.println(reason);

	}

	public int readNextInt(int min, int max) {
		Scanner commandScanner = new Scanner(System.in);
		int count = 0;
		boolean valid = false;
		while (!valid) {
			try {
				count = Integer.parseInt(commandScanner.nextLine());
				if (min <= count && count <= max) {
					valid = true;
				} else {
					System.out.println("You didn't enter a valid amount. Please try again");
					valid = false;
				}
			} catch (Exception e) {
				System.out.println("You didn't enter a integer. Please try again.");
			}
		}
		return count;

	}
}

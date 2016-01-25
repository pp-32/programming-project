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
	private Condition turnStarted = lock.newCondition();

	private QwirkleClient client;

	public TUIView(QwirkleClient client) {
		this.client = client;
	}

	public void run() {
		Scanner user_input = new Scanner(System.in);

		boolean inputValid = false;
		
		System.out.print("Enter your name: ");
		while(!inputValid) {
			try {
				String playerName = user_input.nextLine();
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
		boolean validInput = false;
		while (!validInput) {
			try {
				int playerCount = Integer.parseInt(user_input.nextLine());
				if(playerCount <= 4) {
					System.out.print("The number of players is: " + playerCount);
					System.out.println("");
					client.requestGame(playerCount);
					validInput = true;
				} else {
					validInput = false;
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
				// TODO: try catch block
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

	private void handlePlaceCommand() {
		List<Move> moves = new ArrayList<Move>();

		Scanner commandScanner = new Scanner(System.in);

		// place <aantal stenen> {<index> <x> <y> ....}
		System.out.print("How many stones do you want to place? ");
		int stonesCount = Integer.parseInt(commandScanner.nextLine());

		for (int i = 0; i < stonesCount; i++) {
			System.out.print("Which stone do you want to place? ");
			int stoneIndex = Integer.parseInt(commandScanner.nextLine());
			System.out.print("Enter the desired x-location for stone " + stoneIndex + " ");
			int x = Integer.parseInt(commandScanner.nextLine());
			System.out.print("Enter the desired y-location for stone " + stoneIndex + " ");
			int y = Integer.parseInt(commandScanner.nextLine());
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
		lock.lock();
		try {
			if (arg1 instanceof Game) {
					gameStarted.signalAll();
			} else if (arg0 instanceof Board) {
				System.out.println(((Board) arg0).toString());
			} else if (arg0 instanceof HumanPlayer) {
				HumanPlayer p = ((HumanPlayer) arg0);
				switch ((String) arg1) {
				case "stones":
					printStones(p.getStones());
					break;
				case "score":
					System.out.println("Player " + p.getName() + " has now " + p.getScore() + " score.");
					break;
				}
			} else if (arg0 instanceof Player) {
				Player p = ((Player)arg0);
				switch ((String) arg1) {
				case "score":
					System.out.println("Player " + p.getName() + " has now " + p.getScore() + " score.");
					break;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void showError(String reason) {
		System.out.println(reason);

	}
}

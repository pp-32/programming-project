package qwirkle.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import qwirkle.Board;
import qwirkle.Game;
import qwirkle.Location;
import qwirkle.Move;
import qwirkle.MoveResult;
import qwirkle.OpenHandPlayer;
import qwirkle.Player;
import qwirkle.Stone;
import qwirkle.View;

public class TUIView implements View {

	private Lock lock = new ReentrantLock();
	private boolean acceptedRequest;
	private Condition firstResponseReceived = lock.newCondition();
	private Condition gameStarted = lock.newCondition();

	private QwirkleClient client;

	public TUIView(QwirkleClient client) {
		this.client = client;
	}

	public void run() {
		System.out.println(" .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------. ");
		System.out.println("| .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |");
		System.out.println("| |    ___       | || | _____  _____ | || |     _____    | || |  _______     | || |  ___  ____   | || |   _____      | || |  _________   | |");
		System.out.println("| |  .'   '.     | || ||_   _||_   _|| || |    |_   _|   | || | |_   __ \\    | || | |_  ||_  _|  | || |  |_   _|     | || | |_   ___  |  | |");
		System.out.println("| | /  .-.  \\    | || |  | | /\\ | |  | || |      | |     | || |   | |__) |   | || |   | |_/ /    | || |    | |       | || |   | |_  \\_|  | |");
		System.out.println("| | | |   | |    | || |  | |/  \\| |  | || |      | |     | || |   |  __ /    | || |   |  __'.    | || |    | |   _   | || |   |  _|  _   | |");
		System.out.println("| | \\  `-'  \\_   | || |  |   /\\   |  | || |     _| |_    | || |  _| |  \\ \\_  | || |  _| |  \\ \\_  | || |   _| |__/ |  | || |  _| |___/ |  | |");
		System.out.println("| |  `.___.\\__|  | || |  |__/  \\__|  | || |    |_____|   | || | |____| |___| | || | |____||____| | || |  |________|  | || | |_________|  | |");
		System.out.println("| |              | || |              | || |              | || |              | || |              | || |              | || |              | |");
		System.out.println("| '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |");
		System.out.println(" '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------' ");
		Scanner userInput = new Scanner(System.in);

		// ask for name and request to join.
		String playerName = null;
		while (!acceptedRequest) {
			boolean inputValid = false;
			System.out.print("Enter your name: ");
			while (!inputValid) {
				playerName = userInput.nextLine();
				System.out.print("Your name is: " + playerName);
				System.out.println("");
				inputValid = !playerName.contains(" ");
			}
	
			client.requestJoin(playerName);
			lock.lock();
			try {
				firstResponseReceived.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}

		// ask for amount of players.
		System.out.println("You joined the server!");
		System.out.print("Enter the desired amount of players: ");
		int playerCount = readNextInt(1, 4);
		System.out.print("The number of players is: " + playerCount);
		System.out.println("");
		
		client.requestGame(playerCount);

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

		// start basic I/O loop
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
				case "chat":
					handleChatCommand(commandScanner);
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

	private void handleChatCommand(Scanner commandScanner) {
		System.out.print("Enter your message: ");
		Scanner messageScanner = new Scanner(System.in);
		client.sendChatMessage(messageScanner.nextLine());
	}

	private void handleTradeCommand(Scanner commandScanner) {
		if (!client.getIsMyTurn()) {
			showError("Wait for your turn");
			return;
		}
		
		List<Stone> stones = new ArrayList<Stone>();

		System.out.print("How many stones do you want to trade? ");

		int stonesCount = readNextInt(1, 6);

		for (int i = 0; i < stonesCount; i++) {
			System.out.print("Enter the index of the stone you want to trade. ");
			int stoneIndex = readNextInt(0, 5);
			stones.add(client.getPlayer().getStones().get(stoneIndex));
		}

		client.getPlayer().performTrade(stones);
	}

	private void handlePlaceCommand() {
		if (!client.getIsMyTurn()) {
			showError("Wait for your turn");
			return;
		}

		OpenHandPlayer player = client.getPlayer();
		List<Move> moves = new ArrayList<Move>();

		System.out.print("How many stones do you want to place? ");

		int stonesCount = readNextInt(1, 6);
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

			Move move = new Move(player.getStones().get(stoneIndex), location);
			moves.add(move);
		}

		MoveResult result = player.placeStones(client.getCurrentGame().getBoard(), moves);
		if (!result.isSuccessful()) {
			System.out.println("Invalid move!");
		}

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
				case "acceptedrequest":
					acceptedRequest = true;
					firstResponseReceived.signalAll();
					break;
				case "gamestarted":
					gameStarted.signalAll();
					break;
				case "placedstone":
					printBoard();
					break;
				case "turnstarted":
					System.out.println("Your turn has started!");
					break;
				case "stones":
					OpenHandPlayer hp = (OpenHandPlayer) arg0;
					printStones(hp.getStones());
					break;
				case "score":
					Player p = (Player) arg0;
					System.out.println("Player " + p.getName() 
									 + " has now " + p.getScore() + " score.");
					break;
				case "turnEnded":
					break;
				case "gameover":
					System.out.println("GAME OVER!");
					printStones(client.getPlayer().getStones());
					System.out.println("Your score: " + client.getPlayer().getScore());

					int input;
					boolean validInput = false;
					while (!validInput) {
						System.out.println("Do you want to play another game? [y/n]:");
						try {
							input = System.in.read();
							validInput = input == 'y' || input == 'n';
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					//continueGame = input == 'y';
					break;
			}
		} finally {
			lock.unlock();
		}
	}

	private void printBoard() {
		Game game = client.getCurrentGame();
		Board board = game.getBoard();
		
		System.out.println(board);
		System.out.println("There are " + board.getStoneCount() + " in the pile.");
		
		for (Player player : game.getPlayers()) {
			System.out.println("Player " + player.getName() 
							   + " has " + player.getHandSize() + " stones and"
							   + " has " + player.getScore() + " score.");
		}
	}

	@Override
	public void showError(String reason) {
		System.out.println(reason);
		if (!acceptedRequest) {
			lock.lock();
			firstResponseReceived.signalAll();
			lock.unlock();
		}
	}

	public int readNextInt(int min, int max) {
		int count = 0;
		boolean valid = false;
		Scanner commandScanner = new Scanner(System.in);
		while (!valid) {
			try {
				count = Integer.parseInt(commandScanner.nextLine());
				if (min <= count && count <= max) {
					valid = true;
				} else {
					System.out.println("You didn't enter a valid amount. Please try again.");
					valid = false;
				}
			} catch (NumberFormatException e) {
				System.out.println("You didn't enter a integer. Please try again.");
			}
		}
	
		return count;

	}
}

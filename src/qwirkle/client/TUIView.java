package qwirkle.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
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
import qwirkle.ScoreComparator;
import qwirkle.Stone;
import qwirkle.View;

public class TUIView implements View {

	private static final Random RANDOM = new Random();
	
	private Lock lock = new ReentrantLock();
	private boolean acceptedRequest;
	private Condition firstResponseReceived = lock.newCondition();
	private Condition gameStarted = lock.newCondition();

	private Scanner inputScanner = new Scanner(System.in);
	private QwirkleClient client;
	private boolean isGameOver = false;
	private boolean startOver = true;
	
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
		
		askForPlayerName();

		System.out.println("You joined the server!");
		
		while (startOver) {
			startOver = false;
			askForGameRequest();
			System.out.println("Waiting for game...");
			waitForGame();
			
			lock.lock();
			System.out.println("Game joined!");
			System.out.println(client.getCurrentGame().getBoard().toString());
			lock.unlock();
			
			// basic I/O loop
			boolean continueLoop = true;
			while (continueLoop) {
				continueLoop = inputScanner.hasNextLine()
							   && processCommand(inputScanner.nextLine());
			}
		}
		
		client.shutDown();
	}

	private void waitForGame() {
		lock.lock();
		try {
			gameStarted.await();
			isGameOver = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	private void askForPlayerName() {
		String playerName = null;
		while (!acceptedRequest) {
			boolean inputValid = false;
			System.out.print("Enter your name: ");
			while (!inputValid) {
				playerName = inputScanner.nextLine();
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
	}
	
	private void askForGameRequest() {
		System.out.print("Enter the desired amount of players: ");
		int playerCount = readNextInt(1, 4);
		System.out.print("The number of players is: " + playerCount);
		System.out.println("");
		
		client.requestGame(playerCount);
	}

	private boolean processCommand(String command) {		
		boolean continueLoop = true;
		try (Scanner commandScanner = new Scanner(command)) {
			switch (commandScanner.nextLine()) {
				case "place":
					handlePlaceCommand();
					break;
				case "trade":
					handleTradeCommand();
					break;
				case "hint":
					handleHintCommand();
					break;
				case "chat":
					handleChatCommand();
					break;
				case "exit":
					continueLoop = false;
					break;
				case "yes":
					if (isGameOver) {
						startOver = true;
						continueLoop = false;
					}
					break;
				case "no":
					if (isGameOver) {
						startOver = false;
						continueLoop = false;
					}
					break;
				default:
					System.out.println("You didn't enter a valid command. Please try again.");
					break;
			}
			
		}
		return continueLoop;
	}

	private void handleHintCommand() {
		System.out.println("~ HINT ~");
		Board board = client.getCurrentGame().getBoard();
		List<Move> possibleMoves = SmartStrategy.findStartingMoves(client.getPlayer(), board);
		
		if (possibleMoves.size() == 0) {
			System.out.println("You can't place any stones. You need to trade.");
		} else {
			Move random = possibleMoves.get(RANDOM.nextInt(possibleMoves.size()));
			System.out.println("You can place stone " + random.getStone() 
							 + " at " + random.getLocation());
		}
	}

	private void handleChatCommand() {
		if (!client.getSupportsChat()) {
			System.out.println("Server does not support chat messages!");
		} else {
			System.out.print("Enter your message: ");
			client.sendChatMessage(inputScanner.nextLine());
		}
	}

	private void handleTradeCommand() {
		if (!client.getIsMyTurn()) {
			showError("Wait for your turn");
			return;
		}
		
		List<Stone> stones = new ArrayList<Stone>();

		System.out.print("How many stones do you want to trade? ");

		int stonesCount = readNextInt(1, 6);

		if (!client.getCurrentGame().getBoard().canPickStones(stonesCount)) {
			showError("Can't pick " + stonesCount + " stones from the pile.");	
		} else {
			for (int i = 0; i < stonesCount; i++) {
				System.out.print("Enter the index of the stone you want to trade. ");
				int stoneIndex = readNextInt(0, 5);
				stones.add(client.getPlayer().getStones().get(stoneIndex));
			}
	
			client.getPlayer().performTrade(stones);
		}
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
					handleAcceptedRequest();
					break;
				case "gamestarted":
					gameStarted.signalAll();
					break;
				case "stones":
					printStones(client.getPlayer().getStones());
					break;
				case "turnstarted":
					System.out.println("Your turn has started!");
					System.out.print("Place, trade, hint, chat or exit?: ");
					break;
			}
		} finally {
			lock.unlock();
		}
	}
	
	private void handleAcceptedRequest() {
		acceptedRequest = true;
		firstResponseReceived.signalAll();
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
		
		printStones(client.getPlayer().getStones());
	}

	@Override
	public void notifyMove(Player player, List<Move> moves, int score) {
		printBoard();
		System.out.println("Player " + player.getName() + " earned " + score + " score!");
	}

	@Override
	public void notifyTrade(Player player, int stones) {
		printBoard();
		System.out.println("Player " + player.getName() + " traded " + stones + " stones.");		
	}
	
	@Override
	public void notifyConnectionLost(String clientName) {
		System.out.println("Connection with " + clientName + " was lost.");
		isGameOver = true;
		System.out.println("Do you want to play another game? [yes/no]:");
	}
	
	@Override
	public void notifyGameOver(List<Player> ranking) {
		System.out.println("~ GAME OVER ~");

		System.out.println("# | Name            | Score");
		System.out.println("--+-----------------+----------------");
		
		for (int i = 0; i < ranking.size(); i++) {
			Player player = ranking.get(i);
			System.out.println(String.format("%d | %-15s | %d",
								i + 1, 
								player.getName(), 
								player.getScore()));
		}
		
		isGameOver = true;
		System.out.print("Do you want to play another game? [yes/no]:");
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

	@Override
	public void showChatMessage(String playerName, String message) {
		System.out.println(String.format("[%s]: %s", playerName, message));
	}

	public int readNextInt(int min, int max) {
		int count = 0;
		boolean valid = false;
		while (!valid) {
			try {
				count = Integer.parseInt(inputScanner.nextLine());
				valid = min <= count && count <= max;
			} catch (NumberFormatException e) {
				valid = false;
			}
			
			if (!valid) {
				System.out.println("You didn't enter a valid amount. Please try again.");
				valid = false;
			}
		}
	
		return count;

	}

}

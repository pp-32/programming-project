package qwirkle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TUIView implements View {
	
	private QwirkleClient client;
	private Game game;
	
	public TUIView(QwirkleClient client) {
		this.client = client;
	}
		
	public void run() {
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
		// TODO Auto-generated method stub
		
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
}

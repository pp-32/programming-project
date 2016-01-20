package qwirkle;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TUIView implements View {
	
	private QwirkleClient client;
	private Game game;
	
	public TUIView(QwirkleClient client) {
		this.client = client;
	}
		
	public void run() {
		while (true) {
			try (Scanner scanner = new Scanner(System.in)) {
				boolean continueLoop = true;
				while (continueLoop) {
					System.out.print("Command:");
					continueLoop = scanner.hasNextLine() && processCommand(scanner.nextLine());
				}
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
			case "exit":
				continueLoop = false;
				break;
			}
		}
		return continueLoop;
	}

	private void handlePlaceCommand(Scanner commandScanner) {
		Map<Stone, Location> placements = new HashMap<Stone, Location>();
		
		int stonesCount = Integer.parseInt(commandScanner.next());
		
		for (int i = 0; i < stonesCount; i++) { 
			int stoneIndex = Integer.parseInt(commandScanner.next());
			int x = Integer.parseInt(commandScanner.next());
			int y = Integer.parseInt(commandScanner.next());
			Location location = new Location(x, y);
			
			// TODO: ask controller for hand and retrieve stones from there.
		}
		
		client.setMove(placements);
	}

	public void updateView() {
		
	}

	@Override
	public void buildView() {
		// TODO Auto-generated method stub
		
	}
}

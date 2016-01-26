package qwirkle.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import qwirkle.Board;
import qwirkle.Location;
import qwirkle.Move;
import qwirkle.Rectangle;
import qwirkle.Stone;

public class NaiveStrategy implements Strategy {

	private static final Random random = new Random();

	private int x = 0;
	
	@Override
	public String getName() {
		return "Naive";
	}

	@Override
	public void makeMove(ComputerPlayer player, Board board) {
		List<Move> moves = determineMoves(player, board);
		
		x %= 2;
		if (board.canPickStone() && (x == 0 || moves.size() == 0)) {
			System.out.println("[naive]: perform trade");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			player.performTrade(pickRandomStones(player.getStones()));
		} else {
			System.out.println("[naive]: place stones");
			player.placeStones(board, moves);
		}
	}

	private List<Stone> pickRandomStones(List<Stone> stones) {
		List<Stone> pickedStones = new ArrayList<Stone>();
		pickedStones.add(stones.get(0));
		return pickedStones;
	}

	public List<Move> determineMoves(ComputerPlayer player, Board board) {
		List<Move> moves = new ArrayList<Move>();
		
		Move firstMove = determineFirstMove(player, board);
		if (firstMove == null) {
			return moves;			
		} else {
			moves.add(firstMove);
		}
		
		
		return moves;
	}
	
	private Move determineFirstMove(ComputerPlayer player, Board board) {
		Rectangle dimensions = board.getDimensions().deepCopy();
		dimensions.inflate(1);
		Location topLeft = dimensions.getTopLeft();
		Location bottomRight = dimensions.getBottomRight();

		for (int y = topLeft.getY(); y >= bottomRight.getY(); y--) {
			for (int x = topLeft.getX(); x <= bottomRight.getX(); x++) {
				for (Stone s : player.getStones()) {
					Move m = new Move(s, x, y);
					if (board.checkMove(m)) {
						return m;
					}
				}
			}
		}
		
		return null;
	}
	
}

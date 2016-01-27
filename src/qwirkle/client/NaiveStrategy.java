package qwirkle.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import qwirkle.Board;
import qwirkle.Location;
import qwirkle.Move;
import qwirkle.Stone;

/**
 * Represents a naive strategy for playing the Qwirkle game.
 * @author Jerre
 *
 */
public class NaiveStrategy implements Strategy {

	private static final Random RANDOM = new Random();

	@Override
	public String getName() {
		return "Naive";
	}

	/**
	 * Places a random stone onto the board, or trades a random stone.
	 */
	@Override
	public void makeMove(ComputerPlayer player, Board board) {
		List<Move> moves = determineMoves(player, board);
		if (board.canPickStone() && moves.size() == 0) {
			player.performTrade(pickRandomStones(player.getStones()));
		} else {
			player.placeStones(board, moves);
		}
	}

	private static List<Stone> pickRandomStones(List<Stone> stones) {
		List<Stone> pickedStones = new ArrayList<Stone>();
		pickedStones.add(stones.get(RANDOM.nextInt(stones.size())));
		return pickedStones;
	}

	private static List<Move> determineMoves(ComputerPlayer player, Board board) {
		List<Move> moves = new ArrayList<Move>();
		
		Move firstMove = determineFirstMove(player, board);
		if (firstMove == null) {
			return moves;			
		} else {
			moves.add(firstMove);
		}
		
		return moves;
	}
	
	private static Move determineFirstMove(ComputerPlayer player, Board board) {
		
		if (board.isEmpty()) {
			return new Move(player.getStones().get(0), 0, 0);
		}
		
		for (Location occupiedField : board.getOccupiedFields()) {
			Move move = findMoveNearLocation(player, board, occupiedField);
			if (move != null) {
				return move;
			}
		}
		
		return null;
	}
	
	private static Move findMoveNearLocation(ComputerPlayer player, 
											 Board board, 
											 Location location) {
		for (int y = location.getY() + 1; y >= location.getY() - 1; y--) {
			for (int x = location.getX() - 1; x <= location.getX() + 1; x++) {
				Move move = findMoveForLocation(player, board, new Location(x, y));
				if (move != null) {
					return move;
				}
			}
		}
		return null;
	}
	
	private static Move findMoveForLocation(ComputerPlayer player, Board board, Location location) {
		for (Stone s : player.getStones()) {
			Move m = new Move(s, location);
			if (board.checkMove(m)) {
				return m;
			}
		}
		return null;
	}
	
}

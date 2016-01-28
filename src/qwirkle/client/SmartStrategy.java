package qwirkle.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import qwirkle.Board;
import qwirkle.Location;
import qwirkle.Move;
import qwirkle.OpenHandPlayer;
import qwirkle.Stone;

/**
 * Represents a smart strategy used in playing the Qwirkle game.
 * @author Jerre
 *
 */
public class SmartStrategy implements Strategy {

	private static final Random RANDOM = new Random();
	
	@Override
	public String getName() {
		return "Smart";
	}

	@Override
	public void makeMove(ComputerPlayer player, Board board) {
		
		List<Move> startingMoves = findStartingMoves(player, board);

		if (startingMoves.size() == 0) {
			// no starting points found, perform a trade.
			System.out.println("startingMoves == 0");
			player.performTrade(pickRandomStones(player.getStones()));
		} else {
			
			// TODO: find sequences.
			List<List<Move>> possibleMoves = new ArrayList<List<Move>>();
			for (Move start : startingMoves) {
				List<Move> possibleMove = new ArrayList<Move>();
				possibleMove.add(start);
				possibleMoves.add(possibleMove);
			}
			
			player.placeStones(board, findBestMove(board, possibleMoves));			
		}
	}

	private static List<Stone> pickRandomStones(List<Stone> stones) {
		List<Stone> pickedStones = new ArrayList<Stone>();
		pickedStones.add(stones.get(RANDOM.nextInt(stones.size())));
		return pickedStones;
	}

	private static List<Move> findBestMove(Board board, List<List<Move>> possibleMoves) {
		List<Move> best = null;
		int bestScore = 0;
		
		for (List<Move> moves : possibleMoves) {
			int score = board.calculateScore(moves);
			if (bestScore < score) {
				bestScore = score;
				best = moves;
			}
		}
		
		return best;
	}
	
	public static List<Move> findStartingMoves(OpenHandPlayer player, Board board) {

		List<Move> moves = new ArrayList<Move>();
		if (board.isEmpty()) {
			moves.addAll(findMovesForLocation(player, board, new Location(0, 0)));
		} else {		
			for (Location occupiedField : board.getOccupiedFields()) {
				moves.addAll(findMovesNearLocation(player, board, occupiedField));
			}
		}
		return moves;
	}
	
	private static List<Move> findMovesNearLocation(OpenHandPlayer player, 
													Board board, 
													Location location) {
		
		List<Move> moves = new ArrayList<Move>();
		for (int y = location.getY() + 1; y >= location.getY() - 1; y--) {
			for (int x = location.getX() - 1; x <= location.getX() + 1; x++) {
				moves.addAll(findMovesForLocation(player, board, new Location(x, y)));
			}
		}
		return moves;
	}

	private static List<Move> findMovesForLocation(OpenHandPlayer player, 
												   Board board,
												   Location location) {
		List<Move> moves = new ArrayList<Move>();
		for (Stone s : player.getStones()) {
			Move m = new Move(s, location);
			if (board.checkMove(m)) {
				moves.add(m);
			}
		}
		return moves;
	}
}

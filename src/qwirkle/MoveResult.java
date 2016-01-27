package qwirkle;

import java.util.List;

/**
 * Represents a result of a move.
 * @author Jerre
 *
 */
public class MoveResult {
	private List<Move> moves;
	private int score;
	
	/**
	 * Creates a new MoveResult instance.
	 * @param moves The moves that were made.
	 * @param score The score that was gained.
	 */
	public MoveResult(List<Move> moves, int score) {
		this.moves = moves;
		this.score = score;
	}
	
	/**
	 * Gets the moves that were made.
	 * @return The moves.
	 */
	public List<Move> getMoves() {
		return moves;
	}
	
	/**
	 * Gets the score that was gained.
	 * @return The score.
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Gets a value indicating whether the move was successful or not.
	 * @return True if the moves were succesful, false otherwise.
	 */
	public boolean isSuccessful() {
		return score != 0;
	}
}

package qwirkle;

import java.util.List;

public class MoveResult {
	private List<Move> moves;
	private int score;
	
	public MoveResult(List<Move> moves, int score) {
		this.moves = moves;
		this.score = score;
	}
	
	public List<Move> getMoves() {
		return moves;
	}
	
	public int getScore() {
		return score;
	}
	
	public boolean isSuccessful() {
		return score != 0;
	}
}

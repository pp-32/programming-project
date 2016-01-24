package qwirkle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Represents a board in the Qwirkle game.
 * @author Jerre
 *
 */
public abstract class Board extends Observable {

	private StoneShape[] shapes = new StoneShape[] {
			StoneShape.CIRCLE,
			StoneShape.CLUBS,
			StoneShape.CROSS,
			StoneShape.DIAMOND,
			StoneShape.RECTANGLE,
			StoneShape.STAR,
	};
	private StoneColor[] colors = new StoneColor[] {
			StoneColor.RED,
			StoneColor.BLUE,
			StoneColor.GREEN,
			StoneColor.ORANGE,
			StoneColor.PURPLE,
			StoneColor.YELLOW
	};
	
	private List<Stone> stones; 
	
	public Board() {
		stones = new ArrayList<Stone>();
		
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 6; k++) {
				for (int l = 0; l < 6; l++) {
					stones.add(new Stone(shapes[k], colors[l]));
				}
			}
		}
	}
	
	/**
	 * Creates a deep copy of the board.
	 * @return the copy.
	 */
	public abstract Board deepCopy();
		
	/**
	 * Resets the board, clearing all stones from the field.
	 */
	public abstract void reset();
	
	/**
	 * Gets the current dimensions of the board.
	 * @return the dimensions.
	 */
	public abstract Rectangle getDimensions();
	
	/**
	 * Gets the stone that is located at the given position.
	 * @param x The x coordinate of the position.
	 * @param y The y coordinate of the position.
	 * @return The stone, or null if the field is empty.
	 */
	public abstract Stone getField(int x, int y);

	/**
	 * Gets the stone that is located at the given position.
	 * @param location The position.
	 * @return The stone, or null if the field is empty.
	 */
	public Stone getField(Location location) {
		return getField(location.getX(), location.getY());
	}
	
	/**
	 * Determines whether a field at the given coordinates is empty or not.
	 * @param x The x coordinate of the position.
	 * @param y The y coordinate of the position.
	 * @return True if the field is empty, false otherwise.
	 */
	public boolean isEmptyField(int x, int y) {
		return getField(x, y) == null;
	}
	
	/**
	 * Determines whether the board is empty or not.
	 * @return True if the board is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return isEmptyField(0, 0);
	}
	
	/**
	 * Determines whether a stone can be picked up from the pile.
	 * @return True if the pile is not empty, false otherwise.
	 */
	public boolean canPickStone() {
		return stones.size() > 0;
	}

	/**
	 * Determines whether the given amount of stones can be picked up from the pile.
	 * @param amount the amount of stones to pick.
	 * @return True if the pile is not empty, false otherwise.
	 */
	public boolean canPickStones(int amount) {
		return stones.size() >= amount;
	}
	
	/**
	 * Grabs a stone from the pile.
	 * @return The Stone that was picked up.
	 */
	public Stone pickStone() { 
		Stone stone = stones.get((int)(Math.random() * stones.size()));
		stones.remove(stone);
		return stone;
	}
	
	/**
	 * Grabs a given amount of stones from the pile.
	 * @param amount The amount of stones to grab.
	 * @return The stones that were grabbed.
	 */
	public List<Stone> pickStones(int amount) { 
		List<Stone> stones = new ArrayList<Stone>();
		for (int i = 0; i < amount; i++) {
			stones.add(pickStone());
		}
		return stones;
	}
	
	/**
	 * Places a stone in the bag.
	 * @param stone the stone.
	 */
	public void placeStoneInBag(Stone stone) {
		stones.add(stone);
	}
	
	/**
	 * Determines whether a list of moves combine to a legal move.
	 * @param moves The moves to check.
	 * @return True if the moves are legal, false otherwise.
	 */
	public boolean checkMoves(List<Move> moves) {

		Board copy = deepCopy();
		
		for (Move m : moves) {
			if (copy.checkMove(m)) {
				copy.placeStone(m);
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Determines whether a placement of a specific stone at the given location
	 * is a legal move or not.
	 * @param move The move to check.
	 * @return True if the move is legal, false otherwise.
	 */
	public boolean checkMove(Move move) {
		int x = move.getLocation().getX();
		int y = move.getLocation().getY();
		
		if (isEmpty()) {
			return x == 0 && y == 0;
		} else if (!isEmptyField(x, y)) {
			return false;
		} else if (isEmptyField(x - 1, y) 
				&& isEmptyField(x + 1, y)
				&& isEmptyField(x, y - 1)
				&& isEmptyField(x, y + 1)) {
			return false;
		}
		
		Board copy = this.deepCopy();
		copy.placeStone(move);

		Sequence horizontal = new Sequence(copy, new Location(x, y), SequenceDirection.HORIZONTAL);
		Sequence vertical = new Sequence(copy, new Location(x, y), SequenceDirection.VERTICAL);
		
		return horizontal.isValid() && vertical.isValid();
	}
	
	/**
	 * Performs a move to the board.
	 * @param move The move to apply.
	 */
	public abstract void placeStone(Move move);

	/**
     * Performs a list of moves.
     * @param moves The moves to apply.
     */
	public void placeStones(List<Move> moves) {
		for (Move m : moves) {
			placeStone(m);
		}
		setChanged();
		notifyObservers(this);
	}

	public int calculateScore(List<Move> moves) {
		SequenceDirection direction;
		if (moves.size() == 1) {
			direction = SequenceDirection.UNKNOWN;
		} else {
			Location delta = moves.get(0).getLocation().deepCopy();
			delta.subtract(moves.get(1).getLocation());
			direction = Sequence.getDirectionFromVector(delta);
		}
		
		Board copy = this.deepCopy();
		copy.placeStones(moves);		
		Sequence horizontal = new Sequence(copy, moves.get(0).getLocation(), SequenceDirection.HORIZONTAL);
		Sequence vertical = new Sequence(copy, moves.get(0).getLocation(), SequenceDirection.VERTICAL);
		
		int score = 0;
		switch (direction) {
		case HORIZONTAL:
			score = horizontal.calculateScore();
			break;
		case VERTICAL:
			score = vertical.calculateScore();
			break;
		case UNKNOWN:
			if (horizontal.getLength() > 0 ) {
				score += horizontal.getLength(); 
			} else {
				score += vertical.getLength();
			}
			break;
		}
		
		return score;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Rectangle dimensions = getDimensions().deepCopy();		
		dimensions.inflate(1);
		for (int y = dimensions.getTopLeft().getY(); y >= dimensions.getBottomRight().getY(); y--) {
			builder.append("| ");
			for (int x = dimensions.getTopLeft().getX(); x <= dimensions.getBottomRight().getX(); x++) {
				Stone field = getField(x, y);
				if (field == null) {
					builder.append("  ");
				} else {
					builder.append(field.toString());
				}
				builder.append(" | ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}


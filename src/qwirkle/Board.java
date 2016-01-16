package qwirkle;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a board in the Qwirkle game.
 * @author Jerre
 *
 */
public abstract class Board {
	
	private List<Stone> stones; 
	
	public Board() {
		stones = new ArrayList<Stone>();
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
	 * Grabs a stone from the pile.
	 * @return The Stone that was picked up.
	 */
	public Stone pickStone() { 
		Stone stone = stones.get((int)(Math.random() * stones.size()));
		stones.remove(stone);
		return stone;
	}
	
	/**
	 * Places a stone in the bag.
	 * @param stone the stone.
	 */
	public void placeStoneInBag(Stone stone) {
		stones.add(stone);
	}
	
	/**
	 * Determines whether a placement of a specific stone at the given location
	 * is a legal move or not.
	 * @param stone The stone to place.
	 * @param x The x-coordinate of the field to place the stone on.
	 * @param y The y-coordinate of the field to place the stone on.
	 * @return True if the move is legal, false otherwise.
	 */
	public boolean checkMove(Stone stone, int x, int y) {
		if (isEmpty()) {
			return x == 0 && y == 0;
		} else if (!isEmptyField(x, y)) {
			return false;
		}

		Sequence leftNeighbors = new Sequence(this, new Location(x - 1, y), new Location(-1, 0));
		Sequence rightNeighbors = new Sequence(this, new Location(x + 1, y), new Location(1, 0));
		//Sequence topNeighbors = new Sequence(this, new Location(x, y + 1), new Location(0, 1));
		//Sequence bottomNeighbors = new Sequence(this, new Location(x, y - 1), new Location(0, -1));
		
		boolean validMove = false;
		
		if (leftNeighbors.getStones().size() != 0 
			&& rightNeighbors.getStones().size() != 0) {
			// special case.
			
			if (leftNeighbors.getType() == SequenceType.UNKNOWN
				|| rightNeighbors.getType() == SequenceType.UNKNOWN) {
				
			}
				
		} else if (leftNeighbors.getStones().size() != 0) {
			validMove = checkSequence(leftNeighbors, stone);
		} else if (rightNeighbors.getStones().size() != 0) {
			validMove = checkSequence(rightNeighbors, stone);
		}
		
		return validMove;
	}
	
	private boolean checkSequence(Sequence neighborSequence, Stone stoneToPlace) {
		if (neighborSequence.getStones().size() == 0 
			|| neighborSequence.getStones().contains(stoneToPlace)) { 
			return false;
		}
		
		switch (neighborSequence.getType()) {
		case UNKNOWN:
			return true;
		case SHAPE:
			return stoneToPlace.getShape() == neighborSequence.getStones().get(0).getShape();
		case COLOR:
			return stoneToPlace.getColor() == neighborSequence.getStones().get(0).getColor();
		}
		
		return false;
	}
	
	/**
	 * Places a stone at the given location.
	 * @param stone The stone to place.
	 * @param x The x-coordinate of the field to place the stone on.
	 * @param y The y-coordinate of the field to place the stone on.
	 */
	public abstract void placeStone(Stone stone, int x, int y);

	private class Sequence {
		private Board board;
		private Location location;
		private Location directionVector;
		private List<Stone> stones;
		
		public Sequence(Board board, Location location, Location directionVector) {
			this.board = board;
			this.location = location;
			this.directionVector = directionVector;
		}
		
		public List<Stone> getStones() {
			if (stones != null) {
				return stones;
			}
			
			stones = new ArrayList<Stone>();
			Stone currentStone;
			while ((currentStone = board.getField(location)) != null) {
				stones.add(currentStone);
				location.add(directionVector);
			}
			return stones;
		}
		
		public SequenceType getType() {
			List<Stone> stones = getStones();
			if (stones.size() <= 1) {
				return SequenceType.UNKNOWN;
			} else if (stones.get(0).getShape() == stones.get(1).getShape()) {
				return SequenceType.SHAPE;
			} else {			
				return SequenceType.COLOR;
			}
		}
	}
	
	private enum SequenceType {
		UNKNOWN,
		SHAPE,
		COLOR
	}
}


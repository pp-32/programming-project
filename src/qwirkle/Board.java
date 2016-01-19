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
		} else if (isEmptyField(x - 1, y) 
				&& isEmptyField(x + 1, y)
				&& isEmptyField(x, y - 1)
				&& isEmptyField(x, y + 1)) {
			return false;
		}
		
		Board copy = this.deepCopy();
		copy.placeStone(stone, x, y);

		Sequence horizontal = new Sequence(copy, new Location(x, y), SequenceDirection.HORIZONTAL);
		Sequence vertical = new Sequence(copy, new Location(x, y), SequenceDirection.VERTICAL);
		
		return horizontal.isValid() && vertical.isValid();
	}
	
	/**
	 * Places a stone at the given location.
	 * @param stone The stone to place.
	 * @param x The x-coordinate of the field to place the stone on.
	 * @param y The y-coordinate of the field to place the stone on.
	 */
	public abstract void placeStone(Stone stone, int x, int y);

	public String toString() {
		StringBuilder builder = new StringBuilder();
		Rectangle dimensions = getDimensions();
		builder.append(dimensions.toString() + "\n");		
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
	
	private class Sequence {
		private Board board;
		private Location location;
		private SequenceDirection direction;
		private List<Stone> stones;
		
		public Sequence(Board board, Location location, SequenceDirection direction) {
			this.board = board;
			this.location = location;
			this.direction = direction;
		}
		
		public List<Stone> getStones() {
			if (stones != null) {
				return stones;
			}
			
			stones = new ArrayList<Stone>();
			
			switch (direction) {
			case HORIZONTAL:
				collectStones(new Location(-1, 0));
				stones.add(board.getField(location));
				collectStones(new Location(1, 0));
				break;
			case VERTICAL: 
				collectStones(new Location(0, 1));
				stones.add(board.getField(location));
				collectStones(new Location(0, -1));
				break;
			}
			return stones;
		}
		
		private void collectStones(Location vector) {
			Location current = location.deepCopy();
			current.add(vector);
			Stone currentStone;
			while ((currentStone = board.getField(current)) != null) {
				stones.add(currentStone);
				current.add(vector);
			}
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
		
		public boolean isValid() {
			if (getStones().size() > 6) {
				return false;
			}

			List<Stone> stones = getStones();
			
			StoneShape shape = stones.get(0).getShape();
			StoneColor color = stones.get(0).getColor();
			
			for (int i = 0; i < stones.size(); i++) {
				Stone stone = stones.get(i);
				
				// check for duplicate
				if (stones.indexOf(stone) != stones.lastIndexOf(stone)) {
					return false;
				}
			
				// check for shape or color, depending on sequence type.
				if (getType() == SequenceType.SHAPE) {
					if (stone.getShape() != shape) {
						return false;
					}
				} else if (getType() == SequenceType.COLOR) {
					if (stone.getColor() != color) {
						return false;
					}
				}
			}
			
			return true;
		}
	}
	
	private enum SequenceType {
		UNKNOWN,
		SHAPE,
		COLOR
	}
	
	private enum SequenceDirection {
		HORIZONTAL,
		VERTICAL
	}
}


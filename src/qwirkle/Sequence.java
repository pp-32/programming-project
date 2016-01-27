package qwirkle;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sequence of stones on a Qwirkle board.
 * @author Jerre
 *
 */
public class Sequence {
	private Board board;
	private Location location;
	private SequenceDirection direction;
	private List<Move> moves;
	 
	/**
	 * Creates a new Sequence instance.
	 * @param board The board the sequence is located at.
	 * @param location The location of one stone in the sequence.
	 * @param direction The direction of the sequence.
	 */
	public Sequence(Board board, Location location, SequenceDirection direction) {
		this.board = board;
		this.location = location;
		this.direction = direction;
		
		moves = new ArrayList<Move>();
		
		switch (direction) {
			case UNKNOWN:
				moves.add(new Move(board.getField(location), location));
				break;
			case HORIZONTAL:
				collectStones(new Location(-1, 0));
				moves.add(new Move(board.getField(location), location));
				collectStones(new Location(1, 0));
				break;
			case VERTICAL: 
				collectStones(new Location(0, 1));
				moves.add(new Move(board.getField(location), location));
				collectStones(new Location(0, -1));
				break;
		}
	}
	
	/**
	 * Gets the moves required to lay the sequence.
	 * @return The moves.
	 */
	public List<Move> getMoves() {
		return moves;		
	}

	/**
	 * Gets the direction of the sequence.
	 * @return The direction.
	 */
	public SequenceDirection getDirection() {
		return direction;
	}
	
	/**
	 * Gets the length of the sequence.
	 * @return The length.
	 */
	public int getLength() {
		return moves.size();
	}
	
	/**
	 * Gets the type of the sequence.
	 * @return The type.
	 */
	public SequenceType getType() {
		if (getLength() <= 1) {
			return SequenceType.UNKNOWN;
		} else if (moves.get(0).getStone().getShape() == moves.get(1).getStone().getShape()) {
			return SequenceType.SHAPE;
		} else {			
			return SequenceType.COLOR;
		}
	}
	
	private void collectStones(Location vector) {
		Location current = location.deepCopy();
		current.add(vector);
		Stone currentStone;
		while ((currentStone = board.getField(current)) != null) {
			moves.add(new Move(currentStone, current.deepCopy()));
			current.add(vector);
		}
	}
		
	/** 
	 * Determines whether the sequence is valid or not.
	 * @return True if the sequence is valid, false otherwise.
	 */
	public boolean isValid() {
		if (getLength() > 6) {
			return false;
		}
		
		StoneShape shape = moves.get(0).getStone().getShape();
		StoneColor color = moves.get(0).getStone().getColor();
		
		for (int i = 0; i < moves.size(); i++) {
			Stone stone = moves.get(i).getStone();
			
			// check for duplicate
			for (int j = i + 1; j < moves.size(); j++) {
				if (moves.get(j).getStone().equals(stone)) {
					return false;
				}
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
	
	/**
	 * Gets the sequence direction from a direction vector.
	 * @param vector The vector to get the direction from.
	 * @return The direction.
	 */
	public static SequenceDirection getDirectionFromVector(Location vector) {
		if (vector.getX() != 0 && vector.getY() == 0) {
			return SequenceDirection.HORIZONTAL;
		} else if (vector.getX() == 0 && vector.getY() != 0) {
			return SequenceDirection.VERTICAL;
		} else {
			return SequenceDirection.UNKNOWN;
		}
	}

}

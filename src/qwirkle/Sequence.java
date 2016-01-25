package qwirkle;

import java.util.ArrayList;
import java.util.List;

import qwirkle.SequenceDirection;
import qwirkle.SequenceType;

public class Sequence {
	private Board board;
	private Location location;
	private SequenceDirection direction;
	private List<Move> moves;
	 
	public Sequence(Board board, Location location, SequenceDirection direction) {
		this.board = board;
		this.location = location;
		this.direction = direction;
		
		moves = new ArrayList<Move>();
		
		// TODO: add unknown case (exception ?)
		switch (direction) {
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
	
	public List<Move> getMoves() {
		return moves;		
	}

	public SequenceDirection getDirection() {
		return direction;
	}
	
	public int getLength() {
		return moves.size();
	}
	
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

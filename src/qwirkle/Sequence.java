package qwirkle;

import java.util.ArrayList;
import java.util.List;

import qwirkle.SequenceDirection;
import qwirkle.SequenceType;

public class Sequence {
	private Board board;
	private Location location;
	private SequenceDirection direction;
	private List<Stone> stones;
	
	public Sequence(Board board, Location location, SequenceDirection direction) {
		this.board = board;
		this.location = location;
		this.direction = direction;
		
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
	}
	
	public List<Stone> getStones() {
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
	
	public static SequenceDirection getDirectionFromVector(Location vector) {
		if (vector.getX() != 0 && vector.getY() == 0) {
			return SequenceDirection.VERTICAL;
		} else if (vector.getX() == 0 && vector.getY() != 0) {
			return SequenceDirection.HORIZONTAL;
		} else {
			return SequenceDirection.UNKNOWN;
		}
	}
}

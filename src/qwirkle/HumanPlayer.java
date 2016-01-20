package qwirkle;

import java.util.List;

public class HumanPlayer extends Player {

	public HumanPlayer(String theName) {
		super(theName);
	}

	public List<Stone> getStones() {
		return this.stones;
	}
}

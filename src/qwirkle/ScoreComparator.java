package qwirkle;

import java.util.Comparator;

/**
 * Represents a player comparator that compares the score of players, 
 * favoring the player with the highest score.
 * @author Jerre
 *
 */
public class ScoreComparator implements Comparator<Player> {

	@Override
	public int compare(Player o1, Player o2) {
		if (o1.getScore() > o2.getScore()) {
			return -1;
		} else if (o1.getScore() < o2.getScore()) {
			return 1;
		} else {		
			return 0;
		}
	}

}

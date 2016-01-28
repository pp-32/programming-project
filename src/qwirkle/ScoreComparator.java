package qwirkle;

import java.util.Comparator;

/**
 * 
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

package qwirkle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Represents a Qwirkle game. 
 * @author Jerre
 *
 */
public class Game extends Observable implements Runnable {

	// -- Instance variables -----------------------------------------

	/**
	 * The board.
	 */
	private Board board;

	/**
	 * The players of the game.
	 */
	private List<Player> players;

	/**
	 * Index of the current player.
	 */
	private int current;
	
	/**
	 * Creates a new <code>Game</code> object.
	 */
	public Game(List<Player> players) {
		board = new ArrayBoard();
		this.players = new ArrayList<Player>();
		this.players.addAll(players);
	}
	
	/**
	 * Gets the board of the game.
	 * @return The board.
	 */
	public Board getBoard() {
		return board;
	}
	
	/**
	 * Gets the players playing the game.
	 * @return The players
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Runs the basic loop of the game.
	 */
	public void run() {
		while (!gameOver()) {
			getCurrentPlayer().makeMove(board);
			nextPlayer();
		}
		setChanged();
		notifyObservers("gameover");
	}
	
	/**
	 * Instructs the game to hand over the turn to the next player.
	 */
	private void nextPlayer() {
		current = (current + 1) % players.size();
	}
	
	/**
	 * Gets the current player that is making a move.
	 * @return the player.
	 */
	public Player getCurrentPlayer() {
		return players.get(current);
	}
		
	/**
	 * Gets the player by its name.
	 * @param name The name of the player.
	 * @return The player, or null if no player exists with the given name.
	 */
	public Player getPlayerByName(String name) {
		for (Player player : getPlayers()) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}
	/**
	 * Returns true if the game is over. This is the case if a player has used
	 * up all his/her stones or if the bag is empty and no more moves can be done.
	 * 
	 * @return true if the game is over
	 */
	public boolean gameOver() {
		if (getBoard().canPickStone()) {
			return false;
		}
		for (Player p : players) {
			if (p.getHandSize() == 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if a player has won. A player wins if he/she has the highest score.
	 * @param player The player to check.
	 * @return True if the player has won, false otherwise.
	 */
	public boolean isWinner(Player player) {
		for (Player other : getPlayers()) {
			if (other.getScore() > player.getScore()) {
				return false;
			}
		}
		return true;
	}


}

package qwirkle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Game extends Observable {

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
	
	public Board getBoard() {
		return board;
	}
	
	public HumanPlayer getHumanPlayer() {
		for (Player p : players) {
			if (p instanceof HumanPlayer) {
				return (HumanPlayer)p;
			}
		}
		return null;
	}

	/**
	 * Starts the Qwirkle game. Asks after each ended game if the user wants to
	 * continue. Continues until the user does not want to play anymore.
	 */
	public void start() {

	}

	public void waitForTurn() {
		// 
	}
	
	/**
	 * Prints the game situation.
	 */
	private void update() {

	}
	
	/**
	 * 
	 */
	private void nextPlayer() {
		
	}
	
	/**
	 * Resets the game. 
	 */
	public void reset() {
		
	}
	
	/**
	 * Prints the score.
	 */
	public void updateScore() {
		
	}
	
	/**
	 * Returns true if the game is over. This is the case if a player has used
	 * up all his/her stones or if the bag is empty and no more moves can be done.
	 * 
	 * @return true if the game is over
	 */
	public boolean gameOver() {
		return false;
	}
	
	/**
	 * Checks if a player has won. A player wins if he/she has the highest score.
	 * @return
	 */
	public boolean isWinner() {
		return false;
	}

	public List<Player> getPlayers() {
		return players;
	}

}

package qwirkle;

import java.util.List;
import java.util.Observer;

/**
 * Represents a view for the Qwirkle game.
 * @author Jerre
 *
 */
public interface View extends Runnable, Observer {
	
	/**
	 * Notifies the view the connection was lost with another player.
	 * @param clientName The name of the player.
	 */
	public void notifyConnectionLost(String clientName);
	
	/**
	 * Notifies the view a player has made a move.
	 * @param player The player who made the move.
	 * @param moves The moves that were made.
	 * @param score The score that was gained.
	 */
	public void notifyMove(Player player, List<Move> moves, int score);
	
	/**
	 * Notifies the view a player has traded a specified amount of stones.
	 * @param player Theplayer who made the trade.
	 * @param stones The amount of stones that were traded.
	 */
	public void notifyTrade(Player player, int stones);

	/**
	 * Notifies the view the game is over.
	 * @param ranking The ranking of the game, starting with the player with the most score.
	 */
	public void notifyGameOver(List<Player> ranking);
	
	/**
	 * Shows a chat message that was sent by a user.
	 * @param playerName The name of the sender.
	 * @param message The message.
	 */
	public void showChatMessage(String sender, String message);
	
	/**
	 * Shows an error message.
	 * @param reason The error message.
	 */
	public void showError(String reason);
}

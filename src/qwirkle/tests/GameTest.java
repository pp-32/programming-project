package qwirkle.tests;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import qwirkle.Game;
import qwirkle.OpenHandPlayer;
import qwirkle.Player;
import qwirkle.Stone;
import qwirkle.client.ComputerPlayer;
import qwirkle.client.NaiveStrategy;

public class GameTest {

	private static final String PLAYER1 = "Alice";
	private static final String PLAYER2 = "Bob";
	
	private OpenHandPlayer player1;
	private OpenHandPlayer player2;
	private List<Player> players;
	private Game game;
	
	@Before
	public void setup() {
		player1 = new ComputerPlayer(PLAYER1, new NaiveStrategy());
		player2 = new ComputerPlayer(PLAYER2, new NaiveStrategy());
		players = Arrays.asList(new Player[] {player1, player2});
		game = new Game(players);
		
	}
	
	@Test
	public void testPlayers() {
		assertEquals(players.size(), game.getPlayers().size());
		game.getPlayers().containsAll(players);
	}
	
	@Test
	public void testGetPlayerByName() {
		assertEquals(player1, game.getPlayerByName(PLAYER1));
		assertEquals(player2, game.getPlayerByName(PLAYER2));
	}
	
	@Test
	public void testCurrentPlayer() {
		for (int i = 0; i < players.size(); i++) {
			assertEquals(players.get(i), game.getCurrentPlayer());
			game.nextPlayer();
		}
		assertEquals(players.get(0), game.getCurrentPlayer());
	}
	
	@Test
	public void testGameOver() {
		Stone stone = game.getBoard().pickStone();
		player1.getStones().add(stone);
		player2.getStones().add(game.getBoard().pickStone());
		assertFalse(game.gameOver());
		player1.getStones().remove(stone);
		assertFalse(game.gameOver());
		game.getBoard().pickStones(game.getBoard().getStoneCount());
		assertTrue(game.gameOver());
	}
	
	@Test
	public void testIsWinner() {
		game.getBoard().pickStones(game.getBoard().getStoneCount());
		player1.setScore(1);
		player2.setScore(0);
		assertTrue(game.isWinner(player1));
		assertFalse(game.isWinner(player2));		
	}

}

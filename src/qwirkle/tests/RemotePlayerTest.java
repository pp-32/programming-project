package qwirkle.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import qwirkle.RemotePlayer;

public class RemotePlayerTest {

	private static final String PLAYERNAME = "Me";
	private RemotePlayer remotePlayer;
	
	@Before
	public void setup() {
		remotePlayer = new RemotePlayer(PLAYERNAME);
	}
	
	@Test
	public void testInitial() {
		assertEquals(PLAYERNAME, remotePlayer.getName());
		assertEquals(0, remotePlayer.getScore());
		assertEquals(0, remotePlayer.getHandSize());
	}
	
	@Test
	public void testUpdateScore() {
		assertEquals(0, remotePlayer.getScore());
		remotePlayer.setScore(3);
		assertEquals(3, remotePlayer.getScore());
	}
	
	@Test
	public void testUpdateHandSize() {
		assertEquals(0, remotePlayer.getHandSize());
		remotePlayer.setHandSize(6);
		assertEquals(6, remotePlayer.getHandSize());
	}

}

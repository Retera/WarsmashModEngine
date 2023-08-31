package com.etheller.warsmash.networking;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WarsmashServerLogicTest {

	private static final long PLAYER_ONE_SESSION = 1337001L;
	private static final long PLAYER_TWO_SESSION = 1337002L;
	private static final SocketAddress PLAYER_ONE_ADDR = new InetSocketAddress("warsmash.net", 13001);
	private static final SocketAddress PLAYER_TWO_ADDR = new InetSocketAddress("warsmash.net", 13002);
	MockServerWriter mockServerWriter;
	WarsmashServerLogic serverLogic;
	Set<SocketAddress> socketAddressesKnown;
	Map<Long,Integer> sessionTokenToPermittedSlot;
	
	@Before
	public void setup() {
		mockServerWriter = new MockServerWriter();
		socketAddressesKnown = new HashSet<>();
		sessionTokenToPermittedSlot = new HashMap<>();
		sessionTokenToPermittedSlot.put(PLAYER_ONE_SESSION, 0);
		sessionTokenToPermittedSlot.put(PLAYER_TWO_SESSION, 1);
		serverLogic = new WarsmashServerLogic(mockServerWriter, sessionTokenToPermittedSlot, socketAddressesKnown);
	}

	@Test
	public void testStartGame() {
		serverLogic.joinGame(PLAYER_ONE_ADDR, PLAYER_ONE_SESSION);
		assertEquals(0, mockServerWriter.getSentState().startGameCount);
		serverLogic.joinGame(PLAYER_TWO_ADDR, PLAYER_TWO_SESSION);
		assertEquals(1, mockServerWriter.getSentState().startGameCount);
	}
	
	@Test
	public void test10Turns() {
		testStartGame();
		
		for(int i = 1; i <= 10; i++) {
			serverLogic.finishedTurn(PLAYER_ONE_ADDR, PLAYER_ONE_SESSION, i);
			serverLogic.finishedTurn(PLAYER_TWO_ADDR, PLAYER_TWO_SESSION, i);
			assertEquals(i+1, mockServerWriter.getSentState().finishedTurnCount);
			assertEquals(i, mockServerWriter.getSentState().lastFinishTurnTick);
		}
	}
	
	@Test
	public void test10000Turns() {
		testStartGame();
		
		for(int i = 1; i <= 10000; i++) {
			serverLogic.finishedTurn(PLAYER_ONE_ADDR, PLAYER_ONE_SESSION, i);
			serverLogic.finishedTurn(PLAYER_TWO_ADDR, PLAYER_TWO_SESSION, i);
			assertEquals(i+1, mockServerWriter.getSentState().finishedTurnCount);
			assertEquals(i, mockServerWriter.getSentState().lastFinishTurnTick);
		}
	}
	
	@Test
	public void testAlternatingTurns() {
		testStartGame();
		
		for(int i = 1; i <= 10000; i++) {
			if ((i % 2) == 1) {
				serverLogic.finishedTurn(PLAYER_TWO_ADDR, PLAYER_TWO_SESSION, i);
				assertEquals(i-1, mockServerWriter.getSentState().lastFinishTurnTick);
				serverLogic.finishedTurn(PLAYER_ONE_ADDR, PLAYER_ONE_SESSION, i);
			} else {
				serverLogic.finishedTurn(PLAYER_ONE_ADDR, PLAYER_ONE_SESSION, i);
				assertEquals(i-1, mockServerWriter.getSentState().lastFinishTurnTick);
				serverLogic.finishedTurn(PLAYER_TWO_ADDR, PLAYER_TWO_SESSION, i);
			}
			assertEquals(i+1, mockServerWriter.getSentState().finishedTurnCount);
			assertEquals(i, mockServerWriter.getSentState().lastFinishTurnTick);
		}
	}
}

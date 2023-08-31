package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import net.warsmash.networking.udp.OrderedUdpServer;
import net.warsmash.uberserver.GamingNetwork;

public class WarsmashServerLogic implements ClientToServerListener {
	private static final boolean VERBOSE_LOGGING = false;
	private static final int MAGIC_DELAY_OFFSET = 0;
	private final Set<SocketAddress> socketAddressesKnown;
	private final Map<Long, Integer> sessionTokenToPermittedSlot;
	private final Map<SocketAddress, Integer> clientToTurnFinished = new HashMap<>();
	private final List<Runnable> turnActions = new ArrayList<>();
	private final SendableServerToClientListener writer;
	private int currentTurnTick = MAGIC_DELAY_OFFSET;
	private boolean gameStarted = false;
	private long lastServerHeartbeatTime = 0;
	private int joinCount = 0;

	public WarsmashServerLogic(final SendableServerToClientListener writer, final Map<Long, Integer> sessionTokenToPermittedSlot, final Set<SocketAddress> socketAddressesKnown) {
		this.writer = writer;
		this.sessionTokenToPermittedSlot = sessionTokenToPermittedSlot;
		this.socketAddressesKnown = socketAddressesKnown;
	}


	public void startGame() {
		this.gameStarted = true;
		WarsmashServerLogic.this.writer.startGame();
		WarsmashServerLogic.this.writer.send();
		startTurn();
	}

	private void startTurn() {
		System.out.println("sending finishedTurn " + this.currentTurnTick);
		WarsmashServerLogic.this.writer.finishedTurn(this.currentTurnTick);
		WarsmashServerLogic.this.writer.send();
		this.currentTurnTick++;
	}

	private int getPlayerIndex(final SocketAddress sourceAddress, final long sessionToken) {
		final Integer permittedSlot = this.sessionTokenToPermittedSlot.get(sessionToken);
		if (permittedSlot != null) {
			this.socketAddressesKnown.add(sourceAddress);
			return permittedSlot;
		}
		System.err.println("received bad session token during game: " + sessionToken);
		return -1;
	}

	@Override
	public void joinGame(final SocketAddress sourceAddress, final long sessionToken) {
		System.out.println("joinGame " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress, sessionToken);
		if (playerIndex == -1) {
			return;
		}
		WarsmashServerLogic.this.writer.acceptJoin(playerIndex);
		WarsmashServerLogic.this.writer.send(sourceAddress);

		this.joinCount++;
		if (this.joinCount == this.sessionTokenToPermittedSlot.size()) {
			startGame();
		}
	}

	@Override
	public void issueTargetOrder(final SocketAddress sourceAddress, final long sessionToken, final int unitHandleId,
			final int abilityHandleId, final int orderId, final int targetHandleId, final boolean queue) {
		System.out.println("issueTargetOrder from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress, sessionToken);
		if (playerIndex == -1) {
			return;
		}
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServerLogic.this.writer.issueTargetOrder(playerIndex, unitHandleId, abilityHandleId, orderId,
						targetHandleId, queue);
				WarsmashServerLogic.this.writer.send();
			}
		});
	}

	@Override
	public void issuePointOrder(final SocketAddress sourceAddress, final long sessionToken, final int unitHandleId,
			final int abilityHandleId, final int orderId, final float x, final float y, final boolean queue) {
		System.out.println("issuePointOrder from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress, sessionToken);
		if (playerIndex == -1) {
			return;
		}
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServerLogic.this.writer.issuePointOrder(playerIndex, unitHandleId, abilityHandleId, orderId, x, y,
						queue);
				WarsmashServerLogic.this.writer.send();
			}
		});
	}

	@Override
	public void issueDropItemAtPointOrder(final SocketAddress sourceAddress, final long sessionToken,
			final int unitHandleId, final int abilityHandleId, final int orderId, final int targetHandleId,
			final float x, final float y, final boolean queue) {
		System.out.println("issueDropItemAtPointOrder from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress, sessionToken);
		if (playerIndex == -1) {
			return;
		}
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServerLogic.this.writer.issueDropItemAtPointOrder(playerIndex, unitHandleId, abilityHandleId,
						orderId, targetHandleId, x, y, queue);
				WarsmashServerLogic.this.writer.send();
			}
		});
	}

	@Override
	public void issueDropItemAtTargetOrder(final SocketAddress sourceAddress, final long sessionToken,
			final int unitHandleId, final int abilityHandleId, final int orderId, final int targetHandleId,
			final int targetHeroHandleId, final boolean queue) {
		System.out.println("issueDropItemAtTargetOrder from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress, sessionToken);
		if (playerIndex == -1) {
			return;
		}
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServerLogic.this.writer.issueDropItemAtTargetOrder(playerIndex, unitHandleId, abilityHandleId,
						orderId, targetHandleId, targetHeroHandleId, queue);
				WarsmashServerLogic.this.writer.send();
			}
		});
	}

	@Override
	public void issueImmediateOrder(final SocketAddress sourceAddress, final long sessionToken, final int unitHandleId,
			final int abilityHandleId, final int orderId, final boolean queue) {
		System.out.println("issueImmediateOrder from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress, sessionToken);
		if (playerIndex == -1) {
			return;
		}
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServerLogic.this.writer.issueImmediateOrder(playerIndex, unitHandleId, abilityHandleId, orderId,
						queue);
				WarsmashServerLogic.this.writer.send();
			}
		});
	}

	@Override
	public void unitCancelTrainingItem(final SocketAddress sourceAddress, final long sessionToken,
			final int unitHandleId, final int cancelIndex) {
		System.out.println("unitCancelTrainingItem from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress, sessionToken);
		if (playerIndex == -1) {
			return;
		}
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServerLogic.this.writer.unitCancelTrainingItem(playerIndex, unitHandleId, cancelIndex);
				WarsmashServerLogic.this.writer.send();
			}
		});
	}

	@Override
	public void finishedTurn(final SocketAddress sourceAddress, final long sessionToken, final int clientGameTurnTick) {
		final int gameTurnTick = clientGameTurnTick + MAGIC_DELAY_OFFSET;
		if (VERBOSE_LOGGING) {
			System.out.println("finishedTurn(" + gameTurnTick + ") from " + sourceAddress);
		}
		if (!this.gameStarted) {
			throw new IllegalStateException(
					"Client should not send us finishedTurn() message when game has not started!");
		}
		this.clientToTurnFinished.put(sourceAddress, gameTurnTick);
		boolean allDone = true;
		for (final SocketAddress clientAddress : this.socketAddressesKnown) {
			final Integer turnFinishedValue = this.clientToTurnFinished.get(clientAddress);
			if ((turnFinishedValue == null) || (turnFinishedValue < gameTurnTick)) {
				allDone = false;
			}
		}
		if (allDone) {
			for (final Runnable turnAction : this.turnActions) {
				turnAction.run();
			}
			this.turnActions.clear();
			startTurn();
		}
	}

	@Override
	public void framesSkipped(final long sessionToken, final int nFramesSkipped) {
		if (this.sessionTokenToPermittedSlot.containsKey(sessionToken)) {
			// dont care for now
			final long currentTimeMillis = System.currentTimeMillis();
			if ((currentTimeMillis - this.lastServerHeartbeatTime) > 3000) {
				// 3 seconds of frame skipping, make sure we keep in contact with client
				System.out.println("sending server heartbeat()");
				WarsmashServerLogic.this.writer.heartbeat();
				WarsmashServerLogic.this.writer.send();
				this.lastServerHeartbeatTime = currentTimeMillis;
			}
		}
	}

	public static void main(final String[] args) {
		try {
			final WarsmashServer server = new WarsmashServer(GamingNetwork.UDP_SINGLE_GAME_PORT,
					Collections.emptyMap());
			server.startThread();

			final Scanner scanner = new Scanner(System.in);
			while (scanner.hasNextLine()) {
				final String line = scanner.nextLine();
				if ("start".equals(line)) {
					server.startGame();
					break;
				}
			}
			scanner.close();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
}

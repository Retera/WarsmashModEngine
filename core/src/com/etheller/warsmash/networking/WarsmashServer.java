package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.etheller.warsmash.util.WarsmashConstants;

import net.warsmash.networking.udp.OrderedUdpServer;

public class WarsmashServer implements ClientToServerListener {
	private static final int MAGIC_DELAY_OFFSET = 4;
	private final OrderedUdpServer udpServer;
	private final Map<SocketAddress, Integer> socketAddressToPlayerIndex = new HashMap<>();
	private final Map<SocketAddress, Integer> clientToTurnFinished = new HashMap<>();
	private final List<Runnable> turnActions = new ArrayList<>();
	private final WarsmashServerWriter writer;
	private int currentTurnTick = MAGIC_DELAY_OFFSET;
	private boolean gameStarted = false;
	private long lastServerHeartbeatTime = 0;

	public WarsmashServer() throws IOException {
		this.udpServer = new OrderedUdpServer(WarsmashConstants.PORT_NUMBER, new WarsmashServerParser(this));
		this.writer = new WarsmashServerWriter(this.udpServer, this.socketAddressToPlayerIndex.keySet());
	}

	public void startThread() {
		new Thread(this.udpServer).start();
	}

	public void startGame() {
		this.gameStarted = true;
		WarsmashServer.this.writer.startGame();
		WarsmashServer.this.writer.send();
		startTurn();
	}

	private void startTurn() {
		System.out.println("sending finishedTurn " + this.currentTurnTick);
		WarsmashServer.this.writer.finishedTurn(this.currentTurnTick);
		WarsmashServer.this.writer.send();
		this.currentTurnTick++;
	}

	private int getPlayerIndex(final SocketAddress sourceAddress) {
		Integer playerIndex = this.socketAddressToPlayerIndex.get(sourceAddress);
		if (playerIndex == null) {
			playerIndex = this.socketAddressToPlayerIndex.size();
			if (playerIndex == 2) {
				playerIndex = 5;
			}
			else if (playerIndex == 3) {
				playerIndex = 6;
			}
			this.socketAddressToPlayerIndex.put(sourceAddress, playerIndex);
		}
		return playerIndex;
	}

	@Override
	public void joinGame(final SocketAddress sourceAddress) {
		System.out.println("joinGame " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress);
		WarsmashServer.this.writer.acceptJoin(playerIndex);
		WarsmashServer.this.writer.send(sourceAddress);
	}

	@Override
	public void issueTargetOrder(final SocketAddress sourceAddress, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final boolean queue) {
		System.out.println("issueTargetOrder from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress);
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServer.this.writer.issueTargetOrder(playerIndex, unitHandleId, abilityHandleId, orderId,
						targetHandleId, queue);
				WarsmashServer.this.writer.send();
			}
		});
	}

	@Override
	public void issuePointOrder(final SocketAddress sourceAddress, final int unitHandleId, final int abilityHandleId,
			final int orderId, final float x, final float y, final boolean queue) {
		System.out.println("issuePointOrder from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress);
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServer.this.writer.issuePointOrder(playerIndex, unitHandleId, abilityHandleId, orderId, x, y,
						queue);
				WarsmashServer.this.writer.send();
			}
		});
	}

	@Override
	public void issueDropItemAtPointOrder(final SocketAddress sourceAddress, final int unitHandleId,
			final int abilityHandleId, final int orderId, final int targetHandleId, final float x, final float y,
			final boolean queue) {
		System.out.println("issueDropItemAtPointOrder from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress);
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServer.this.writer.issueDropItemAtPointOrder(playerIndex, unitHandleId, abilityHandleId,
						orderId, targetHandleId, x, y, queue);
				WarsmashServer.this.writer.send();
			}
		});
	}

	@Override
	public void issueDropItemAtTargetOrder(final SocketAddress sourceAddress, final int unitHandleId,
			final int abilityHandleId, final int orderId, final int targetHandleId, final int targetHeroHandleId,
			final boolean queue) {
		System.out.println("issueDropItemAtTargetOrder from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress);
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServer.this.writer.issueDropItemAtTargetOrder(playerIndex, unitHandleId, abilityHandleId,
						orderId, targetHandleId, targetHeroHandleId, queue);
				WarsmashServer.this.writer.send();
			}
		});
	}

	@Override
	public void issueImmediateOrder(final SocketAddress sourceAddress, final int unitHandleId,
			final int abilityHandleId, final int orderId, final boolean queue) {
		System.out.println("issueImmediateOrder from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress);
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServer.this.writer.issueImmediateOrder(playerIndex, unitHandleId, abilityHandleId, orderId,
						queue);
				WarsmashServer.this.writer.send();
			}
		});
	}

	@Override
	public void unitCancelTrainingItem(final SocketAddress sourceAddress, final int unitHandleId,
			final int cancelIndex) {
		System.out.println("unitCancelTrainingItem from " + sourceAddress);
		final int playerIndex = getPlayerIndex(sourceAddress);
		this.turnActions.add(new Runnable() {
			@Override
			public void run() {
				WarsmashServer.this.writer.unitCancelTrainingItem(playerIndex, unitHandleId, cancelIndex);
				WarsmashServer.this.writer.send();
			}
		});
	}

	@Override
	public void finishedTurn(final SocketAddress sourceAddress, final int clientGameTurnTick) {
		final int gameTurnTick = clientGameTurnTick + MAGIC_DELAY_OFFSET;
		if (WarsmashConstants.VERBOSE_LOGGING) {
			System.out.println("finishedTurn(" + gameTurnTick + ") from " + sourceAddress);
		}
		if (!this.gameStarted) {
			throw new IllegalStateException(
					"Client should not send us finishedTurn() message when game has not started!");
		}
		this.clientToTurnFinished.put(sourceAddress, clientGameTurnTick);
		boolean allDone = true;
		for (final SocketAddress clientAddress : this.socketAddressToPlayerIndex.keySet()) {
			final Integer turnFinishedValue = this.clientToTurnFinished.get(clientAddress);
			if ((turnFinishedValue == null) || (turnFinishedValue < clientGameTurnTick)) {
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
	public void framesSkipped(final int nFramesSkipped) {
		// dont care for now
		final long currentTimeMillis = System.currentTimeMillis();
		if ((currentTimeMillis - this.lastServerHeartbeatTime) > 3000) {
			// 3 seconds of frame skipping, make sure we keep in contact with client
			System.out.println("sending server heartbeat()");
			WarsmashServer.this.writer.heartbeat();
			WarsmashServer.this.writer.send();
			this.lastServerHeartbeatTime = currentTimeMillis;
		}
	}

	public static void main(final String[] args) {
		try {
			final WarsmashServer server = new WarsmashServer();
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

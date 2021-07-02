package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.etheller.warsmash.networking.udp.OrderedUdpServer;
import com.etheller.warsmash.util.WarsmashConstants;

public class WarsmashServer implements ClientToServerListener {
	private final OrderedUdpServer udpServer;
	private final Map<SocketAddress, Integer> socketAddressToPlayerIndex = new HashMap<>();
	private final Set<SocketAddress> clientsAwaitingTurnFinished = new HashSet<>();
	private final List<Runnable> turnActions = new ArrayList<>();
	private final WarsmashServerWriter writer;
	private int currentTurnTick = 0;
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
		this.clientsAwaitingTurnFinished.addAll(this.socketAddressToPlayerIndex.keySet());
		WarsmashServer.this.writer.finishedTurn(this.currentTurnTick);
		WarsmashServer.this.writer.send();
		this.currentTurnTick++;
	}

	private int getPlayerIndex(final SocketAddress sourceAddress) {
		Integer playerIndex = this.socketAddressToPlayerIndex.get(sourceAddress);
		if (playerIndex == null) {
			playerIndex = this.socketAddressToPlayerIndex.size();
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
	public void finishedTurn(final SocketAddress sourceAddress, final int gameTurnTick) {
//		System.out.println("finishedTurn(" + gameTurnTick + ") from " + sourceAddress);
		if (!this.gameStarted) {
			throw new IllegalStateException(
					"Client should not send us finishedTurn() message when game has not started!");
		}
		if (gameTurnTick == this.currentTurnTick) {
			this.clientsAwaitingTurnFinished.remove(sourceAddress);
			if (this.clientsAwaitingTurnFinished.isEmpty()) {
				for (final Runnable turnAction : this.turnActions) {
					turnAction.run();
				}
				this.turnActions.clear();
				startTurn();
			}
		}
		else {
			System.err.println("received bad finishedTurn() with remote gameTurnTick=" + gameTurnTick
					+ ", server local currenTurnTick=" + this.currentTurnTick);
		}
	}

	@Override
	public void framesSkipped(final int nFramesSkipped) {
		// dont care for now
		long currentTimeMillis = System.currentTimeMillis();
		if(currentTimeMillis - lastServerHeartbeatTime > 3000) {
			// 3 seconds of frame skipping, make sure we keep in contact with client

			WarsmashServer.this.writer.heartbeat();
			WarsmashServer.this.writer.send();
			lastServerHeartbeatTime = currentTimeMillis;
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

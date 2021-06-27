package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.etheller.warsmash.networking.udp.UdpClient;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderExecutor;

public class WarsmashClient implements ServerToClientListener, GameTurnManager {
	private final UdpClient udpClient;
	private final War3MapViewer game;
	private final Map<Integer, CPlayerUnitOrderExecutor> indexToExecutor = new HashMap<>();
	private int latestCompletedTurn = -1;
	private final WarsmashClientWriter writer;

	public WarsmashClient(final InetAddress serverAddress, final War3MapViewer game)
			throws UnknownHostException, IOException {
		this.udpClient = new UdpClient(serverAddress, WarsmashConstants.PORT_NUMBER, new WarsmashClientParser(this));
		this.game = game;
		this.writer = new WarsmashClientWriter(this.udpClient);
	}

	public CPlayerUnitOrderExecutor getExecutor(final int playerIndex) {
		CPlayerUnitOrderExecutor executor = this.indexToExecutor.get(playerIndex);
		if (executor == null) {
			executor = new CPlayerUnitOrderExecutor(this.game.simulation, playerIndex);
			this.indexToExecutor.put(playerIndex, executor);
		}
		return executor;
	}

	public void startThread() {
		new Thread(this.udpClient).start();
	}

	@Override
	public void acceptJoin(final int playerIndex) {
		this.game.setLocalPlayerIndex(playerIndex);
	}

	@Override
	public void issueTargetOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final boolean queue) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				executor.issueTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, queue);
			}
		});
	}

	@Override
	public void issuePointOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final float x, final float y, final boolean queue) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				executor.issuePointOrder(unitHandleId, abilityHandleId, orderId, x, y, queue);
			}
		});

	}

	@Override
	public void issueDropItemAtPointOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final float x, final float y, final boolean queue) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				executor.issueDropItemAtPointOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, x, y, queue);
			}
		});
	}

	@Override
	public void issueImmediateOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final boolean queue) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				executor.issueImmediateOrder(unitHandleId, abilityHandleId, orderId, queue);
			}
		});
	}

	@Override
	public void unitCancelTrainingItem(final int playerIndex, final int unitHandleId, final int cancelIndex) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				executor.unitCancelTrainingItem(unitHandleId, cancelIndex);
			}
		});
	}

	@Override
	public void finishedTurn(final int gameTurnTick) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				WarsmashClient.this.latestCompletedTurn = gameTurnTick;
			}
		});
	}

	@Override
	public void turnCompleted(final int gameTurnTick) {
		this.writer.finishedTurn(gameTurnTick);
		this.writer.send();
	}

	@Override
	public void startGame() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				WarsmashClient.this.game.setGameTurnManager(WarsmashClient.this);
			}
		});
	}

	@Override
	public int getLatestCompletedTurn() {
		return this.latestCompletedTurn;
	}

	public UdpClient getUdpClient() {
		return this.udpClient;
	}

	public WarsmashClientWriter getWriter() {
		return this.writer;
	}
}

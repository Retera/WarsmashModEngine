package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderExecutor;

import net.warsmash.networking.udp.OrderedUdpClient;

public class WarsmashClient implements ServerToClientListener, GameTurnManager {
	private final OrderedUdpClient udpClient;
	private final War3MapViewer game;
	private final Map<Integer, CPlayerUnitOrderExecutor> indexToExecutor = new HashMap<>();
	private int latestCompletedTurn = -1;
	private int latestLocallyRequestedTurn = -1;
	private final WarsmashClientWriter writer;
	private final Queue<QueuedMessage> queuedMessages = new ArrayDeque<>();

	public WarsmashClient(final InetAddress serverAddress, final War3MapViewer game)
			throws UnknownHostException, IOException {
		this.udpClient = new OrderedUdpClient(serverAddress, WarsmashConstants.PORT_NUMBER,
				new WarsmashClientParser(this));
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
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issueTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, queue);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issueTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, queue);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
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
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issuePointOrder(unitHandleId, abilityHandleId, orderId, x, y, queue);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issuePointOrder(unitHandleId, abilityHandleId, orderId, x, y, queue);
					;
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
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
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issueDropItemAtPointOrder(unitHandleId, abilityHandleId, orderId, targetHandleId,
									x, y, queue);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issueDropItemAtPointOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, x, y,
							queue);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
			}
		});
	}

	@Override
	public void issueDropItemAtTargetOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final int targetHeroHandleId, final boolean queue) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issueDropItemAtTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId,
									targetHeroHandleId, queue);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issueDropItemAtTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId,
							targetHeroHandleId, queue);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
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
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.issueImmediateOrder(unitHandleId, abilityHandleId, orderId, queue);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.issueImmediateOrder(unitHandleId, abilityHandleId, orderId, queue);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
			}
		});
	}

	@Override
	public void unitCancelTrainingItem(final int playerIndex, final int unitHandleId, final int cancelIndex) {
		final CPlayerUnitOrderExecutor executor = getExecutor(playerIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				final int currentServerTurnInProgress = WarsmashClient.this.latestCompletedTurn + 1;
				if (currentServerTurnInProgress > WarsmashClient.this.latestLocallyRequestedTurn) {
					WarsmashClient.this.queuedMessages.add(new QueuedMessage(currentServerTurnInProgress) {
						@Override
						public void run() {
							executor.unitCancelTrainingItem(unitHandleId, cancelIndex);
						}
					});
				}
				else if (currentServerTurnInProgress == WarsmashClient.this.latestLocallyRequestedTurn) {
					executor.unitCancelTrainingItem(unitHandleId, cancelIndex);
				}
				else {
					System.err.println("Turn tick system mismatch: " + currentServerTurnInProgress + " < "
							+ WarsmashClient.this.latestLocallyRequestedTurn);
				}
			}
		});
	}

	@Override
	public void finishedTurn(final int gameTurnTick) {
		if (WarsmashConstants.VERBOSE_LOGGING) {
			System.out.println("finishedTurn " + gameTurnTick);
		}
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
		this.latestLocallyRequestedTurn = gameTurnTick;
		while (!this.queuedMessages.isEmpty()
				&& (this.queuedMessages.peek().messageTurnTick == this.latestLocallyRequestedTurn)) {
			this.queuedMessages.poll().run();
		}
		if (!this.queuedMessages.isEmpty()) {
			System.out.println("stopped with " + this.queuedMessages.peek().messageTurnTick + " != "
					+ this.latestLocallyRequestedTurn);
		}
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
	public void framesSkipped(final float skippedCount) {
		this.writer.framesSkipped((int) skippedCount);
		this.writer.send();
	}

	@Override
	public void heartbeat() {
		// Not doing anything here at the moment. The act of the server sending us that
		// packet
		// will let the middle layer UDP system know to re-request any lost packets
		// based
		// on the heartbeat seq no. But at app layer, here, we can ignore it.
		System.out.println("got heartbeat() from server");
	}

	@Override
	public int getLatestCompletedTurn() {
		return this.latestCompletedTurn;
	}

	public WarsmashClientWriter getWriter() {
		return this.writer;
	}

	private static abstract class QueuedMessage implements Runnable {
		private final int messageTurnTick;

		public QueuedMessage(final int messageTurnTick) {
			this.messageTurnTick = messageTurnTick;
		}

		public final int getMessageTurnTick() {
			return this.messageTurnTick;
		}

		@Override
		public abstract void run();
	}
}

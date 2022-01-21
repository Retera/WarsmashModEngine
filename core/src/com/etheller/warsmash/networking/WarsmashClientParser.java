package com.etheller.warsmash.networking;

import java.nio.ByteBuffer;

import net.warsmash.networking.udp.OrderedUdpClientListener;

public class WarsmashClientParser implements OrderedUdpClientListener {
	private final ServerToClientListener listener;

	public WarsmashClientParser(final ServerToClientListener listener) {
		this.listener = listener;
	}

	@Override
	public void cantReplay(final int seqNo) {
		throw new IllegalStateException("Cant replay seqNo=" + seqNo + " !");
	}

	@Override
	public void parse(final ByteBuffer buffer) {
		final int initialLimit = buffer.limit();
		try {
			while (buffer.hasRemaining()) {
				final int length = buffer.getInt();
				if (length > buffer.remaining()) {
					// this packet is junk to us, so we will skip and continue (drop system will
					// handle it)
					System.err.println("Got mismatched protocol length " + length + " > " + buffer.remaining() + "!!");
				}
				final int protocol = buffer.getInt();
				switch (protocol) {
				case ServerToClientProtocol.ISSUE_TARGET_ORDER: {
					final int playerIndex = buffer.getInt();
					final int unitHandleId = buffer.getInt();
					final int abilityHandleId = buffer.getInt();
					final int orderId = buffer.getInt();
					final int targetHandleId = buffer.getInt();
					final boolean queue = buffer.get() == 1;
					this.listener.issueTargetOrder(playerIndex, unitHandleId, abilityHandleId, orderId, targetHandleId,
							queue);
					break;
				}
				case ServerToClientProtocol.ISSUE_POINT_ORDER: {
					final int playerIndex = buffer.getInt();
					final int unitHandleId = buffer.getInt();
					final int abilityHandleId = buffer.getInt();
					final int orderId = buffer.getInt();
					final float x = buffer.getFloat();
					final float y = buffer.getFloat();
					final boolean queue = buffer.get() == 1;
					this.listener.issuePointOrder(playerIndex, unitHandleId, abilityHandleId, orderId, x, y, queue);
					break;
				}
				case ServerToClientProtocol.ISSUE_DROP_ITEM_ORDER: {
					final int playerIndex = buffer.getInt();
					final int unitHandleId = buffer.getInt();
					final int abilityHandleId = buffer.getInt();
					final int orderId = buffer.getInt();
					final int targetHandleId = buffer.getInt();
					final float x = buffer.getFloat();
					final float y = buffer.getFloat();
					final boolean queue = buffer.get() == 1;
					this.listener.issueDropItemAtPointOrder(playerIndex, unitHandleId, abilityHandleId, orderId,
							targetHandleId, x, y, queue);
					break;
				}
				case ServerToClientProtocol.ISSUE_DROP_ITEM_ON_TARGET_ORDER: {
					final int playerIndex = buffer.getInt();
					final int unitHandleId = buffer.getInt();
					final int abilityHandleId = buffer.getInt();
					final int orderId = buffer.getInt();
					final int targetHandleId = buffer.getInt();
					final int targetHeroHandleId = buffer.getInt();
					final boolean queue = buffer.get() == 1;
					this.listener.issueDropItemAtTargetOrder(playerIndex, unitHandleId, abilityHandleId, orderId,
							targetHandleId, targetHeroHandleId, queue);
					break;
				}
				case ServerToClientProtocol.ISSUE_IMMEDIATE_ORDER: {
					final int playerIndex = buffer.getInt();
					final int unitHandleId = buffer.getInt();
					final int abilityHandleId = buffer.getInt();
					final int orderId = buffer.getInt();
					final boolean queue = buffer.get() == 1;
					this.listener.issueImmediateOrder(playerIndex, unitHandleId, abilityHandleId, orderId, queue);
					break;
				}
				case ServerToClientProtocol.UNIT_CANCEL_TRAINING: {
					final int playerIndex = buffer.getInt();
					final int unitHandleId = buffer.getInt();
					final int cancelIndex = buffer.getInt();
					this.listener.unitCancelTrainingItem(playerIndex, unitHandleId, cancelIndex);
					break;
				}
				case ServerToClientProtocol.FINISHED_TURN: {
					final int gameTurnTick = buffer.getInt();
					this.listener.finishedTurn(gameTurnTick);
					break;
				}
				case ServerToClientProtocol.ACCEPT_JOIN: {
					final int playerIndex = buffer.getInt();
					this.listener.acceptJoin(playerIndex);
					break;
				}
				case ServerToClientProtocol.START_GAME: {
					this.listener.startGame();
					break;
				}
				case ServerToClientProtocol.HEARTBEAT: {
					this.listener.heartbeat();
					break;
				}

				default:
					System.err.println("Got unknown protocol: " + protocol);
					break;
				}
			}
		}
		finally {
			buffer.position(initialLimit);
		}
	}
}

package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import net.warsmash.networking.udp.OrderedUdpServerListener;

public class WarsmashServerParser implements OrderedUdpServerListener {

	private final ClientToServerListener listener;

	public WarsmashServerParser(final ClientToServerListener clientToServerListener) throws IOException {
		this.listener = clientToServerListener;
	}

	@Override
	public void parse(final SocketAddress sourceAddress, final ByteBuffer buffer) {
		final int initialLimit = buffer.limit();
		try {
			while (buffer.hasRemaining()) {
				final int length = buffer.getInt();
				if (length > buffer.remaining()) {
					// this packet is junk to us, so we will skip and continue (drop system will
					// handle it)
					System.err.println("Got mismatched protocol length " + length + " > " + buffer.remaining() + "!!");
					break;
				}
				final int protocol = buffer.getInt();
				switch (protocol) {
				case ClientToServerProtocol.ISSUE_TARGET_ORDER: {
					final int unitHandleId = buffer.getInt();
					final int abilityHandleId = buffer.getInt();
					final int orderId = buffer.getInt();
					final int targetHandleId = buffer.getInt();
					final boolean queue = buffer.get() == 1;
					this.listener.issueTargetOrder(sourceAddress, unitHandleId, abilityHandleId, orderId,
							targetHandleId, queue);
					break;
				}
				case ClientToServerProtocol.ISSUE_POINT_ORDER: {
					final int unitHandleId = buffer.getInt();
					final int abilityHandleId = buffer.getInt();
					final int orderId = buffer.getInt();
					final float x = buffer.getFloat();
					final float y = buffer.getFloat();
					final boolean queue = buffer.get() == 1;
					this.listener.issuePointOrder(sourceAddress, unitHandleId, abilityHandleId, orderId, x, y, queue);
					break;
				}
				case ClientToServerProtocol.ISSUE_DROP_ITEM_ORDER: {
					final int unitHandleId = buffer.getInt();
					final int abilityHandleId = buffer.getInt();
					final int orderId = buffer.getInt();
					final int targetHandleId = buffer.getInt();
					final float x = buffer.getFloat();
					final float y = buffer.getFloat();
					final boolean queue = buffer.get() == 1;
					this.listener.issueDropItemAtPointOrder(sourceAddress, unitHandleId, abilityHandleId, orderId,
							targetHandleId, x, y, queue);
					break;
				}
				case ClientToServerProtocol.ISSUE_DROP_ITEM_ON_TARGET_ORDER: {
					final int unitHandleId = buffer.getInt();
					final int abilityHandleId = buffer.getInt();
					final int orderId = buffer.getInt();
					final int targetHandleId = buffer.getInt();
					final int targetHeroHandleId = buffer.getInt();
					final boolean queue = buffer.get() == 1;
					this.listener.issueDropItemAtTargetOrder(sourceAddress, unitHandleId, abilityHandleId, orderId,
							targetHandleId, targetHeroHandleId, queue);
					break;
				}
				case ClientToServerProtocol.ISSUE_IMMEDIATE_ORDER: {
					final int unitHandleId = buffer.getInt();
					final int abilityHandleId = buffer.getInt();
					final int orderId = buffer.getInt();
					final boolean queue = buffer.get() == 1;
					this.listener.issueImmediateOrder(sourceAddress, unitHandleId, abilityHandleId, orderId, queue);
					break;
				}
				case ClientToServerProtocol.UNIT_CANCEL_TRAINING: {
					final int unitHandleId = buffer.getInt();
					final int cancelIndex = buffer.getInt();
					this.listener.unitCancelTrainingItem(sourceAddress, unitHandleId, cancelIndex);
					break;
				}
				case ClientToServerProtocol.FINISHED_TURN: {
					final int gameTurnTick = buffer.getInt();
					this.listener.finishedTurn(sourceAddress, gameTurnTick);
					break;
				}
				case ClientToServerProtocol.JOIN_GAME: {
					this.listener.joinGame(sourceAddress);
					break;
				}
				case ClientToServerProtocol.FRAMES_SKIPPED: {
					final int nFramesSkipped = buffer.getInt();
					this.listener.framesSkipped(nFramesSkipped);
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

	@Override
	public void cantReplay(final SocketAddress sourceAddress, final int seqNo) {
		throw new IllegalStateException("Cant replay " + seqNo + " to " + sourceAddress + " !");
	}
}

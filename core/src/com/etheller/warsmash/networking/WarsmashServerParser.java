package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import com.etheller.warsmash.networking.udp.UdpServerListener;

public class WarsmashServerParser implements UdpServerListener {

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
					throw new IllegalStateException(
							"Got mismatched protocol length " + length + " > " + buffer.remaining() + "!!");
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

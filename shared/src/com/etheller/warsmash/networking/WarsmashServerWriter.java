package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;

import net.warsmash.networking.udp.OrderedUdpServer;

public class WarsmashServerWriter implements ServerToClientListener {
	private final OrderedUdpServer server;
	private final ByteBuffer sendBuffer = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN);
	private final Set<SocketAddress> allKnownAddressesToSend;

	public WarsmashServerWriter(final OrderedUdpServer server, final Set<SocketAddress> allKnownAddressesToSend) {
		this.server = server;
		this.allKnownAddressesToSend = allKnownAddressesToSend;
	}

	@Override
	public void issueTargetOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ServerToClientProtocol.ISSUE_TARGET_ORDER);
		this.sendBuffer.putInt(playerIndex);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.putInt(targetHandleId);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	@Override
	public void issuePointOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final float x, final float y, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ServerToClientProtocol.ISSUE_POINT_ORDER);
		this.sendBuffer.putInt(playerIndex);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.putFloat(x);
		this.sendBuffer.putFloat(y);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	@Override
	public void issueDropItemAtPointOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final float x, final float y, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ServerToClientProtocol.ISSUE_DROP_ITEM_ORDER);
		this.sendBuffer.putInt(playerIndex);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.putInt(targetHandleId);
		this.sendBuffer.putFloat(x);
		this.sendBuffer.putFloat(y);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	@Override
	public void issueDropItemAtTargetOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final int targetHeroHandleId, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ServerToClientProtocol.ISSUE_DROP_ITEM_ON_TARGET_ORDER);
		this.sendBuffer.putInt(playerIndex);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.putInt(targetHandleId);
		this.sendBuffer.putInt(targetHeroHandleId);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	@Override
	public void issueImmediateOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ServerToClientProtocol.ISSUE_IMMEDIATE_ORDER);
		this.sendBuffer.putInt(playerIndex);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	@Override
	public void unitCancelTrainingItem(final int playerIndex, final int unitHandleId, final int cancelIndex) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ServerToClientProtocol.UNIT_CANCEL_TRAINING);
		this.sendBuffer.putInt(playerIndex);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(cancelIndex);
	}

	@Override
	public void finishedTurn(final int gameTurnTick) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4);
		this.sendBuffer.putInt(ServerToClientProtocol.FINISHED_TURN);
		this.sendBuffer.putInt(gameTurnTick);
	}

	@Override
	public void heartbeat() {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4);
		this.sendBuffer.putInt(ServerToClientProtocol.HEARTBEAT);
	}

	@Override
	public void acceptJoin(final int playerIndex) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4);
		this.sendBuffer.putInt(ServerToClientProtocol.ACCEPT_JOIN);
		this.sendBuffer.putInt(playerIndex);
	}

	@Override
	public void startGame() {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4);
		this.sendBuffer.putInt(ServerToClientProtocol.START_GAME);
	}

	public void send(final SocketAddress sourceAddress) {
		this.sendBuffer.flip();
		try {
			this.server.send(sourceAddress, this.sendBuffer);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void send() {
		this.sendBuffer.flip();
		try {
			for (final SocketAddress address : this.allKnownAddressesToSend) {
				final int pos = this.sendBuffer.position();
				final int limit = this.sendBuffer.limit();
				this.server.send(address, this.sendBuffer);
				this.sendBuffer.position(pos);
				this.sendBuffer.limit(limit);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}

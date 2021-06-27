package com.etheller.warsmash.networking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.etheller.warsmash.networking.udp.UdpClient;

public class WarsmashClientWriter {
	private final UdpClient client;
	private final ByteBuffer sendBuffer = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN);

	public WarsmashClientWriter(final UdpClient client) {
		this.client = client;
	}

	public void issueTargetOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetHandleId, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ClientToServerProtocol.ISSUE_TARGET_ORDER);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.putInt(targetHandleId);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	public void issuePointOrder(final int unitHandleId, final int abilityHandleId, final int orderId, final float x,
			final float y, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ClientToServerProtocol.ISSUE_POINT_ORDER);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.putFloat(x);
		this.sendBuffer.putFloat(y);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	public void issueDropItemAtPointOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetHandleId, final float x, final float y, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ClientToServerProtocol.ISSUE_DROP_ITEM_ORDER);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.putInt(targetHandleId);
		this.sendBuffer.putFloat(x);
		this.sendBuffer.putFloat(y);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	public void issueImmediateOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ClientToServerProtocol.ISSUE_IMMEDIATE_ORDER);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	public void unitCancelTrainingItem(final int unitHandleId, final int cancelIndex) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ClientToServerProtocol.UNIT_CANCEL_TRAINING);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(cancelIndex);
	}

	public void finishedTurn(final int gameTurnTick) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 4);
		this.sendBuffer.putInt(ClientToServerProtocol.FINISHED_TURN);
		this.sendBuffer.putInt(gameTurnTick);
	}

	public void joinGame() {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4);
		this.sendBuffer.putInt(ClientToServerProtocol.JOIN_GAME);
	}

	public void send() {
		this.sendBuffer.flip();
		System.out.println("CLIENT WRITER calling send() on " + this.sendBuffer.remaining() + " bytes");
		try {
			this.client.send(this.sendBuffer);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}

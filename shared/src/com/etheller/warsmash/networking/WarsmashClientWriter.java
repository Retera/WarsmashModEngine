package com.etheller.warsmash.networking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.warsmash.networking.udp.OrderedUdpClient;

public class WarsmashClientWriter {
	private final OrderedUdpClient client;
	private final ByteBuffer sendBuffer = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN);
	private final long sessionToken;

	public WarsmashClientWriter(final OrderedUdpClient client, final long sessionToken) {
		this.client = client;
		this.sessionToken = sessionToken;
	}

	public void issueTargetOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetHandleId, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 8 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ClientToServerProtocol.ISSUE_TARGET_ORDER);
		this.sendBuffer.putLong(this.sessionToken);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.putInt(targetHandleId);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	public void issuePointOrder(final int unitHandleId, final int abilityHandleId, final int orderId, final float x,
			final float y, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 8 + 4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ClientToServerProtocol.ISSUE_POINT_ORDER);
		this.sendBuffer.putLong(this.sessionToken);
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
		this.sendBuffer.putInt(4 + 8 + 4 + 4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ClientToServerProtocol.ISSUE_DROP_ITEM_ORDER);
		this.sendBuffer.putLong(this.sessionToken);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.putInt(targetHandleId);
		this.sendBuffer.putFloat(x);
		this.sendBuffer.putFloat(y);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	public void issueDropItemAtTargetOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetHandleId, final int targetHeroHandleId, final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 8 + 4 + 4 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ClientToServerProtocol.ISSUE_DROP_ITEM_ON_TARGET_ORDER);
		this.sendBuffer.putLong(this.sessionToken);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.putInt(targetHandleId);
		this.sendBuffer.putInt(targetHeroHandleId);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	public void issueImmediateOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final boolean queue) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 8 + 4 + 4 + 4 + 1);
		this.sendBuffer.putInt(ClientToServerProtocol.ISSUE_IMMEDIATE_ORDER);
		this.sendBuffer.putLong(this.sessionToken);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(abilityHandleId);
		this.sendBuffer.putInt(orderId);
		this.sendBuffer.put(queue ? (byte) 1 : (byte) 0);
	}

	public void unitCancelTrainingItem(final int unitHandleId, final int cancelIndex) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 8 + 4 + 4);
		this.sendBuffer.putInt(ClientToServerProtocol.UNIT_CANCEL_TRAINING);
		this.sendBuffer.putLong(this.sessionToken);
		this.sendBuffer.putInt(unitHandleId);
		this.sendBuffer.putInt(cancelIndex);
	}

	public void issueGuiPlayerEvent(final int eventId) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 8 + 4);
		this.sendBuffer.putInt(ClientToServerProtocol.ISSUE_GUI_PLAYER_EVENT);
		this.sendBuffer.putLong(this.sessionToken);
		this.sendBuffer.putInt(eventId);
	}

	public void finishedTurn(final int gameTurnTick) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 8 + 4);
		this.sendBuffer.putInt(ClientToServerProtocol.FINISHED_TURN);
		this.sendBuffer.putLong(this.sessionToken);
		this.sendBuffer.putInt(gameTurnTick);
	}

	public void framesSkipped(final int skippedCount) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 8 + 4);
		this.sendBuffer.putInt(ClientToServerProtocol.FRAMES_SKIPPED);
		this.sendBuffer.putLong(this.sessionToken);
		this.sendBuffer.putInt(skippedCount);
	}

	public void joinGame() {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(4 + 8);
		this.sendBuffer.putInt(ClientToServerProtocol.JOIN_GAME);
		this.sendBuffer.putLong(this.sessionToken);
	}

	public void send() {
		this.sendBuffer.flip();
		try {
			this.client.send(this.sendBuffer);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}

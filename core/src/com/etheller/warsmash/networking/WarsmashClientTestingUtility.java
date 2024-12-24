package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.InetAddress;

import net.warsmash.networking.udp.OrderedUdpClient;
import net.warsmash.uberserver.GamingNetwork;

public class WarsmashClientTestingUtility implements ServerToClientListener {
	private final OrderedUdpClient udpClient;
	private final WarsmashClientWriter writer;
	private int myTurn = -1;

	public WarsmashClientTestingUtility(final InetAddress serverHost, final int udpSingleGamePort,
			final long sessionToken) throws IOException {
		udpClient = new OrderedUdpClient(serverHost, udpSingleGamePort, new WarsmashClientParser(this));
		writer = new WarsmashClientWriter(udpClient, sessionToken);
	}

	public void startThread() {
		new Thread(this.udpClient).start();
	}

	public WarsmashClientWriter getWriter() {
		return writer;
	}

	@Override
	public void acceptJoin(final int playerIndex) {
		System.out.println("WarsmashClientTestingUtility.acceptJoin");
		System.out.println("playerIndex = " + playerIndex);
	}

	@Override
	public void issueTargetOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final boolean queue) {
		System.out.println("WarsmashClientTestingUtility.issueTargetOrder");
		System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", abilityHandleId = "
				+ abilityHandleId + ", orderId = " + orderId + ", targetHandleId = " + targetHandleId + ", queue = "
				+ queue);
	}

	@Override
	public void issuePointOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final float x, final float y, final boolean queue) {
		System.out.println("WarsmashClientTestingUtility.issuePointOrder");
		System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", abilityHandleId = "
				+ abilityHandleId + ", orderId = " + orderId + ", x = " + x + ", y = " + y + ", queue = " + queue);
	}

	@Override
	public void issueDropItemAtPointOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final float x, final float y, final boolean queue) {
		System.out.println("WarsmashClientTestingUtility.issueDropItemAtPointOrder");
		System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", abilityHandleId = "
				+ abilityHandleId + ", orderId = " + orderId + ", targetHandleId = " + targetHandleId + ", x = " + x
				+ ", y = " + y + ", queue = " + queue);
	}

	@Override
	public void issueDropItemAtTargetOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final int targetHandleId, final int targetHeroHandleId, final boolean queue) {
		System.out.println("WarsmashClientTestingUtility.issueDropItemAtTargetOrder");
		System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", abilityHandleId = "
				+ abilityHandleId + ", orderId = " + orderId + ", targetHandleId = " + targetHandleId
				+ ", targetHeroHandleId = " + targetHeroHandleId + ", queue = " + queue);
	}

	@Override
	public void issueImmediateOrder(final int playerIndex, final int unitHandleId, final int abilityHandleId,
			final int orderId, final boolean queue) {
		System.out.println("WarsmashClientTestingUtility.issueImmediateOrder");
		System.out.println("playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", abilityHandleId = "
				+ abilityHandleId + ", orderId = " + orderId + ", queue = " + queue);
	}

	@Override
	public void unitCancelTrainingItem(final int playerIndex, final int unitHandleId, final int cancelIndex) {
		System.out.println("WarsmashClientTestingUtility.unitCancelTrainingItem");
		System.out.println(
				"playerIndex = " + playerIndex + ", unitHandleId = " + unitHandleId + ", cancelIndex = " + cancelIndex);
	}

	@Override
	public void issueGuiPlayerEvent(final int playerIndex, final int eventId) {
		System.out.println("WarsmashClientTestingUtility.issueGuiPlayerEvent");
		System.out.println("playerIndex = " + playerIndex + ", eventId = " + eventId);
	}

	@Override
	public void startGame() {
		System.out.println("WarsmashClientTestingUtility.startGame");
		System.out.println();
	}

	@Override
	public void finishedTurn(final int gameTurnTick) {
		System.out.println("WarsmashClientTestingUtility.finishedTurn");
		System.out.println("gameTurnTick = " + gameTurnTick);

		while (myTurn <= gameTurnTick) {
			myTurn++;
			if ((myTurn % 4) == 3) {
				writer.issuePointOrder(999, 1234, 8192, 0, 0, false);
			}
			writer.finishedTurn(myTurn);
			writer.send();
		}
	}

	@Override
	public void heartbeat() {
		System.out.println("WarsmashClientTestingUtility.heartbeat");
		System.out.println();
	}

	public static void main(final String[] args) {
		final long sessionToken = 1337002L;
		try {
			final InetAddress localHost = InetAddress.getByName("creative.etheller.com");
			final int udpSingleGamePort = GamingNetwork.UDP_SINGLE_GAME_PORT;
			final WarsmashClientTestingUtility warsmashClientTestingUtility = new WarsmashClientTestingUtility(
					localHost, udpSingleGamePort, sessionToken);
			warsmashClientTestingUtility.startThread();
			final WarsmashClientWriter writer = warsmashClientTestingUtility.getWriter();
			writer.joinGame();
			writer.send();
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}

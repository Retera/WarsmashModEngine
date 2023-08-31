package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import net.warsmash.networking.udp.OrderedUdpServer;
import net.warsmash.uberserver.GamingNetwork;

public class WarsmashServer implements ClientToServerListener {
	private final WarsmashServerLogic serverLogic;
	private final OrderedUdpServer udpServer;

	public WarsmashServer(final int port, final Map<Long, Integer> sessionTokenToPermittedSlot) throws IOException {
		final Set<SocketAddress> socketAddressesKnown = new HashSet<>();
		this.udpServer = new OrderedUdpServer(port, new WarsmashServerParser(this));
		this.serverLogic = new WarsmashServerLogic(new WarsmashServerWriter(this.udpServer, socketAddressesKnown), sessionTokenToPermittedSlot, socketAddressesKnown);
	}
	
	public void startGame() {
		this.serverLogic.startGame();
	}

	// Useful for if they pass 0 as port and get an auto-assigned one
	public int getPort() {
		return this.udpServer.getPort();
	}

	public InetSocketAddress getLocalAddress() {
		return this.udpServer.getLocalAddress();
	}

	public void startThread() {
		new Thread(this.udpServer).start();
	}

	@Override
	public void joinGame(final SocketAddress sourceAddress, final long sessionToken) {
		this.serverLogic.joinGame(sourceAddress, sessionToken);
	}

	@Override
	public void issueTargetOrder(final SocketAddress sourceAddress, final long sessionToken, final int unitHandleId,
			final int abilityHandleId, final int orderId, final int targetHandleId, final boolean queue) {
		this.serverLogic.issueTargetOrder(sourceAddress, sessionToken, unitHandleId, abilityHandleId, orderId, targetHandleId, queue);
	}

	@Override
	public void issuePointOrder(final SocketAddress sourceAddress, final long sessionToken, final int unitHandleId,
			final int abilityHandleId, final int orderId, final float x, final float y, final boolean queue) {
		this.serverLogic.issuePointOrder(sourceAddress, sessionToken, unitHandleId, abilityHandleId, orderId, x, y, queue);
	}

	@Override
	public void issueDropItemAtPointOrder(final SocketAddress sourceAddress, final long sessionToken,
			final int unitHandleId, final int abilityHandleId, final int orderId, final int targetHandleId,
			final float x, final float y, final boolean queue) {
		this.serverLogic.issueDropItemAtPointOrder(sourceAddress, sessionToken, unitHandleId, abilityHandleId, orderId, targetHandleId, x, y, queue);
	}

	@Override
	public void issueDropItemAtTargetOrder(final SocketAddress sourceAddress, final long sessionToken,
			final int unitHandleId, final int abilityHandleId, final int orderId, final int targetHandleId,
			final int targetHeroHandleId, final boolean queue) {
		this.serverLogic.issueDropItemAtTargetOrder(sourceAddress, sessionToken, unitHandleId, abilityHandleId, orderId, targetHandleId, targetHeroHandleId, queue);
	}

	@Override
	public void issueImmediateOrder(final SocketAddress sourceAddress, final long sessionToken, final int unitHandleId,
			final int abilityHandleId, final int orderId, final boolean queue) {
		this.serverLogic.issueImmediateOrder(sourceAddress, sessionToken, unitHandleId, abilityHandleId, orderId, queue);
	}

	@Override
	public void unitCancelTrainingItem(final SocketAddress sourceAddress, final long sessionToken,
			final int unitHandleId, final int cancelIndex) {
		this.serverLogic.unitCancelTrainingItem(sourceAddress, sessionToken, unitHandleId, cancelIndex);
	}

	@Override
	public void finishedTurn(final SocketAddress sourceAddress, final long sessionToken, final int clientGameTurnTick) {
		this.serverLogic.finishedTurn(sourceAddress, sessionToken, clientGameTurnTick);
	}

	@Override
	public void framesSkipped(final long sessionToken, final int nFramesSkipped) {
		this.serverLogic.framesSkipped(sessionToken, nFramesSkipped);
	}
}

package com.etheller.warsmash.networking.uberserver;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;

import net.warsmash.nio.channels.SelectableChannelOpener;
import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.nio.util.ExceptionListener;
import net.warsmash.uberserver.GamingNetwork;
import net.warsmash.uberserver.GamingNetworkClientToServerWriter;
import net.warsmash.uberserver.GamingNetworkConnection;
import net.warsmash.uberserver.GamingNetworkServerToClientListener;
import net.warsmash.uberserver.GamingNetworkServerToClientListener.GamingNetworkServerToClientNotifier;
import net.warsmash.uberserver.HostedGameVisibility;
import net.warsmash.uberserver.LobbyGameSpeed;
import net.warsmash.uberserver.TCPGamingNetworkServerToClientParser;

public class GamingNetworkConnectionImpl implements GamingNetworkConnection {
	private WritableOutput tcpChannel = null;
	private final SelectableChannelOpener selectableChannelOpener;
	private Thread networkThread;
	private final GamingNetworkServerToClientNotifier notifier;
	private GamingNetworkClientToServerWriter gamingNetworkClientToServerWriter;
	private final String gateway;

	public GamingNetworkConnectionImpl(final String gateway) {
		this.gateway = gateway;
		this.selectableChannelOpener = new SelectableChannelOpener();
		this.notifier = new GamingNetworkServerToClientNotifier();
	}

	public void start() {
		this.tcpChannel = this.selectableChannelOpener.openTCPClientChannel(
				new InetSocketAddress(this.gateway, GamingNetwork.PORT),
				new TCPGamingNetworkServerToClientParser(this.notifier), ExceptionListener.THROW_RUNTIME,
				8 * 1024 * 1024, ByteOrder.LITTLE_ENDIAN);
		this.gamingNetworkClientToServerWriter = new GamingNetworkClientToServerWriter(this.tcpChannel);
		this.networkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					GamingNetworkConnectionImpl.this.selectableChannelOpener.select(0);
				}
			}
		});
		this.networkThread.start();
	}

	public void stop() {
		if (this.tcpChannel != null) {
			this.tcpChannel.close();
			this.tcpChannel = null;
			this.gamingNetworkClientToServerWriter = null;
		}
		if (this.networkThread != null) {
			this.networkThread.interrupt();
			this.networkThread = null;
		}
	}

	@Override
	public void handshake(final String gameId, final int version) {
		this.gamingNetworkClientToServerWriter.handshake(gameId, version);
	}

	@Override
	public void createAccount(final String username, final char[] passwordHash) {
		this.gamingNetworkClientToServerWriter.createAccount(username, passwordHash);
	}

	@Override
	public void login(final String username, final char[] passwordHash) {
		this.gamingNetworkClientToServerWriter.login(username, passwordHash);
	}

	@Override
	public void joinChannel(final long sessionToken, final String channelName) {
		this.gamingNetworkClientToServerWriter.joinChannel(sessionToken, channelName);
	}

	@Override
	public void chatMessage(final long sessionToken, final String text) {
		this.gamingNetworkClientToServerWriter.chatMessage(sessionToken, text);
	}

	@Override
	public void emoteMessage(final long sessionToken, final String text) {
		this.gamingNetworkClientToServerWriter.emoteMessage(sessionToken, text);
	}

	@Override
	public void queryGamesList(final long sessionToken) {
		this.gamingNetworkClientToServerWriter.queryGamesList(sessionToken);
	}

	@Override
	public void queryGameInfo(final long sessionToken, final String gameName) {
		this.gamingNetworkClientToServerWriter.queryGameInfo(sessionToken, gameName);
	}

	@Override
	public void joinGame(final long sessionToken, final String gameName) {
		this.gamingNetworkClientToServerWriter.joinGame(sessionToken, gameName);
	}

	@Override
	public void createGame(final long sessionToken, final String gameName, final String mapName, final int totalSlots,
			final LobbyGameSpeed gameSpeed, final long gameCreationTimeMillis, final HostedGameVisibility visibility) {
		this.gamingNetworkClientToServerWriter.createGame(sessionToken, gameName, mapName, totalSlots, gameSpeed,
				gameCreationTimeMillis, visibility);
	}

	@Override
	public void disconnected() {
		stop();
	}

	@Override
	public void addListener(final GamingNetworkServerToClientListener listener) {
		this.notifier.addSubscriber(listener);
	}

	@Override
	public void userRequestDisconnect() {
		stop();
	}

	@Override
	public boolean userRequestConnect() {
		stop();
		try {
			start();
			return true;
		}
		catch (final Exception exc) {
			exc.printStackTrace();
		}
		return false;
	}

	@Override
	public String getGatewayString() {
		return this.gateway;
	}

}

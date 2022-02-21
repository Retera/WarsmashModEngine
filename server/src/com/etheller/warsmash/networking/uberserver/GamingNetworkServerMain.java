package com.etheller.warsmash.networking.uberserver;

import java.util.HashSet;
import java.util.Set;

import com.etheller.warsmash.networking.uberserver.users.InRAMUserManager;

import net.warsmash.nio.channels.SelectableChannelOpener;
import net.warsmash.uberserver.GamingNetwork;

public class GamingNetworkServerMain {
	public static void main(final String[] args) {
		final SelectableChannelOpener channelOpener = new SelectableChannelOpener();
		final Set<AcceptedGameListKey> acceptedGames = new HashSet<>();
		acceptedGames.add(new AcceptedGameListKey(GamingNetwork.GAME_ID_BASE, GamingNetwork.GAME_VERSION_DATA));
		acceptedGames.add(new AcceptedGameListKey(GamingNetwork.GAME_ID_XPAC, GamingNetwork.GAME_VERSION_DATA));
		final InRAMUserManager inRAMUserManager = new InRAMUserManager();
		final String welcomeMessage = "Thank you for connecting to the first draft of the Warsmash game server.";
		final TCPGamingNetworkServer tcpGamingNetworkServer = new TCPGamingNetworkServer(channelOpener,
				new DefaultGamingNetworkServerClientBuilder(
						new GamingNetworkServerBusinessLogicImpl(acceptedGames, inRAMUserManager, welcomeMessage)));
		tcpGamingNetworkServer.start();

		while (true) {
			channelOpener.select(100);
		}
	}
}

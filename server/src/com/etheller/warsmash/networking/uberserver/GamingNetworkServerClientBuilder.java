package com.etheller.warsmash.networking.uberserver;

import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.nio.channels.WritableSocketOutput;
import net.warsmash.uberserver.GamingNetworkClientToServerListener;

public interface GamingNetworkServerClientBuilder {
	GamingNetworkClientToServerListener createClient(WritableSocketOutput output);
}

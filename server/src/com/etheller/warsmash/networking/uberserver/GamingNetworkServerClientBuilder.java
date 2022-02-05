package com.etheller.warsmash.networking.uberserver;

import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.uberserver.GamingNetworkClientToServerListener;

public interface GamingNetworkServerClientBuilder {
	GamingNetworkClientToServerListener createClient(WritableOutput output);
}

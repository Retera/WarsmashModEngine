package com.etheller.warsmash.networking.uberserver;

import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.uberserver.GamingNetworkServerToClientListener;

public interface GamingNetworkServerClientBuilder {
	GamingNetworkServerToClientListener createClient(WritableOutput output);
}

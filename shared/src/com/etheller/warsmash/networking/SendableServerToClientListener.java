package com.etheller.warsmash.networking;

import java.net.SocketAddress;

public interface SendableServerToClientListener extends ServerToClientListener {
	void send();
	
	void send(final SocketAddress address);
}

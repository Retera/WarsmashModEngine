package net.warsmash.networking.udp;

import java.net.SocketAddress;

public interface OrderedUdpServerListener extends UdpServerListener {
	void cantReplay(SocketAddress sourceAddress, int seqNo);
}

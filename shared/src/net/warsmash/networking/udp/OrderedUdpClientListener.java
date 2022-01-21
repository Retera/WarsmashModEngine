package net.warsmash.networking.udp;

public interface OrderedUdpClientListener extends UdpClientListener {
	void cantReplay(int seqNo);
}

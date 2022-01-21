package net.warsmash.networking.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class OrderedUdpClient extends OrderedUdpCommuncation implements Runnable {
	private final UdpClient udpClient;

	public OrderedUdpClient(final InetAddress serverAddress, final int portNumber,
			final OrderedUdpClientListener listener) throws UnknownHostException, IOException {
		super(listener);
		this.udpClient = new UdpClient(serverAddress, portNumber, this);
	}

	@Override
	protected void trySend(final ByteBuffer data) {
		try {
			this.udpClient.send(data);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run() {
		this.udpClient.run();
	}
}

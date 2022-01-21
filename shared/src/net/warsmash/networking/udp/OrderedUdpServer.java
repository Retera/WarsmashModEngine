package net.warsmash.networking.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class OrderedUdpServer implements UdpServerListener, Runnable {
	private final OrderedUdpServerListener listener;
	private final UdpServer udpServer;
	private final Map<SocketAddress, OrderedKnownClient> addrToClient = new HashMap<>();

	public OrderedUdpServer(final int port, final OrderedUdpServerListener listener) throws IOException {
		this.listener = listener;
		this.udpServer = new UdpServer(port, this);
	}

	@Override
	public void parse(final SocketAddress sourceAddress, final ByteBuffer buffer) {
		getClient(sourceAddress).parse(buffer);
	}

	public void send(final SocketAddress destination, final ByteBuffer buffer) throws IOException {
		getClient(destination).send(buffer);
	}

	private OrderedKnownClient getClient(final SocketAddress sourceAddress) {
		OrderedKnownClient orderedKnownClient = this.addrToClient.get(sourceAddress);
		if (orderedKnownClient == null) {
			orderedKnownClient = new OrderedKnownClient(sourceAddress, new OrderedAddressedSender(sourceAddress));
			this.addrToClient.put(sourceAddress, orderedKnownClient);
		}
		return orderedKnownClient;
	}

	private class OrderedKnownClient extends OrderedUdpCommuncation {

		private final SocketAddress sourceAddress;

		public OrderedKnownClient(final SocketAddress sourceAddress, final OrderedUdpClientListener listener) {
			super(listener);
			this.sourceAddress = sourceAddress;
		}

		@Override
		protected void trySend(final ByteBuffer data) {
			try {
				OrderedUdpServer.this.udpServer.send(this.sourceAddress, data);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	private class OrderedAddressedSender implements OrderedUdpClientListener {
		private final SocketAddress sourceAddress;

		public OrderedAddressedSender(final SocketAddress sourceAddress) {
			this.sourceAddress = sourceAddress;
		}

		@Override
		public void parse(final ByteBuffer buffer) {
			OrderedUdpServer.this.listener.parse(this.sourceAddress, buffer);
		}

		@Override
		public void cantReplay(final int seqNo) {
			OrderedUdpServer.this.listener.cantReplay(this.sourceAddress, seqNo);
		}

	}

	@Override
	public void run() {
		this.udpServer.run();
	}
}

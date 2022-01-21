package net.warsmash.networking.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class UdpServer implements Runnable {

	private final Selector selector;
	private boolean running;
	private final SelectionKey key;
	private final ByteBuffer readBuffer;
	private final UdpServerListener serverListener;
	private final DatagramChannel channel;

	public UdpServer(final int portNumber, final UdpServerListener serverListener) throws IOException {
		this.serverListener = serverListener;
		this.selector = Selector.open();
		this.channel = DatagramChannel.open().bind(new InetSocketAddress(portNumber));
		this.channel.configureBlocking(false);
		this.key = this.channel.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		this.readBuffer = ByteBuffer.allocate(1024);
		this.readBuffer.order(ByteOrder.BIG_ENDIAN);
	}

	public void send(final SocketAddress destination, final ByteBuffer buffer) throws IOException {
		this.channel.send(buffer, destination);
	}

	@Override
	public void run() {
		this.running = true;
		while (this.running) {
			try {
				final int selectedKeyCount = this.selector.select();
				if (selectedKeyCount > 0) {
					final Set<SelectionKey> selectedKeys = this.selector.selectedKeys();

					final Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

					while (keyIterator.hasNext()) {
						final SelectionKey key = keyIterator.next();

						if (key.isReadable()) {
							final DatagramChannel channel = (DatagramChannel) key.channel();
							this.readBuffer.clear();
							final SocketAddress receiveAddr = channel.receive(this.readBuffer);
							this.readBuffer.flip();
							this.serverListener.parse(receiveAddr, this.readBuffer);
						}

						keyIterator.remove();
					}
				}
			}
			catch (final IOException e) {
				System.err.println("Error reading from channel:");
				e.printStackTrace();
			}
		}
	}

	public void setRunning(final boolean running) {
		this.running = running;
	}

}

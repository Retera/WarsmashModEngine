package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import com.etheller.warsmash.util.WarsmashConstants;

public class WarsmashGameServer implements Runnable {

	private final Selector selector;
	private boolean running;
	private final SelectionKey key;
	private final ByteBuffer readBuffer;

	public WarsmashGameServer() throws IOException {
		this.selector = Selector.open();
		this.running = true;
		final DatagramChannel channel = DatagramChannel.open()
				.bind(new InetSocketAddress(WarsmashConstants.PORT_NUMBER));
		channel.configureBlocking(false);
		this.key = channel.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		this.readBuffer = ByteBuffer.allocate(256);
	}

	@Override
	public void run() {
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
							System.out.println("NOTE - Received from: " + receiveAddr);
							while (this.readBuffer.hasRemaining()) {
								System.out.println("NOTE - Received: " + this.readBuffer.get());
							}
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

	public static void main(final String[] args) {
		WarsmashGameServer warsmashGameServer;
		try {
			warsmashGameServer = new WarsmashGameServer();
			new Thread(warsmashGameServer).start();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

}

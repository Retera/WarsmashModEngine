package net.warsmash.nio.channels;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

import net.warsmash.networking.udp.UdpServerListener;
import net.warsmash.nio.channels.tcp.TCPClientKeyAttachment;
import net.warsmash.nio.channels.tcp.TCPClientParser;
import net.warsmash.nio.channels.tcp.TCPServerKeyAttachment;
import net.warsmash.nio.channels.udp.UDPServerKeyAttachment;
import net.warsmash.nio.util.ExceptionListener;

public class SelectableChannelOpener implements ChannelOpener {

	private Selector selector;
	private int openChannelCount;
	private final ChannelListener channelListener;

	public SelectableChannelOpener() {
		this.channelListener = new ClosedChannelListenerImpl();
	}

	private void ensureSelectorOpen() throws IOException {
		if (this.selector == null) {
			this.selector = SelectorProvider.provider().openSelector();
		}
	}

	@Override
	public OpenedChannel openUDPServerChannel(final int port, final UdpServerListener listener,
			final ExceptionListener exceptionListener, final int bufferSize, final ByteOrder byteOrder) {
		try {
			ensureSelectorOpen();
			final DatagramChannel channel = DatagramChannel.open().bind(new InetSocketAddress(port));
			channel.configureBlocking(false);
			final ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
			readBuffer.order(byteOrder);
			final UDPServerKeyAttachment udpServerKeyAttachment = new UDPServerKeyAttachment(readBuffer, listener,
					channel, exceptionListener, this.channelListener);
			udpServerKeyAttachment.setKey(channel.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE,
					udpServerKeyAttachment));
			this.channelListener.channelOpened();
			return udpServerKeyAttachment;
		}
		catch (final IOException e) {
			throw new RuntimeException("Exception while opening channel", e);
		}
	}

	@Override
	public OpenedChannel openTCPServerChannel(final int port, final SocketChannelCallback callback,
			final ExceptionListener exceptionListener, final int bufferSize, final ByteOrder byteOrder) {
		try {
			ensureSelectorOpen();
			final ServerSocketChannel channel = ServerSocketChannel.open().bind(new InetSocketAddress(port));
			channel.configureBlocking(false);
			final TCPServerKeyAttachment tcpServerKeyAttachment = new TCPServerKeyAttachment(this.selector, channel,
					callback, exceptionListener, this.channelListener, bufferSize, byteOrder);
			tcpServerKeyAttachment
					.setKey(channel.register(this.selector, SelectionKey.OP_ACCEPT, tcpServerKeyAttachment));
			this.channelListener.channelOpened();
			return tcpServerKeyAttachment;
		}
		catch (final IOException e) {
			throw new RuntimeException("Exception while opening channel", e);
		}
	}

	@Override
	public WritableOutput openTCPClientChannel(final SocketAddress socketAddress, final TCPClientParser tcpClientParser,
			final ExceptionListener exceptionListener, final int bufferSize, final ByteOrder byteOrder) {
		try {
			ensureSelectorOpen();
			final SocketChannel channel = SocketChannel.open();
			final boolean connected = channel.connect(socketAddress);
			channel.configureBlocking(false);
			final ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize).order(byteOrder);
			final ByteBuffer writeBuffer = ByteBuffer.allocate(bufferSize).order(byteOrder);
			final TCPClientKeyAttachment keyAttachment = new TCPClientKeyAttachment(this.selector, channel,
					exceptionListener, this.channelListener, readBuffer, writeBuffer);
			keyAttachment.setParser(tcpClientParser);
			if (connected) {
				final SelectionKey key = channel.register(this.selector,
						SelectionKey.OP_READ/* | SelectionKey.OP_WRITE */, keyAttachment);
				keyAttachment.setKey(key);
			}
			else {
				throw new IllegalStateException("Not connected");
			}
			this.channelListener.channelOpened();
			return keyAttachment;
		}
		catch (final IOException e) {
			throw new RuntimeException("Exception while opening channel", e);
		}
	}

	public void select(final int timeout) {
		try {
			final int selectedKeyCount = this.selector.select(timeout);
			if (selectedKeyCount > 0) {
				final Set<SelectionKey> selectedKeys = this.selector.selectedKeys();

				final Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

				while (keyIterator.hasNext()) {
					final SelectionKey key = keyIterator.next();
					if (key.isValid()) {
						((KeyAttachment) key.attachment()).selected();
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

	private final class ClosedChannelListenerImpl implements ChannelListener {
		@Override
		public void channelClosed() {
			SelectableChannelOpener.this.openChannelCount--;
			if (SelectableChannelOpener.this.openChannelCount == 0) {
				try {
					SelectableChannelOpener.this.selector.close();
				}
				catch (final IOException e) {
					throw new RuntimeException(e);
				}
				SelectableChannelOpener.this.selector = null;
			}
		}

		@Override
		public void channelOpened() {
			SelectableChannelOpener.this.openChannelCount++;
		}
	}
}

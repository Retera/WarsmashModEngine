package net.warsmash.nio.channels.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import net.warsmash.nio.channels.ChannelListener;
import net.warsmash.nio.channels.KeyAttachment;
import net.warsmash.nio.channels.OpenedChannel;
import net.warsmash.nio.channels.SocketChannelCallback;
import net.warsmash.nio.util.ExceptionListener;

public class TCPServerKeyAttachment implements KeyAttachment, OpenedChannel {
	private final ServerSocketChannel channel;
	private final SocketChannelCallback callback;
	private final ExceptionListener exceptionListener;
	private final Selector selector;
	private final ChannelListener channelListener;
	private final int bufferSize;
	private final ByteOrder byteOrder;
	private SelectionKey key;

	public TCPServerKeyAttachment(final Selector selector, final ServerSocketChannel channel,
			final SocketChannelCallback callback, final ExceptionListener exceptionListener,
			final ChannelListener channelListener, final int bufferSize, final ByteOrder byteOrder) {
		this.selector = selector;
		this.channel = channel;
		this.callback = callback;
		this.exceptionListener = exceptionListener;
		this.channelListener = channelListener;
		this.bufferSize = bufferSize;
		this.byteOrder = byteOrder;
	}

	public void setKey(final SelectionKey key) {
		this.key = key;
	}

	@Override
	public void selected() {
		if (this.key.isAcceptable()) {
			try {
				final SocketChannel socketChannel = this.channel.accept();
				socketChannel.configureBlocking(false);
				final ByteBuffer readBuffer = ByteBuffer.allocate(this.bufferSize);
				readBuffer.order(this.byteOrder);
				final ByteBuffer writeBuffer = ByteBuffer.allocate(this.bufferSize);
				writeBuffer.order(this.byteOrder);
				final TCPClientKeyAttachment tcpServerClientKeyAttachment = new TCPClientKeyAttachment(this.selector,
						socketChannel, this.exceptionListener, this.channelListener, readBuffer, writeBuffer);
				final TCPClientParser parser = this.callback.onConnect(tcpServerClientKeyAttachment,
						socketChannel.getRemoteAddress());
				tcpServerClientKeyAttachment.setParser(parser);
				tcpServerClientKeyAttachment.setKey(socketChannel.register(this.selector,
						SelectionKey.OP_READ/* | SelectionKey.OP_WRITE */, tcpServerClientKeyAttachment));
				this.channelListener.channelOpened();
			}
			catch (final IOException e) {
				this.exceptionListener.caught(e);
			}
		}
	}

	@Override
	public void close() {
		try {
			this.channel.close();
			this.channelListener.channelClosed();
		}
		catch (final IOException e) {
			this.exceptionListener.caught(e);
		}
	}

}

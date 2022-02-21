package net.warsmash.nio.channels.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import net.warsmash.nio.channels.ChannelListener;
import net.warsmash.nio.channels.KeyAttachment;
import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.nio.util.ExceptionListener;

public class TCPClientKeyAttachment implements KeyAttachment, WritableOutput {
	private TCPClientParser parser;
	private final Selector selector;
	private final SocketChannel channel;
	private final ExceptionListener exceptionListener;
	private final ChannelListener channelListener;
	private final ByteBuffer readBuffer;
	private final ByteBuffer writeBuffer;
	private SelectionKey key;

	public TCPClientKeyAttachment(final Selector selector, final SocketChannel channel,
			final ExceptionListener exceptionListener, final ChannelListener channelListener,
			final ByteBuffer readBuffer, final ByteBuffer writeBuffer) {
		this.selector = selector;
		this.channel = channel;
		this.exceptionListener = exceptionListener;
		this.channelListener = channelListener;
		this.readBuffer = readBuffer;
		this.writeBuffer = writeBuffer;
	}

	public void setParser(final TCPClientParser parser) {
		this.parser = parser;
	}

	public void setKey(final SelectionKey key) {
		this.key = key;
	}

	@Override
	public void selected() {
		if (this.key.isReadable()) {
			this.readBuffer.clear();
			try {
				final int nRead = this.channel.read(this.readBuffer);
				if (nRead > 0) {
					this.readBuffer.flip();
					this.parser.parse(this.readBuffer);
				}
				else if (nRead == -1) {
					this.parser.disconnected();
					close();
					return;
				}
				else {
					throw new IOException("Did not read bytes");
				}
			}
			catch (final IOException e) {
				close();
				this.exceptionListener.caught(e);
				return;
			}
		}
		if (this.key.isWritable()) {
			this.writeBuffer.flip();
			if (this.writeBuffer.remaining() > 0) {
				try {
					this.channel.write(this.writeBuffer);
				}
				catch (final IOException e) {
					close();
					this.exceptionListener.caught(e);
					return;
				}
			}
			this.writeBuffer.compact();
		}
	}

	@Override
	public void close() {
		try {
			if (this.key != null) {
				this.key.cancel();
			}
			else {
				throw new IllegalStateException("close() called multiple times on TCPClientKeyAttachment");
			}
			this.key = null;
			this.channel.close();
			this.channelListener.channelClosed();
		}
		catch (final IOException e) {
			this.exceptionListener.caught(e);
		}
	}

	@Override
	public void write(final ByteBuffer data) {
		final int bytesWanted = data.remaining();
		try {
			if (this.channel.write(data) < bytesWanted) {
				this.writeBuffer.put(data);
			}
		}
		catch (final IOException e) {
			this.exceptionListener.caught(e);
		}
	}

}

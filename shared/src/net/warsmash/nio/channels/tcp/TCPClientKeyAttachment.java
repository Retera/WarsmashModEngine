package net.warsmash.nio.channels.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import net.warsmash.nio.channels.ChannelListener;
import net.warsmash.nio.channels.KeyAttachment;
import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.nio.channels.WritableSocketOutput;
import net.warsmash.nio.util.ExceptionListener;

public class TCPClientKeyAttachment implements KeyAttachment, WritableSocketOutput {
	private static final int MAX_MAP_SIZE_ROUGHLY = 256 * 1024 * 1024;
	private TCPClientParser parser;
	private final Selector selector;
	private final SocketChannel channel;
	private final ExceptionListener exceptionListener;
	private final ChannelListener channelListener;
	private final ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;
	private final Object writeBufferLock = new Object();
	private SelectionKey key;
	private boolean queueingWrites = false;

	public TCPClientKeyAttachment(final Selector selector, final SocketChannel channel,
			final ExceptionListener exceptionListener, final ChannelListener channelListener,
			final ByteBuffer readBuffer, final ByteBuffer writeBuffer) {
		this.selector = selector;
		this.channel = channel;
		this.exceptionListener = exceptionListener;
		this.channelListener = channelListener;
		this.readBuffer = readBuffer;
		this.readBuffer.clear();
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
			try {
				final int nRead = this.channel.read(this.readBuffer);
				if (nRead > 0) {
					this.readBuffer.flip();
					this.parser.parse(this.readBuffer);
					this.readBuffer.compact();
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
			catch (final Exception e) {
				this.parser.disconnected();
				try {
					close();
				}
				catch (final Exception exc) {
					// TODO this extra catch case is a bit of a pain
					this.exceptionListener.caught(e);
					this.exceptionListener.caught(exc);
					return;
				}
				this.exceptionListener.caught(e);
				return;
			}
		}
		if ((this.key != null) && this.key.isWritable()) {
			synchronized (this.writeBufferLock) {
				this.writeBuffer.flip();
				if (this.writeBuffer.remaining() > 0) {
					try {
						final int r = this.writeBuffer.remaining();
						this.channel.write(this.writeBuffer);
						final int r2 = this.writeBuffer.remaining();
						System.err.println("wrote queued " + (r - r2));
					}
					catch (final Exception e) {
						this.parser.disconnected();
						close();
						this.exceptionListener.caught(e);
						return;
					}
				}
				if (this.writeBuffer.remaining() == 0) {
					try {
						this.key = this.channel.register(this.selector, SelectionKey.OP_READ, this);
						this.queueingWrites = false;
					}
					catch (final Exception e) {
						this.exceptionListener.caught(e);
					}
				}
				this.writeBuffer.compact();
			}
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
			synchronized (this.writeBufferLock) {
				if (!this.queueingWrites) {
					if (this.channel.write(data) < bytesWanted) {
						putDataInWriteBuffer(data);
						this.queueingWrites = true;
						this.key = this.channel.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE,
								this);
					}
				}
				else {
					putDataInWriteBuffer(data);
				}
			}
		}
		catch (final IOException e) {
			this.exceptionListener.caught(e);
		}
	}

	private void putDataInWriteBuffer(final ByteBuffer data) {
		System.err.println("queueing write of " + data.remaining());
		if (this.writeBuffer.remaining() >= data.remaining()) {
			this.writeBuffer.put(data);
		}
		else {
			// NOTE: below is the sub-optimal programming solution to try
			// to help goobers who play maps that are larger than 8MB in size.
			// Yes, after this happens, future throttling in writing that needs this
			// buffer may be slower even for smaller maps. So, if you play a giant
			// map, it might benefit you to later restart the client.
			final ByteBuffer newWriteBuffer = ByteBuffer.allocate(this.writeBuffer.capacity() * 2);
			this.writeBuffer.flip();
			newWriteBuffer.put(this.writeBuffer);
			newWriteBuffer.put(data);
			this.writeBuffer = newWriteBuffer;
		}
	}

	@Override
	public SocketAddress getLocalAddress() {
		try {
			return channel.getLocalAddress();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SocketAddress getRemoteAddress() {
		try {
			return channel.getRemoteAddress();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}

package net.warsmash.nio.channels.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import net.warsmash.networking.udp.UdpServerListener;
import net.warsmash.nio.channels.ChannelListener;
import net.warsmash.nio.channels.KeyAttachment;
import net.warsmash.nio.channels.OpenedChannel;
import net.warsmash.nio.util.ExceptionListener;

public class UDPServerKeyAttachment implements KeyAttachment, OpenedChannel {
	private final ByteBuffer readBuffer;
	private final UdpServerListener serverListener;
	private final DatagramChannel channel;
	private final ExceptionListener exceptionListener;
	private final ChannelListener channelListener;
	private SelectionKey key;

	public UDPServerKeyAttachment(final ByteBuffer readBuffer, final UdpServerListener serverListener,
			final DatagramChannel channel, final ExceptionListener exceptionListener,
			final ChannelListener channelListener) {
		this.readBuffer = readBuffer;
		this.serverListener = serverListener;
		this.channel = channel;
		this.exceptionListener = exceptionListener;
		this.channelListener = channelListener;
	}

	public void setKey(final SelectionKey key) {
		this.key = key;
	}

	@Override
	public void selected() {
		if (this.key.isReadable()) {
			this.readBuffer.clear();
			SocketAddress receiveAddr;
			try {
				receiveAddr = this.channel.receive(this.readBuffer);
				this.readBuffer.flip();
				this.serverListener.parse(receiveAddr, this.readBuffer);
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

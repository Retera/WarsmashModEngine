package net.warsmash.nio.channels.tcp;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import net.warsmash.nio.channels.KeyAttachment;
import net.warsmash.nio.util.ExceptionListener;

public class ConnectionFinishingKeyAttachment implements KeyAttachment {
	private final Selector selector;
	private final SocketChannel channel;
	private final ExceptionListener exceptionListener;
	private final KeyAttachment delegate;
	private SelectionKey key;

	public ConnectionFinishingKeyAttachment(final Selector selector, final SocketChannel channel,
			final ExceptionListener exceptionListener, final KeyAttachment delegate) {
		this.selector = selector;
		this.channel = channel;
		this.exceptionListener = exceptionListener;
		this.delegate = delegate;
	}

	public void setKey(final SelectionKey key) {
		this.key = key;
	}

	@Override
	public void selected() {
		try {
			this.channel.finishConnect();
			System.out.println("finishConnect()");
			this.key.cancel();
//			delegate.setKey(channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, delegate));
		}
		catch (final IOException e) {
			this.exceptionListener.caught(e);
		}
	}
}

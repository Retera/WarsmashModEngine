package net.warsmash.networking.tcp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;

import net.warsmash.nio.channels.SelectableChannelOpener;
import net.warsmash.nio.channels.SocketChannelCallback;
import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.nio.channels.tcp.TCPClientParser;
import net.warsmash.nio.util.ExceptionListener;

public class TestChatServer {
	private static final int PORT = 8989;

	public static void main(final String[] args) {
		final SelectableChannelOpener selectableChannelOpener = new SelectableChannelOpener();
		final Set<WritableOutput> outputs = new HashSet<>();
		selectableChannelOpener.openTCPServerChannel(PORT, new SocketChannelCallback() {
			@Override
			public TCPClientParser onConnect(final WritableOutput writableOpenedChannel,
					final SocketAddress remoteAddress) {
				System.out.println("Received connection from " + remoteAddress);
				outputs.add(writableOpenedChannel);
				return new TCPClientParser() {
					@Override
					public void parse(final ByteBuffer data) {
						final byte[] bs = new byte[data.remaining()];
						data.get(bs);
						final String message = new String(bs);
						for (final WritableOutput output : outputs) {
							output.write(ByteBuffer.wrap((remoteAddress.toString() + ":" + message).getBytes()));
						}
					}

					@Override
					public void disconnected() {
						outputs.remove(writableOpenedChannel);
						for (final WritableOutput output : outputs) {
							output.write(
									ByteBuffer.wrap((remoteAddress.toString() + " has left the chat.").getBytes()));
						}
						System.out.println("Disconnected from " + remoteAddress);
					}
				};
			}
		}, ExceptionListener.THROW_RUNTIME, 8 * 1024 * 1024, ByteOrder.BIG_ENDIAN);

		while (true) {
			selectableChannelOpener.select(0);
		}
	}
}

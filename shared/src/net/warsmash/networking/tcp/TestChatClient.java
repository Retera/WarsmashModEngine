package net.warsmash.networking.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.warsmash.nio.channels.SelectableChannelOpener;
import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.nio.channels.tcp.TCPClientParser;
import net.warsmash.nio.util.ExceptionListener;

public class TestChatClient {
	private static final int PORT = 8989;

	public static void main(final String[] args) {
		final SelectableChannelOpener selectableChannelOpener = new SelectableChannelOpener();

		final ByteBuffer writeBuffer = ByteBuffer.allocate(8 * 1024 * 1024).order(ByteOrder.BIG_ENDIAN);
		final WritableOutput clientChannel = selectableChannelOpener
				.openTCPClientChannel(new InetSocketAddress("localhost", PORT), new TCPClientParser() {
					@Override
					public void parse(final ByteBuffer data) {
						final byte[] bs = new byte[data.remaining()];
						data.get(bs);
						final String message = new String(bs);
						System.out.println(message);
					}

					@Override
					public void disconnected() {
						System.out.println("TCP disconnected!");
					}
				}, ExceptionListener.THROW_RUNTIME, 8 * 1024 * 1024, ByteOrder.BIG_ENDIAN);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					selectableChannelOpener.select(0);
				}
			}
		}).start();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String line;
			while ((line = reader.readLine()) != null) {
				writeBuffer.clear();
				writeBuffer.put(line.getBytes());
				writeBuffer.flip();
				clientChannel.write(writeBuffer);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}

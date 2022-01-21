package net.warsmash.networking.tcp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.warsmash.nio.channels.SelectableChannelOpener;
import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.nio.channels.tcp.TCPClientParser;
import net.warsmash.nio.util.ExceptionListener;

public class TCPTestClient {
	private static final int PORT = 8989;

	public static void main(final String[] args) {
		final SelectableChannelOpener selectableChannelOpener = new SelectableChannelOpener();

		final ByteBuffer writeBuffer = ByteBuffer.allocate(8 * 1024 * 1024).order(ByteOrder.BIG_ENDIAN);
		final WritableOutput clientChannel = selectableChannelOpener
				.openTCPClientChannel(new InetSocketAddress("localhost", PORT), new TCPClientParser() {
					@Override
					public void parse(final ByteBuffer data) {
						System.out.println("Got " + data.remaining() + " bytes from server!");
						if (data.hasRemaining()) {
							System.out.print("[");
							System.out.print(data.get());
							while (data.hasRemaining()) {
								System.out.print(", ");
								System.out.print(data.get());
							}
							System.out.println("]");
						}
					}

					@Override
					public void disconnected() {
						System.out.println("TCP disconnected!");
					}
				}, ExceptionListener.THROW_RUNTIME, 8 * 1024 * 1024, ByteOrder.BIG_ENDIAN);

		writeBuffer.clear();
		for (int i = 0; i < 4; i++) {
			writeBuffer.put((byte) ((i * 2) + 1));
		}
		writeBuffer.flip();
		clientChannel.write(writeBuffer);

		while (true) {
			selectableChannelOpener.select(0);
		}
	}
}

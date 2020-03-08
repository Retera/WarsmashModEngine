package com.etheller.warsmash.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.etheller.warsmash.util.WarsmashConstants;

public class TestClient {

	public static void main(final String[] args) {
		try {
			try (final DatagramChannel channel = DatagramChannel.open()
					.connect(new InetSocketAddress(InetAddress.getLocalHost(), WarsmashConstants.PORT_NUMBER))) {
				final ByteBuffer buffer = ByteBuffer.allocate(256);
				buffer.clear();
				buffer.put((byte) 3);
				buffer.put((byte) 2);
				buffer.put((byte) 4);
				buffer.put((byte) 1);
				buffer.flip();
				channel.write(buffer);
			}
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

}

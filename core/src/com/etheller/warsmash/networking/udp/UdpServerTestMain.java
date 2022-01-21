package com.etheller.warsmash.networking.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import com.etheller.warsmash.util.WarsmashConstants;

import net.warsmash.networking.udp.UdpServer;
import net.warsmash.networking.udp.UdpServerListener;

public class UdpServerTestMain {

	static UdpServer warsmashGameServer;

	public static void main(final String[] args) {
		try {
			warsmashGameServer = new UdpServer(WarsmashConstants.PORT_NUMBER, new UdpServerListener() {
				int n = 0;
				ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

				@Override
				public void parse(final SocketAddress sourceAddress, final ByteBuffer buffer) {
					System.out.println("Got packet from: " + sourceAddress);
					while (buffer.hasRemaining()) {
						System.out.println("Received: " + buffer.get());
					}
					try {
						this.sendBuffer.clear();
						this.sendBuffer.putInt(this.n++);
						this.sendBuffer.flip();
						warsmashGameServer.send(sourceAddress, this.sendBuffer);
					}
					catch (final IOException e) {
						e.printStackTrace();
					}
				}
			});
			new Thread(warsmashGameServer).start();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

}

package com.etheller.warsmash.networking.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import com.etheller.warsmash.util.WarsmashConstants;

import net.warsmash.networking.udp.UdpClient;
import net.warsmash.networking.udp.UdpClientListener;

public class UdpClientTestMain {

	static UdpClient warsmashUdpClient;

	public static void main(final String[] args) {
		try {
			warsmashUdpClient = new UdpClient(InetAddress.getLocalHost(), WarsmashConstants.PORT_NUMBER,
					new UdpClientListener() {
						@Override
						public void parse(final ByteBuffer buffer) {
							System.out.println("got " + buffer.remaining() + " bytes, pos=" + buffer.position()
									+ ", lim=" + buffer.limit());
							System.out.println("got from server: " + buffer.getInt());
						}
					});
			new Thread(warsmashUdpClient).start();

			final ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
			for (int i = 0; i < 10; i++) {
				sendBuffer.clear();
				sendBuffer.put((byte) (1 + i));
				sendBuffer.put((byte) 3);
				sendBuffer.put((byte) 5);
				sendBuffer.put((byte) 7);
				sendBuffer.flip();
				warsmashUdpClient.send(sendBuffer);
				try {
					Thread.sleep(1000);
				}
				catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		catch (final UnknownHostException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

}

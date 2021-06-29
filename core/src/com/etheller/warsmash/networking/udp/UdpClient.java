package com.etheller.warsmash.networking.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;

import com.etheller.warsmash.util.WarsmashConstants;

public class UdpClient implements Runnable {
	private final DatagramChannel channel;
	private final ByteBuffer readBuffer;
	private boolean running;
	private final UdpClientListener clientListener;

	public UdpClient(final InetAddress serverAddress, final int portNumber, final UdpClientListener clientListener)
			throws UnknownHostException, IOException {
		this.channel = DatagramChannel.open().connect(new InetSocketAddress(serverAddress, portNumber));
		this.channel.configureBlocking(true);
		this.readBuffer = ByteBuffer.allocate(1024);
		this.clientListener = clientListener;
		this.readBuffer.order(ByteOrder.BIG_ENDIAN);
	}

	public void send(final ByteBuffer data) throws IOException {
		this.channel.write(data);
	}

	public void setRunning(final boolean running) {
		this.running = running;
	}

	@Override
	public void run() {
		this.running = true;
		while (this.running) {
			try {
				this.readBuffer.clear();
				this.channel.receive(this.readBuffer);
				this.readBuffer.flip();
				this.clientListener.parse(this.readBuffer);
			}
			catch (final IOException e) {
				System.err.println("Error reading from channel:");
				e.printStackTrace();
			}
		}
	}

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

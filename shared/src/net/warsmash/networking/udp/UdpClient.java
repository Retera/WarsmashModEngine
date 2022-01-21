package net.warsmash.networking.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;

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

}

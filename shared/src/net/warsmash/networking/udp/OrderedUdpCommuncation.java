package net.warsmash.networking.udp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public abstract class OrderedUdpCommuncation implements UdpClientListener {
	private static final int MAX_STORED_SENT_DATA_SIZE = 10000;

	private static final int ORDERED_UDP_MESSAGE = 'M';
	private static final int ORDERED_UDP_REPLAY_REQUEST = 'R';

	private final Map<Integer, ByteBuffer> seqNoToDataSent;
	private final Map<Integer, ByteBuffer> seqNoToDataReceived;
	private final Queue<Integer> seqNosStored;
	private int nextSendSeqNo;
	private int nextReceiveSeqNo;
	private final OrderedUdpClientListener delegate;
	private final ByteBuffer sendBuffer;

	public OrderedUdpCommuncation(final OrderedUdpClientListener delegate) {
		this.seqNoToDataSent = new HashMap<>();
		this.seqNoToDataReceived = new HashMap<>();
		this.seqNosStored = new ArrayDeque<>();
		this.sendBuffer = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN);
		this.delegate = delegate;
	}

	public void send(final ByteBuffer data) throws IOException {
		final ByteBuffer writeBuffer = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN);
		writeBuffer.clear();
		final Integer seqNo = this.nextSendSeqNo; // only autobox once, would be ideal to not box at all
		writeBuffer.putInt(ORDERED_UDP_MESSAGE);
		writeBuffer.putInt(seqNo);
		writeBuffer.put(data);
		writeBuffer.flip();
		final int position = writeBuffer.position();
		trySend(writeBuffer);
		writeBuffer.position(position);
		if (this.seqNosStored.size() == MAX_STORED_SENT_DATA_SIZE) {
			this.seqNoToDataSent.remove(this.seqNosStored.poll());
		}
		this.seqNosStored.offer(seqNo);
		this.seqNoToDataSent.put(seqNo, writeBuffer);
		this.nextSendSeqNo++;
	}

	// it's udp so we're just trying, we don't really know if it'll drop or not
	protected abstract void trySend(final ByteBuffer data);

	@Override
	public void parse(ByteBuffer readBuffer) {
		final int messageType = readBuffer.getInt();
		switch (messageType) {
		case ORDERED_UDP_MESSAGE: {
			final int serverSeqNo = readBuffer.getInt();
			if (serverSeqNo > this.nextReceiveSeqNo) {
				// ahead, need to queue it and request replay
				for (int i = this.nextReceiveSeqNo; i < serverSeqNo; i++) {
					requestReplay(i);
				}
				final ByteBuffer queuedReceivedData = ByteBuffer.allocate(readBuffer.remaining())
						.order(ByteOrder.BIG_ENDIAN);
				queuedReceivedData.put(readBuffer);
				this.seqNoToDataReceived.put(serverSeqNo, queuedReceivedData);
			}
			else if (serverSeqNo < this.nextReceiveSeqNo) {
				// dup, ignore
			}
			else {
				// exactly equal
				do {
					this.delegate.parse(readBuffer);
					this.nextReceiveSeqNo++;
				}
				while ((readBuffer = this.seqNoToDataReceived.remove(this.nextReceiveSeqNo)) != null);
			}
			break;
		}
		case ORDERED_UDP_REPLAY_REQUEST:
			final int requestedSeqNo = readBuffer.getInt();
			final ByteBuffer replayData = this.seqNoToDataSent.get(requestedSeqNo);
			if (replayData == null) {
				this.delegate.cantReplay(requestedSeqNo);
			}
			else {
				System.err.println("Replay requested for packet with seqNo=" + requestedSeqNo + ", we will send it!");
				final int position = replayData.position();
				trySend(replayData);
				replayData.position(position);
			}
			break;
		}
	}

	private void requestReplay(final int i) {
		this.sendBuffer.clear();
		this.sendBuffer.putInt(ORDERED_UDP_REPLAY_REQUEST);
		this.sendBuffer.putInt(i);
		this.sendBuffer.flip();
		trySend(this.sendBuffer);
	}
}

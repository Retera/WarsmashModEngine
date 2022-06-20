package net.warsmash.map;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32C;

public class NetMapDownloader {
	private final File mapFilePath;
	private FileChannel mapDataFileChannel;
	private int nextSequenceNumber = -1;
	private boolean sequenceNumberingOK = true;

	public NetMapDownloader(File mapFilePath) {
		this.mapFilePath = mapFilePath;
		try {
			mapFilePath.getParentFile().mkdirs();
			this.mapDataFileChannel = FileChannel.open(this.mapFilePath.toPath(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public void receive(int sequenceNumber, ByteBuffer data) {
		try {
			if ((this.nextSequenceNumber != -1) && (this.nextSequenceNumber != sequenceNumber)) {
				this.sequenceNumberingOK = false;
				System.err.println(
						"Out of sequence map data: Expected " + this.nextSequenceNumber + ", got " + sequenceNumber);
			}
			this.mapDataFileChannel.write(data);
			this.nextSequenceNumber = sequenceNumber + 1;
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public long finish(int sequenceNumber) {
		if ((this.nextSequenceNumber != -1) && (this.nextSequenceNumber != sequenceNumber)) {
			this.sequenceNumberingOK = false;
			System.err.println(
					"Out of sequence map data: Expected " + this.nextSequenceNumber + ", got " + sequenceNumber);
		}
		try {
			this.mapDataFileChannel.force(false);
			this.mapDataFileChannel.close();
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}
		try (FileChannel readerChannel = FileChannel.open(this.mapFilePath.toPath(), StandardOpenOption.READ)) {
			final ByteBuffer readBuffer = ByteBuffer.allocate(8 * 1024).clear();
			final CRC32C checksum = new CRC32C();
			checksum.reset();
			while ((readerChannel.read(readBuffer)) != -1) {
				readBuffer.flip();
				checksum.update(readBuffer);
				readBuffer.clear();
			}
			final long checksumValue = checksum.getValue();
			return checksumValue;
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public boolean isSequenceNumberingOK() {
		return this.sequenceNumberingOK;
	}
}

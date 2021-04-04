package mpq.compression;

import java.nio.ByteBuffer;

import mpq.MPQException;

public class DecompressionException extends MPQException {

	private static final long serialVersionUID = 5481075695238468958L;
	private final ByteBuffer decompressedBuffer;
	
	public DecompressionException(ByteBuffer buff, String arg0) {
		super(arg0);
		decompressedBuffer = buff.asReadOnlyBuffer();
	}

	public DecompressionException(ByteBuffer buff, String arg0, Throwable arg1) {
		super(arg0, arg1);
		decompressedBuffer = buff.asReadOnlyBuffer();
	}

	public ByteBuffer getDecompressedBuffer() {
		return decompressedBuffer;
	}
}

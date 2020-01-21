package mpq.data;

import java.nio.ByteBuffer;

public class BlockTableEntry extends Raw {
	public static final int STRUCT_SIZE = 16;
		
	public BlockTableEntry(ByteBuffer source) {
		super(source);
	}
	
	public BlockTableEntry() {
		super();
	}

	public int getFilePosition() {
		return data.getInt(0);
	}

	public int getCompressedSize() {
		return data.getInt(4);
	}

	public int getFileSize() {
		return data.getInt(8);
	}

	public int getFlags() {
		return data.getInt(12);
	}
}

package mpq.data;

import java.nio.ByteBuffer;

public class HashTableEntry extends Raw {
	public static final int STRUCT_SIZE = 16;
	
	public HashTableEntry(ByteBuffer source) {
		super(source);
	}
	
	public HashTableEntry() {
		super();
	}

	public long getHash() {
		return data.getLong(0);
	}

	public short getLocale() {
		return data.getShort(8);
	}

	public short getPlatform() {
		return data.getShort(10);
	}

	public int getBlockIndex() {
		return data.getInt(12);
	}
}

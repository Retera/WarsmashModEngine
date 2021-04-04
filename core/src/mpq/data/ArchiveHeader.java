package mpq.data;

import java.nio.ByteBuffer;

public class ArchiveHeader extends Raw{
	public static final int STRUCT_SIZE_V1 = 32 - 8;
	public static final int STRUCT_SIZE_V2 = STRUCT_SIZE_V1 + 12;
	public static final int STRUCT_SIZE_V3 = STRUCT_SIZE_V2 + 24;
	public static final int STRUCT_SIZE_V4 = STRUCT_SIZE_V3 + 44;
	
	public ArchiveHeader(ByteBuffer source) {
		super(source);
	}

	// VERSION 1
	
	public int getArchiveSize() {
		return data.getInt(0);
	}

	public short getFormatVersion() {
		return data.getShort(4);
	}

	public short getBlockSize() {
		return data.getShort(6);
	}

	public int getHashTablePosition() {
		return data.getInt(8);
	}

	public int getBlockTablePosition() {
		return data.getInt(12);
	}

	public int getHashTableSize() {
		return data.getInt(16);
	}

	public int getBlockTableSize() {
		return data.getInt(20);
	}
	
	// VERSION 2
	
	public long getHighBlockTablePosition() {
		return data.getLong(24);
	}
	
	public short getHashTablePositionHigh() {
		return data.getShort(32);
	}
	
	public short getBlockTablePositionHigh() {
		return data.getShort(34);
	}
	
	// VERSION 3
	
	public long getArchiveSizeLong() {
		return data.getLong(36);
	}
	
	public long getBetTablePosition() {
		return data.getLong(44);
	}
	
	public long getHetTablePosition() {
		return data.getLong(52);
	}
	
	// VERSION 4
	
	public long getHashTableSizeCompressed() {
		return data.getLong(60);
	}
	
	public long getBlockTableSizeCompressed() {
		return data.getLong(68);
	}
	
	public long getHighBlockTableSizeCompressed() {
		return data.getLong(76);
	}
	
	public long getHetTableSizeCompressed() {
		return data.getLong(84);
	}
	
	public long getBetTableSizeCompressed() {
		return data.getLong(92);
	}
	
	public int getRawChunkSize() {
		return data.getInt(100);
	}
}

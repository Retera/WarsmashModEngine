package mpq;

import mpq.data.BlockTableEntry;

public class BlockTable {
	public static final int FLAG_IMPLODE = 0x00000100;
	public static final int FLAG_COMPRESS = 0x00000200;
	public static final int FLAG_ENCRYPTED = 0x00010000;
	public static final int FLAG_FIX_KEY = 0x00020000;
	public static final int FLAG_PATCH_FILE = 0x00100000;
	public static final int FLAG_SINGLE_UNIT = 0x01000000;
	public static final int FLAG_DELETE_MARKER = 0x02000000;
	public static final int FLAG_SECTOR_CRC = 0x04000000;
	public static final int FLAG_EXISTS = 0x80000000;
	
	private Entry[] tableArray;
	
	// raw constructor, expects all entries to be non-null.
	public BlockTable(Entry[] entries){
		tableArray = entries;
	}
	
	public Entry lookupEntry(int entry){
		return tableArray[entry];
	}
		
	public static String flagsToString(int source){
		return ( (source&FLAG_IMPLODE) != 0 ? "IMPLODE " : "" )+
				( (source&FLAG_COMPRESS) != 0 ? "COMPRESS " : "" )+
				( (source&FLAG_ENCRYPTED) != 0 ? "ENCRYPTED " : "" )+
				( (source&FLAG_FIX_KEY) != 0 ? "FIX_KEY " : "" )+
				( (source&FLAG_PATCH_FILE) != 0 ? "PATCH_FILE " : "" )+
				( (source&FLAG_SINGLE_UNIT) != 0 ? "SINGLE_UNIT " : "" )+
				( (source&FLAG_DELETE_MARKER) != 0 ? "DELETE_MARKER " : "" )+
				( (source&FLAG_SECTOR_CRC) != 0 ? "SECTOR_CRC " : "" )+
				( (source&FLAG_EXISTS) != 0 ? "EXISTS " : "" );
	}
	
	
	public static class Entry{
		public long filePosition;
		public int compressedSize;
		public int fileSize;
		public int flags;
		
		public Entry(BlockTableEntry source){
			filePosition = source.getFilePosition();
			compressedSize = source.getCompressedSize();
			fileSize = source.getFileSize();
			flags = source.getFlags();
		}
		
		// raw constructor for data field
		public Entry(){
		}

		public int getFilePosition() {
			return (int) filePosition;
		}

		public int getCompressedSize() {
			return compressedSize;
		}

		public int getFileSize() {
			return fileSize;
		}

		public int getFlags() {
			return flags;
		}
		
		public boolean hasFlag(int flag){
			return (flags & flag) != 0;
		}
		
	}
}

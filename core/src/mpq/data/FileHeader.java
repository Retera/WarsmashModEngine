package mpq.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FileHeader extends Raw{
	public static final byte[] ARCHIVE_IDENTIFIER_BYTES = {'M','P','Q',0x1a};
	public static final int ARCHIVE_IDENTIFIER_INT = ByteBuffer.wrap(ARCHIVE_IDENTIFIER_BYTES).order(ByteOrder.LITTLE_ENDIAN).getInt(0);
	public static final byte[] USERDATA_IDENTIFIER_BYTES = {'M','P','Q',0x1b};
	public static final int USERDATA_IDENTIFIER_INT = ByteBuffer.wrap(USERDATA_IDENTIFIER_BYTES).order(ByteOrder.LITTLE_ENDIAN).getInt(0);
	public static final int STRUCT_SIZE = 8;
	
	public FileHeader(ByteBuffer source) {
		super(source);
	}
	
	public byte[] getIdentifierBytes() {
		byte[] bytes = new byte[4];
		data.position(0);
		data.get(bytes);
		return bytes;
	}
	
	public int getIdentifierInt() {
		return data.getInt(0);
	}
	
	public int getHeaderSize() {
		return data.getInt(4);
	}		
}

package mpq.data;

import java.nio.ByteBuffer;

public class UserDataHeader extends Raw{
	public static final int STRUCT_SIZE = 16 - 8;
	
	public UserDataHeader(ByteBuffer source) {
		super(source);
	}

	
	public int getArchiveOffset() {
		return data.getInt(0);
	}

	public int getUserDataSize() {
		return data.getInt(4);
	}

}

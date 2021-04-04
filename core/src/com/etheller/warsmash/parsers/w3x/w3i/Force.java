package com.etheller.warsmash.parsers.w3x.w3i;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Force {
	private long flags;
	private long playerMasks;
	private String name;

	public void load(final LittleEndianDataInputStream stream) throws IOException {
		this.flags = ParseUtils.readUInt32(stream);
		this.playerMasks = ParseUtils.readUInt32(stream);
		this.name = ParseUtils.readUntilNull(stream);
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, this.flags);
		ParseUtils.writeUInt32(stream, this.playerMasks);
		ParseUtils.writeWithNullTerminator(stream, this.name);
	}

	public int getByteLength() {
		return 9 + this.name.length();
	}
}

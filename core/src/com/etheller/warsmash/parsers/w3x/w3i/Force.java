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

	public long getFlags() {
		return this.flags;
	}

	public long getPlayerMasks() {
		return this.playerMasks;
	}

	public String getName() {
		return this.name;
	}

	public int getByteLength() {
		return 9 + this.name.length();
	}

	public static final class Flag {
		public static final int ALLIED = 0x0001;
		public static final int ALLIED_VICTORY = 0x0002;
		public static final int SHARE_VISION = 0x0008;
		public static final int SHARE_UNIT_CONTROL = 0x0010;
		public static final int SHARE_ADV_UNIT_CONTROL = 0x0020;
	}
}

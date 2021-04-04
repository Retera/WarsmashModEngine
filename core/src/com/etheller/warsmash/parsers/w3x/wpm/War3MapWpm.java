package com.etheller.warsmash.parsers.w3x.wpm;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;

public class War3MapWpm {
	private static final War3ID MAGIC_NUMBER = War3ID.fromString("MP3W");
	private int version;
	private final int[] size = new int[2];
	private short[] pathing;

	public War3MapWpm(final LittleEndianDataInputStream stream) throws IOException {
		if (stream != null) {
			this.load(stream);
		}
	}

	private boolean load(final LittleEndianDataInputStream stream) throws IOException {
		final War3ID firstId = ParseUtils.readWar3ID(stream);
		if (!MAGIC_NUMBER.equals(firstId)) {
			return false;
		}

		this.version = stream.readInt();
		ParseUtils.readInt32Array(stream, this.size);
		this.pathing = ParseUtils.readUInt8Array(stream, this.size[0] * this.size[1]);

		return true;
	}

	public int getVersion() {
		return this.version;
	}

	public int[] getSize() {
		return this.size;
	}

	public short[] getPathing() {
		return this.pathing;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public void setPathing(final short[] pathing) {
		this.pathing = pathing;
	}

}

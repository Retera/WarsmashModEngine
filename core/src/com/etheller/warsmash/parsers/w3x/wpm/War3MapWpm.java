package com.etheller.warsmash.parsers.w3x.wpm;

import java.io.IOException;

import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.wdt.WdtMap;
import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class War3MapWpm {
	private static final War3ID MAGIC_NUMBER = War3ID.fromString("MP3W");
	private int version;
	private final int[] size = new int[2];
	private short[][] pathing;

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
		this.pathing = new short[this.size[0]][];
		for (int k = 0; k < this.size[0]; k++) {
			this.pathing[k] = ParseUtils.readUInt8Array(stream, this.size[1]);
		}

		return true;
	}

	public void save(final LittleEndianDataOutputStream stream, final War3MapW3i mapInformation) throws IOException {
		ParseUtils.writeWar3ID(stream, MAGIC_NUMBER);
		stream.writeInt(this.version);
		ParseUtils.writeInt32Array(stream, this.size);
		for (int k = 0; k < this.size[0]; k++) {
			ParseUtils.writeUInt8Array(stream, this.pathing[k]);
		}
	}

	public int getVersion() {
		return this.version;
	}

	public int[] getSize() {
		return this.size;
	}

	public short[][] getPathing() {
		return this.pathing;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public void setPathing(final short[][] pathing) {
		this.pathing = pathing;
	}

	public static War3MapWpm generateConverted(final WdtMap map, final War3MapW3e environFile) {
		final War3MapWpm pathing;
		try {
			pathing = new War3MapWpm(null);
			pathing.version = 0;
			pathing.size[0] = environFile.getMapSize()[0] * 4;
			pathing.size[1] = environFile.getMapSize()[1] * 4;
			pathing.pathing = new short[pathing.size[0]][];
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		return pathing;
	}

}

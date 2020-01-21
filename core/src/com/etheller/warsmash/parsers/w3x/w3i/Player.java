package com.etheller.warsmash.parsers.w3x.w3i;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * A player.
 */
public class Player {
	private War3ID id;
	private int type;
	private int race;
	private int isFixedStartPosition;
	private String name;
	private final float[] startLocation = new float[2];
	private long allyLowPriorities;
	private long allyHighPriorities;

	public void load(final LittleEndianDataInputStream stream) throws IOException {
		this.id = ParseUtils.readWar3ID(stream);
		this.type = stream.readInt();
		this.race = stream.readInt();
		this.isFixedStartPosition = stream.readInt();
		this.name = ParseUtils.readUntilNull(stream);
		ParseUtils.readFloatArray(stream, this.startLocation);
		this.allyLowPriorities = ParseUtils.readUInt32(stream);
		this.allyHighPriorities = ParseUtils.readUInt32(stream);
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeWar3ID(stream, this.id);
		stream.writeInt(this.type);
		stream.writeInt(this.race);
		stream.writeInt(this.isFixedStartPosition);
		ParseUtils.writeWithNullTerminator(stream, this.name);
		ParseUtils.writeFloatArray(stream, this.startLocation);
		ParseUtils.writeUInt32(stream, this.allyLowPriorities);
		ParseUtils.writeUInt32(stream, this.allyHighPriorities);
	}

	public int getByteLength() {
		return 33 + this.name.length();
	}
}

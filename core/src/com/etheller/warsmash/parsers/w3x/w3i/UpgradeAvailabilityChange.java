package com.etheller.warsmash.parsers.w3x.w3i;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class UpgradeAvailabilityChange {
	private long playerFlags;
	private War3ID id;
	private int levelAffected;
	private int availability;

	public void load(final LittleEndianDataInputStream stream) throws IOException {
		this.playerFlags = ParseUtils.readUInt32(stream);
		this.id = ParseUtils.readWar3ID(stream);
		this.levelAffected = stream.readInt();
		this.availability = stream.readInt();
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, this.playerFlags);
		ParseUtils.writeWar3ID(stream, this.id);
		stream.writeInt(this.levelAffected);
		stream.writeInt(this.availability);
	}

}

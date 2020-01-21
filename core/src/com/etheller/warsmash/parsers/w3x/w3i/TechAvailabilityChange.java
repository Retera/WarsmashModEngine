package com.etheller.warsmash.parsers.w3x.w3i;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class TechAvailabilityChange {
	private long playerFlags;
	private War3ID id;

	public void load(final LittleEndianDataInputStream stream) throws IOException {
		this.playerFlags = ParseUtils.readUInt32(stream);
		this.id = ParseUtils.readWar3ID(stream);
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, this.playerFlags);
		ParseUtils.writeWar3ID(stream, this.id);
	}

}

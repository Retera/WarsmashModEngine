package com.etheller.warsmash.parsers.w3x.unitsdoo;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class RandomUnit {
	private War3ID id;
	private int chance;

	public void load(final LittleEndianDataInputStream stream) throws IOException {
		this.id = ParseUtils.readWar3ID(stream);
		this.chance = stream.readInt();
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeWar3ID(stream, this.id);
		stream.writeInt(this.chance);
	}
}

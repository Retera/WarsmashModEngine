package com.etheller.warsmash.parsers.w3x.w3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class RandomUnit {
	private int chance;
	private final List<War3ID> ids = new ArrayList<>();

	public void load(final LittleEndianDataInputStream stream, final int positions) throws IOException {
		this.chance = stream.readInt();

		for (int i = 0; i < positions; i++) {
			this.ids.add(ParseUtils.readWar3ID(stream));
		}
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeInt(this.chance);

		for (final War3ID id : this.ids) {
			ParseUtils.writeWar3ID(stream, id);
		}
	}
}

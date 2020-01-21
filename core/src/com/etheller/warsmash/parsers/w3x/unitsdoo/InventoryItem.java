package com.etheller.warsmash.parsers.w3x.unitsdoo;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * An inventory item.
 */
public class InventoryItem {
	private int slot;
	private War3ID id;

	public void load(final LittleEndianDataInputStream stream) throws IOException {
		this.slot = stream.readInt();
		this.id = ParseUtils.readWar3ID(stream);
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeInt(this.slot);
		ParseUtils.writeWar3ID(stream, this.id);
	}
}

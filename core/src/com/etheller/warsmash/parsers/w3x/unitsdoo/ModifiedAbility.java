package com.etheller.warsmash.parsers.w3x.unitsdoo;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class ModifiedAbility {
	private War3ID id;
	private int activeForAutocast = 0;
	private int heroLevel = 1;

	public void load(final LittleEndianDataInputStream stream) throws IOException {
		this.id = ParseUtils.readWar3ID(stream);
		this.activeForAutocast = stream.readInt();
		this.heroLevel = stream.readInt();
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeWar3ID(stream, this.id);
		stream.writeInt(this.activeForAutocast);
		stream.writeInt(this.heroLevel);
	}
}

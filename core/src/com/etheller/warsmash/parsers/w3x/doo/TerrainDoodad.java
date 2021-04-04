package com.etheller.warsmash.parsers.w3x.doo;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * A terrain doodad.
 *
 * This type of doodad works much like cliffs. It uses the height of the
 * terrain, and gets affected by the ground heightmap. It cannot be manipulated
 * in any way in the World Editor once placed. Indeed, the only way to change it
 * is to remove it by changing cliffs around it.
 */
public class TerrainDoodad {
	private War3ID id;
	private long u1;
	private final long[] location = new long[2];

	public void load(final LittleEndianDataInputStream stream, final int version) throws IOException {
		this.id = ParseUtils.readWar3ID(stream);
		this.u1 = ParseUtils.readUInt32(stream);
		ParseUtils.readUInt32Array(stream, this.location);
	}

	public void save(final LittleEndianDataOutputStream stream, final int version) throws IOException {
		ParseUtils.writeWar3ID(stream, this.id);
		ParseUtils.writeUInt32(stream, this.u1);
		ParseUtils.writeUInt32Array(stream, this.location);
	}

	public War3ID getId() {
		return this.id;
	}

	public long getU1() {
		return this.u1;
	}

	public long[] getLocation() {
		return this.location;
	}

	public void setId(final War3ID id) {
		this.id = id;
	}

	public void setU1(final long u1) {
		this.u1 = u1;
	}
}

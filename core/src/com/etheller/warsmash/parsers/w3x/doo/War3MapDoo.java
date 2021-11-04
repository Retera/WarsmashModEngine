package com.etheller.warsmash.parsers.w3x.doo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * war3map.doo - the doodad and destructible file.
 */
public class War3MapDoo {
	private static final War3ID MAGIC_NUMBER = War3ID.fromString("W3do");
	private int version = 0;
	private final short[] u1 = new short[4];
	private final List<Doodad> doodads = new ArrayList<>();
	private final short[] u2 = new short[4];
	private final List<TerrainDoodad> terrainDoodads = new ArrayList<>();

	public War3MapDoo(final LittleEndianDataInputStream stream, final War3MapW3i mapInformation) throws IOException {
		if (stream != null) {
			this.load(stream, mapInformation);
		}
	}

	private boolean load(final LittleEndianDataInputStream stream, final War3MapW3i mapInformation) throws IOException {
		final War3ID firstId = ParseUtils.readWar3ID(stream);
		if (!MAGIC_NUMBER.equals(firstId)) {
			return false;
		}

		this.version = stream.readInt();
		ParseUtils.readUInt8Array(stream, this.u1);

		for (int i = 0, l = stream.readInt(); i < l; i++) {
			final Doodad doodad = new Doodad();

			doodad.load(stream, this.version, mapInformation);

			this.doodads.add(doodad);
		}

		ParseUtils.readUInt8Array(stream, this.u2);

		for (int i = 0, l = stream.readInt(); i < l; i++) {
			final TerrainDoodad terrainDoodad = new TerrainDoodad();

			terrainDoodad.load(stream, this.version);

			this.terrainDoodads.add(terrainDoodad);
		}

		return true;
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {

		ParseUtils.writeWar3ID(stream, MAGIC_NUMBER);
		stream.writeInt(this.version);
		ParseUtils.writeUInt8Array(stream, this.u1);
		ParseUtils.writeUInt32(stream, this.doodads.size());

		for (final Doodad doodad : this.doodads) {
			doodad.save(stream, this.version);
		}

		ParseUtils.writeUInt8Array(stream, this.u2);
		ParseUtils.writeUInt32(stream, this.terrainDoodads.size());

		for (final TerrainDoodad terrainDoodad : this.terrainDoodads) {
			terrainDoodad.save(stream, this.version);
		}
	}

	public int getByteLength() {
		int size = 24 + (this.terrainDoodads.size() * 16);

		for (final Doodad doodad : this.doodads) {
			size += doodad.getByteLength(this.version);
		}

		return size;
	}

	public int getVersion() {
		return this.version;
	}

	public short[] getU1() {
		return this.u1;
	}

	public List<Doodad> getDoodads() {
		return this.doodads;
	}

	public short[] getU2() {
		return this.u2;
	}

	public List<TerrainDoodad> getTerrainDoodads() {
		return this.terrainDoodads;
	}

}

package com.etheller.warsmash.parsers.w3x.unitsdoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class War3MapUnitsDoo {
	private static final War3ID MAGIC_NUMBER = War3ID.fromString("W3do");
	private int version = 8;
	private long unknown = 11;
	private final List<Unit> units = new ArrayList<>();

	public War3MapUnitsDoo(final LittleEndianDataInputStream stream, final War3MapW3i mapInformation)
			throws IOException {
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
		this.unknown = ParseUtils.readUInt32(stream);

		for (int i = 0, l = stream.readInt(); i < l; i++) {
			final Unit unit = new Unit();

			unit.load(stream, this.version, mapInformation);

			this.units.add(unit);
		}

		return true;
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeWar3ID(stream, MAGIC_NUMBER);
		stream.writeInt(this.version);
		ParseUtils.writeUInt32(stream, this.unknown);
		stream.writeInt(this.units.size());

		for (final Unit unit : this.units) {
			unit.save(stream, this.version);
		}
	}

	public int getByteLength() {
		int size = 16;

		for (final Unit unit : this.units) {
			size += unit.getByteLength(this.version);
		}

		return size;
	}

	public List<Unit> getUnits() {
		return this.units;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}
}

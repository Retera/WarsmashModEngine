package com.etheller.warsmash.parsers.w3x.w3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class RandomUnitTable {
	private int id;
	private String name;
	private int positions;
	private int[] columnTypes;
	private final List<RandomUnit> units = new ArrayList<>();

	public void load(final LittleEndianDataInputStream stream) throws IOException {
		this.id = stream.readInt(); // TODO is this a War3ID?
		this.name = ParseUtils.readUntilNull(stream);
		this.positions = stream.readInt();
		this.columnTypes = ParseUtils.readInt32Array(stream, this.positions);

		for (long i = 0, l = ParseUtils.readUInt32(stream); i < l; i++) {
			final RandomUnit unit = new RandomUnit();

			unit.load(stream, this.positions);

			this.units.add(unit);
		}
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeInt(this.id);
		ParseUtils.writeWithNullTerminator(stream, this.name);
		stream.writeInt(this.positions);
		ParseUtils.writeInt32Array(stream, this.columnTypes);
		ParseUtils.writeUInt32(stream, this.units.size());

		for (final RandomUnit unit : this.units) {
			unit.save(stream);
		}
	}

	public int getByteLength() {
		return 13 + this.name.length() + (this.columnTypes.length * 4)
				+ (this.units.size() * (4 + (4 * this.positions)));
	}
}

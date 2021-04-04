package com.etheller.warsmash.parsers.w3x.w3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class RandomItemTable {
	private War3ID id;
	private String name;
	private final List<RandomItemSet> sets = new ArrayList<>();

	public void load(final LittleEndianDataInputStream stream) throws IOException {
		this.id = ParseUtils.readWar3ID(stream);
		this.name = ParseUtils.readUntilNull(stream);

		for (long i = 0, l = ParseUtils.readUInt32(stream); i < l; i++) {
			final RandomItemSet set = new RandomItemSet();

			set.load(stream);

			this.sets.add(set);
		}
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeWar3ID(stream, this.id);
		ParseUtils.writeWithNullTerminator(stream, this.name);
		ParseUtils.writeUInt32(stream, this.sets.size());

		for (final RandomItemSet set : this.sets) {
			set.save(stream);
		}
	}

	public int getByteLength() {
		int size = 9 + this.name.length();

		for (final RandomItemSet set : this.sets) {
			size += set.getByteLength();
		}

		return size;
	}

	public War3ID getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public List<RandomItemSet> getSets() {
		return this.sets;
	}

	public void setId(final War3ID id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}
}

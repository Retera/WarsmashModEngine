package com.etheller.warsmash.parsers.w3x.w3r;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class War3MapW3r {
	private int version;
	private final List<Region> regions = new ArrayList<>();

	public War3MapW3r(final LittleEndianDataInputStream stream) throws IOException {
		if (stream != null) {
			this.load(stream);
		}
	}

	private boolean load(final LittleEndianDataInputStream stream) throws IOException {
		this.version = stream.readInt();

		for (int i = 0, l = stream.readInt(); i < l; i++) {
			final Region region = new Region();

			region.load(stream, this.version);

			this.regions.add(region);
		}

		return true;
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeInt(this.version);
		stream.writeInt(this.regions.size());
		for (final Region unit : this.regions) {
			unit.save(stream, this.version);
		}
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public List<Region> getRegions() {
		return this.regions;
	}

}

package com.etheller.warsmash.parsers.wmo;

import java.util.Arrays;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class WmoPlane implements MdlxBlock, MdlxChunk {

	private final float[] normal = new float[3];
	private float distance;

	@Override
	public long getByteLength(final int version) {
		return 16;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		reader.readFloat32Array(this.normal);
		this.distance = reader.readFloat32();
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		throw new UnsupportedOperationException();
	}

	public float getDistance() {
		return this.distance;
	}

	public float[] getNormal() {
		return this.normal;
	}

	@Override
	public String toString() {
		return "WmoPlane [normal=" + Arrays.toString(this.normal) + ", distance=" + this.distance + "]";
	}
}

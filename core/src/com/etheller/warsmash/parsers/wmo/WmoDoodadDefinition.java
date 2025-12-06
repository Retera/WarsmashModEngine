package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class WmoDoodadDefinition implements MdlxBlock, MdlxChunk {
	private long nameIndex;
	private long flags;
	private final float[] position = new float[3];
	private final float[] orientation = new float[4];
	private float scale;
	private final short[] color = new short[4]; // bgra

	@Override
	public long getByteLength(final int version) {
		return 40;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final long data = reader.readUInt32();
		this.nameIndex = data & 0xFFFFFF;
		this.flags = data >> 24;
		reader.readFloat32Array(this.position);
		reader.readFloat32Array(this.orientation);
		this.scale = reader.readFloat32();
		reader.readUInt8Array(this.color);
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

	public long getNameIndex() {
		return this.nameIndex;
	}

	public long getFlags() {
		return this.flags;
	}

	public float[] getPosition() {
		return this.position;
	}

	public float[] getOrientation() {
		return this.orientation;
	}

	public float getScale() {
		return this.scale;
	}

	public short[] getColor() {
		return this.color;
	}

	public static class Flags {
		public static final int None = 0;
		public static final int AcceptProjTex = 0x1;
		public static final int Unknown_0x2 = 0x2;
		public static final int Unknown_0x4 = 0x4;
		public static final int Unknown_0x8 = 0x8;

	}
}

package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class WmoGroupInfo implements MdlxBlock, MdlxChunk {
	private long offset;
	private long size;
	private int flags;
	public float[] min = new float[3];
	public float[] max = new float[3];
	private int nameIndex;

	public WmoGroupInfo() {
	}

	@Override
	public long getByteLength(final int version) {
		return version == 14 ? 40 : 32;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		if (version == 14) {
			this.offset = reader.readUInt32();
			this.size = reader.readUInt32();
		}
		this.flags = reader.readInt32();
		reader.readFloat32Array(this.min);
		reader.readFloat32Array(this.max);
		this.nameIndex = reader.readInt32();
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

	public long getOffset() {
		return this.offset;
	}

	public long getSize() {
		return this.size;
	}

	public int getFlags() {
		return this.flags;
	}

	public float[] getMin() {
		return this.min;
	}

	public float[] getMax() {
		return this.max;
	}

	public int getNameIndex() {
		return this.nameIndex;
	}

	public static class Flags {
		public static final int None = 0;
		public static final int HasBSP = 0x1;
		public static final int HasLightmap = 0x2;
		public static final int HasVertexColors = 0x4;
		public static final int IsExterior = 0x8;
		public static final int Unknown_0x10 = 0x10;
		public static final int Unknown_0x20 = 0x20;
		public static final int IsExteriorLit = 0x40;
		public static final int Unreachable = 0x80;
		public static final int Unknown_0x100 = 0x100;
		public static final int HasLights = 0x200;
		public static final int HasMPBX = 0x400;
		public static final int HasDoodads = 0x800;
		public static final int HasLiquids = 0x1000;
		public static final int IsInterior = 0x2000;
		public static final int Unknown_0x4000 = 0x4000;
		public static final int Unknown_0x8000 = 0x8000;
		public static final int AlwaysDraw = 0x10000;
		public static final int HasTriangleStrips = 0x20000;
		public static final int ShowSkybox = 0x40000;
		public static final int IsOceanicWater = 0x80000;
		public static final int Unknown_0x100000 = 0x100000;
		public static final int IsMountAllowed = 0x200000;
		public static final int Unknown_0x400000 = 0x400000;
		public static final int Unknown_0x800000 = 0x800000;
		public static final int HasTwoVertexShadingSets = 0x1000000;
		public static final int HasTwoTextureCoordinateSets = 0x2000000;
		public static final int IsAntiportal = 0x4000000;
		public static final int Unknown_0x8000000 = 0x8000000;
		public static final int Unknown_0x10000000 = 0x10000000;
		public static final int ExteriorCull = 0x20000000;
		public static final int HasThreeTextureCoordinateSets = 0x40000000;
		public static final int Unknown_0x80000000 = 0x80000000;

	}
}

package com.etheller.warsmash.parsers.wmo;

import com.etheller.warsmash.util.FlagUtils;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class GroupPolygon implements MdlxBlock, MdlxChunk {

	private byte flags;
	private byte lightmapTex;
	private byte materialId;

	@Override
	public long getByteLength(final int version) {
		return version != 17 ? 4 : 2;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.flags = reader.readInt8();

		if (version != 17) {
			this.lightmapTex = reader.readInt8();
		}

		this.materialId = reader.readInt8();

		if (version != 17) {
			reader.readInt8(); // skip
		}
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

	public byte getFlags() {
		return this.flags;
	}

	public byte getLightmapTex() {
		return this.lightmapTex;
	}

	public byte getMaterialId() {
		return this.materialId;
	}

	/**
	 * @return true if we blend lighting from exterior to interior
	 */
	public boolean isTransitionFace() {
		return FlagUtils.hasFlag(this.flags, Flags.Unknown_0x1)
				&& (FlagUtils.hasFlag(this.flags, Flags.Detail) || FlagUtils.hasFlag(this.flags, Flags.Render));
	}

	public boolean isColor() {
		return !FlagUtils.hasFlag(this.flags, Flags.HasCollision);
	}

	public boolean isRenderFace() {
		return FlagUtils.hasFlag(this.flags, Flags.Render) && !FlagUtils.hasFlag(this.flags, Flags.Detail);
	}

	public boolean isCollidable() {
		return FlagUtils.hasFlag(this.flags, Flags.HasCollision) || isRenderFace();
	}

	public static class Flags {
		public static final int None = 0;
		public static final int Unknown_0x1 = 0x1;
		public static final int NoCameraCollide = 0x2;
		public static final int Detail = 0x4;
		public static final int HasCollision = 0x8;
		public static final int Hint = 0x10;
		public static final int Render = 0x20;
		public static final int Unknown_0x40 = 0x40;
		public static final int CollideHit = 0x80;
	}
}

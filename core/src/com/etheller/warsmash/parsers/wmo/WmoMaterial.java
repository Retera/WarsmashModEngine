package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class WmoMaterial implements MdlxBlock, MdlxChunk {
	private int version;
	private int flags;
	private FilterMode filterMode;
	private long diffuseNameIndex;
	private final short[] sidnColor = new short[4]; // bgra
	private final short[] frameSidnColor = new short[4]; // bgra
	private long envNameIndex;
	private final short[] diffColor = new short[4]; // bgra
	private long groundType;

	public WmoMaterial() {
	}

	@Override
	public long getByteLength(final int version) {
		return version == 14 ? 44 : 64;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		if (version == 14) {
			this.version = reader.readInt32();
		}
		this.flags = reader.readInt32();
		this.filterMode = FilterMode.fromId(reader.readInt32());
		this.diffuseNameIndex = reader.readUInt32();
		reader.readUInt8Array(this.sidnColor);
		reader.readUInt8Array(this.frameSidnColor);
		this.envNameIndex = reader.readUInt32();
		reader.readUInt8Array(this.diffColor);
		this.groundType = reader.readUInt32();

		if (version == 14) {
			// Skip some 0.5.3 alpha things!!
			reader.position(reader.position() + 8);
		}
		else {
			throw new IllegalStateException("Bad version: " + version);
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

	public int getVersion() {
		return this.version;
	}

	public int getFlags() {
		return this.flags;
	}

	public FilterMode getFilterMode() {
		return this.filterMode;
	}

	public long getDiffuseNameIndex() {
		return this.diffuseNameIndex;
	}

	public short[] getSidnColor() {
		return this.sidnColor;
	}

	public short[] getFrameSidnColor() {
		return this.frameSidnColor;
	}

	public long getEnvNameIndex() {
		return this.envNameIndex;
	}

	public short[] getDiffColor() {
		return this.diffColor;
	}

	public long getGroundType() {
		return this.groundType;
	}

	public enum FilterMode {
		OPAQUE("Opaque"),
		ALPHA_KEY("AlphaKey"),
		ALPHA("Alpha"),
		ADD("Add"),
		MOD("Mod"),
		MOD2X("Mod2x"),
		MOD_ADD("ModAdd"),
		INV_SRC_ALPHA_ADD("InvSrcAlphaAdd"),
		INV_SRC_ALPHA_OPAQUE("InvSrcAlphaOpaque"),
		SRC_ALPHA_OPAQUE("SrcAlphaOpaque"),
		NO_ALPHA_ADD("NoAlphaAdd"),
		CONSTANT_ALPHA("ConstantAlpha"),
		SCREEN("Screen"),
		BLEND_ADD("BlendAdd");

		String token;

		FilterMode(final String token) {
			this.token = token;
		}

		public static FilterMode fromId(final int id) {
			return values()[id];
		}

		public static int nameToId(final String name) {
			for (final FilterMode mode : values()) {
				if (mode.token.equals(name)) {
					return mode.ordinal();
				}
			}
			return -1;
		}

		public static FilterMode nameToFilter(final String name) {
			for (final FilterMode mode : values()) {
				if (mode.token.equals(name)) {
					return mode;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return this.token;
		}
	}

	public static class Flags {
		public static final int None = 0;
		public static final int Unlit = 0x1;
		public static final int Unfogged = 0x2;
		public static final int Unculled = 0x4;
		public static final int ExteriorLit = 0x8;
		public static final int SelfIlluminatedDayNight = 0x10;
		public static final int Window = 0x20;
		public static final int ClampSAddress = 0x40;
		public static final int ClampTAddress = 0x80;
		public static final int Unknown_0x100 = 0x100;

	}
}

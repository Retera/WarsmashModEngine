package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class WmoLight implements MdlxBlock, MdlxChunk {
	public Type type = Type.OMNIDIRECTIONAL;
	private byte useAtten;
	private short unknown;
	private final short[] color = new short[4]; // bgra
	private final float[] position = new float[3];
	private float intensity;
	private float attenStart;
	private float attenEnd;

	@Override
	public long getByteLength(final int version) {
		return version == 14 ? 0x20 : 48;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.type = Type.fromId(reader.readInt8());
		this.useAtten = reader.readInt8();
		this.unknown = reader.readInt16();
		reader.readUInt8Array(this.color);
		reader.readFloat32Array(this.position);
		this.intensity = reader.readFloat32();
		if (version != 14) {
			// ?? skip whatever this is
			for (int i = 0; i < 4; i++) {
				reader.readFloat32();
			}
		}
		this.attenStart = reader.readFloat32();
		this.attenEnd = reader.readFloat32();
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

	public Type getType() {
		return this.type;
	}

	public byte getUseAtten() {
		return this.useAtten;
	}

	public short getUnknown() {
		return this.unknown;
	}

	public short[] getColor() {
		return this.color;
	}

	public float[] getPosition() {
		return this.position;
	}

	public float getIntensity() {
		return this.intensity;
	}

	public float getAttenStart() {
		return this.attenStart;
	}

	public float getAttenEnd() {
		return this.attenEnd;
	}

	public enum Type {
		OMNIDIRECTIONAL("Omnidirectional"),
		SPOT("Spot"),
		DIRECTIONAL("Directional"),
		AMBIENT("Ambient");

		String token;

		Type(final String token) {
			this.token = token;
		}

		public static Type fromId(final int id) {
			return values()[id];
		}

		@Override
		public String toString() {
			return this.token;
		}
	}

}

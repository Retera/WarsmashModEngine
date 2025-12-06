package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class WmoFog implements MdlxBlock, MdlxChunk {
	private int flags;
	private final float[] position = new float[3];
	private float start;
	private float end;
	private final FogInfo[] fogs = new FogInfo[] { new FogInfo(), new FogInfo() };

	@Override
	public long getByteLength(final int version) {
		return 48;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.flags = reader.readInt32();
		reader.readFloat32Array(this.position);
		this.start = reader.readFloat32();
		this.end = reader.readFloat32();
		for (int i = 0; i < this.fogs.length; i++) {
			this.fogs[i].readMdx(reader, version);
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

	public int getFlags() {
		return this.flags;
	}

	public float[] getPosition() {
		return this.position;
	}

	public float getStart() {
		return this.start;
	}

	public float getEnd() {
		return this.end;
	}

	public FogInfo[] getFogs() {
		return this.fogs;
	}

	public static final class FogInfo {
		private float end;
		private float startScalar;
		private final short[] color = new short[4]; // bgra

		protected void readMdx(final BinaryReader reader, final int version) {
			this.end = reader.readFloat32();
			this.startScalar = reader.readFloat32();
			reader.readUInt8Array(this.color);
		}
	}
}

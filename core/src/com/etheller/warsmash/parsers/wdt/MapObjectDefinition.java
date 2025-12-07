package com.etheller.warsmash.parsers.wdt;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MapObjectDefinition implements MdlxBlock, MdlxChunk {
	private long nameId;
	private int uniqueId;
	private final float[] position = new float[3];
	private final float[] rotation = new float[3];
	private final float[] minExt = new float[3];
	private final float[] maxExt = new float[3];
	private int flags;
	private int doodadSet;
	private int nameSet;
	private float scale;
	private int scaleShort;

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.nameId = reader.readUInt32();
		this.uniqueId = reader.readInt32();
		reader.readFloat32Array(this.position);
		reader.readFloat32Array(this.rotation);
		reader.readFloat32Array(this.minExt);
		reader.readFloat32Array(this.maxExt);
		this.flags = reader.readUInt16();
		this.doodadSet = reader.readUInt16();
		this.nameSet = reader.readUInt16();
		this.scaleShort = reader.readUInt16();
		this.scale = this.scaleShort / 1024f;
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		throw new RuntimeException();
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		throw new RuntimeException();
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		throw new RuntimeException();
	}

	@Override
	public long getByteLength(final int version) {
		return 4 + 4 + 12 + 12 + 12 + 12 + 2 + 2 + 2 + 2;
	}

	public long getNameId() {
		return this.nameId;
	}

	public int getUniqueId() {
		return this.uniqueId;
	}

	public float[] getPosition() {
		return this.position;
	}

	public float[] getRotation() {
		return this.rotation;
	}

	public float[] getMinExt() {
		return this.minExt;
	}

	public float[] getMaxExt() {
		return this.maxExt;
	}

	public int getFlags() {
		return this.flags;
	}

	public int getDoodadSet() {
		return this.doodadSet;
	}

	public int getNameSet() {
		return this.nameSet;
	}

	public float getScale() {
		return this.scale;
	}

	public int getScaleShort() {
		return this.scaleShort;
	}
}

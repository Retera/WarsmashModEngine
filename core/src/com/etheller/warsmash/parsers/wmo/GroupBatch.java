package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class GroupBatch implements MdlxBlock, MdlxChunk {

	private byte lightMap;
	private byte texture;
	private final short[][] boundingBox = new short[2][3];
	private long startIndex;
	private int count;
	private int minIndex;
	private int maxIndex;
	private byte flags;
	private byte materialId;

	@Override
	public long getByteLength(final int version) {
		return version == 16 ? 32 : 24;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		if (version == 14) {
			this.lightMap = reader.readInt8();
			this.texture = reader.readInt8();
		}

		reader.readInt16Array(this.boundingBox[0]);
		reader.readInt16Array(this.boundingBox[1]);
		this.startIndex = (version == 14) ? reader.readUInt16() : reader.readUInt32();
		this.count = reader.readUInt16();
		this.minIndex = reader.readUInt16();
		this.maxIndex = reader.readUInt16();
		this.flags = reader.readInt8();

		this.materialId = reader.readInt8();
		if (version == 14) {
			this.flags |= this.materialId;
			this.materialId = -1; // ?
		}
		if (version == 16) {
			throw new IllegalStateException();
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

	public byte getLightMap() {
		return this.lightMap;
	}

	public byte getTexture() {
		return this.texture;
	}

	public short[][] getBoundingBox() {
		return this.boundingBox;
	}

	public long getStartIndex() {
		return this.startIndex;
	}

	public int getCount() {
		return this.count;
	}

	public int getMinIndex() {
		return this.minIndex;
	}

	public int getMaxIndex() {
		return this.maxIndex;
	}

	public byte getFlags() {
		return this.flags;
	}

	public byte getMaterialId() {
		return this.materialId;
	}
}

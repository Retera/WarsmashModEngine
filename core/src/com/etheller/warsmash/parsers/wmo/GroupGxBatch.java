package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class GroupGxBatch implements MdlxBlock, MdlxChunk {
	private int vertStart;
	private int vertCount;
	private int batchStart;
	private int batchCount;

	@Override
	public long getByteLength(final int version) {
		return 8;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.vertStart = reader.readUInt16();
		this.vertCount = reader.readUInt16();
		this.batchStart = reader.readUInt16();
		this.batchCount = reader.readUInt16();
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

	public int getVertStart() {
		return this.vertStart;
	}

	public int getVertCount() {
		return this.vertCount;
	}

	public int getBatchStart() {
		return this.batchStart;
	}

	public int getBatchCount() {
		return this.batchCount;
	}
}

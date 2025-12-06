package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class WmoDoodadSet implements MdlxBlock, MdlxChunk {

	private String name;
	private long startIndex;
	private long count;

	@Override
	public long getByteLength(final int version) {
		return 32;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.name = reader.readBytes(20).trim();
		this.startIndex = reader.readUInt32();
		this.count = reader.readUInt32();
		reader.readUInt32(); // skip 4 bytes
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

	public String getName() {
		return this.name;
	}

	public long getStartIndex() {
		return this.startIndex;
	}

	public long getCount() {
		return this.count;
	}

}

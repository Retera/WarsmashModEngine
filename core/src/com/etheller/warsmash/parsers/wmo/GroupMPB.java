package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class GroupMPB implements MdlxBlock, MdlxChunk {
	private int startIndex;
	private int count;

	@Override
	public long getByteLength(final int version) {
		return 4;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.startIndex = reader.readUInt16();
		this.count = reader.readUInt16();
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

	public int getStartIndex() {
		return this.startIndex;
	}

	public int getCount() {
		return this.count;
	}
}

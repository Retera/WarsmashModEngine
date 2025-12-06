package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class GroupRect implements MdlxBlock, MdlxChunk {

	private byte x;
	private byte y;
	private byte width;
	private byte height;

	@Override
	public long getByteLength(final int version) {
		return 4;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.x = reader.readInt8();
		this.y = reader.readInt8();
		this.width = reader.readInt8();
		this.height = reader.readInt8();
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

	public byte getX() {
		return this.x;
	}

	public byte getY() {
		return this.y;
	}

	public byte getWidth() {
		return this.width;
	}

	public byte getHeight() {
		return this.height;
	}
}

package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class WmoPortalReference implements MdlxBlock, MdlxChunk {

	private int portalIndex;
	private int groupIndex;
	private short side;

	@Override
	public long getByteLength(final int version) {
		return 8;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.portalIndex = reader.readUInt16();
		this.groupIndex = reader.readUInt16();
		this.side = reader.readInt16();

		reader.readUInt16(); // skipping this part
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

	public int getPortalIndex() {
		return this.portalIndex;
	}

	public int getGroupIndex() {
		return this.groupIndex;
	}

	public short getSide() {
		return this.side;
	}

}

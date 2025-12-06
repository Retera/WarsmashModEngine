package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class WmoPortal implements MdlxBlock, MdlxChunk {

	private int startVertex;
	private int count;
	private WmoPlane plane;

	@Override
	public long getByteLength(final int version) {
		return 20;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.startVertex = reader.readUInt16();
		this.count = reader.readUInt16();
		this.plane = new WmoPlane();
		this.plane.readMdx(reader, version);
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

	public int getStartVertex() {
		return this.startVertex;
	}

	public int getCount() {
		return this.count;
	}

	public WmoPlane getPlane() {
		return this.plane;
	}

}

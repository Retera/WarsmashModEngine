package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class GroupBSPNode implements MdlxBlock, MdlxChunk {

	private short flags;
	private int negChild;
	private int posChild;
	private int numFaces;
	private long faceStart;
	private float planeDist;

	@Override
	public long getByteLength(final int version) {
		return 16;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.flags = reader.readInt16();
		this.negChild = reader.readUInt16();
		this.posChild = reader.readUInt16();
		this.numFaces = reader.readUInt16();
		this.faceStart = reader.readUInt32();
		this.planeDist = reader.readFloat32();
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

	public short getFlags() {
		return this.flags;
	}

	public int getNegChild() {
		return this.negChild;
	}

	public int getPosChild() {
		return this.posChild;
	}

	public int getNumFaces() {
		return this.numFaces;
	}

	public long getFaceStart() {
		return this.faceStart;
	}

	public float getPlaneDist() {
		return this.planeDist;
	}

	public static final class Flags {
		public static final int NoChild = -1;
		public static final int XAxis = 0x0;
		public static final int YAxis = 0x1;
		public static final int ZAxis = 0x2;
		public static final int Leaf = 0x4;
	}
}

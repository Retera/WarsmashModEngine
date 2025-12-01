package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxCollisionGeometry implements MdlxBlock, MdlxChunk {
	public float[] vertices;
	public float[] normals;
	public int[] faces; // unsigned short[]

	public MdlxCollisionGeometry() {
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final int thisIsVRTX = reader.readInt32(); // skip VRTX
		final int numVertices = reader.readInt32();
		this.vertices = reader.readFloat32Array(numVertices * 3);

		final int thisIsTRIspace = reader.readInt32(); // skip 'TRI '
		this.faces = reader.readUInt16Array(reader.readInt32());

		final int thisIsNRMS = reader.readInt32(); // skip NRMS
		this.normals = reader.readFloat32Array(reader.readInt32() * 3);
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

	@Override
	public long getByteLength(final int version) {
		final long size = 4 + 4 + (this.vertices.length * 4) + 4 + 4 + (this.normals.length * 4) + 4 + 4
				+ (this.faces.length * 2);
		return size;
	}

	public float[] getVertices() {
		return this.vertices;
	}

	public int[] getFaces() {
		return this.faces;
	}

	public float[] getNormals() {
		return this.normals;
	}
}

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
	private float[] position = new float[3];
	private float[] rotation = new float[3];
	private float[] minExt = new float[3];
	private float[] maxExt = new float[3];
	private int flags;
	private int doodadSet;
	private int nameSet;
	private int scale;

	@Override
	public void readMdx(BinaryReader reader, int version) {
		nameId = reader.readUInt32();
		uniqueId = reader.readInt32();
		reader.readFloat32Array(position);
		reader.readFloat32Array(rotation);
		reader.readFloat32Array(minExt);
		reader.readFloat32Array(maxExt);
		flags = reader.readUInt16();
		doodadSet = reader.readUInt16();
		nameSet = reader.readUInt16();
		scale = reader.readUInt16();
	}

	@Override
	public void writeMdx(BinaryWriter writer, int version) {
		throw new RuntimeException();
	}

	@Override
	public void readMdl(MdlTokenInputStream stream, int version) {
		throw new RuntimeException();
	}

	@Override
	public void writeMdl(MdlTokenOutputStream stream, int version) {
		throw new RuntimeException();
	}

	@Override
	public long getByteLength(int version) {
		return 4 + 4 + 12 + 12 + 12 + 12 + 2 + 2 + 2 + 2;
	}

}

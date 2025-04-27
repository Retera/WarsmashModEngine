package com.etheller.warsmash.parsers.wdt;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MapChunkLiquidLayer<T> implements MdlxBlock {

	public float minHeight;
	public float maxHeight;
	public T[][] verts;
	public byte[][] tiles;
	public VertReader<T> vertReader;
	public long nFlowvs;
	public SWFlowv[] flowvs;

	public MapChunkLiquidLayer(VertReader<T> vertReader) {
		this.vertReader = vertReader;
	}

	@Override
	public void readMdx(BinaryReader reader, int version) {
		minHeight = reader.readFloat32();
		maxHeight = reader.readFloat32();

		verts = (T[][]) new Object[9][9];
		for(int i= 0;i  < verts.length; i++) {
			for(int j = 0; j < verts[i].length; j++) {
				verts[i][j] = vertReader.parse(reader);
			}
		}
		tiles = new byte[8][8];
		for(byte[] row: tiles) {
			reader.readInt8Array(row);
		}
		nFlowvs = reader.readUInt32();
		flowvs = new SWFlowv[]{new SWFlowv(reader), new SWFlowv(reader)};
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

	public static interface VertReader<T> {
		T parse(BinaryReader reader);
	}

	public static final class SWVert {
		public byte depth;
		public byte flow0Pct;
		public byte flow1Pct;
		public byte filler;
		public float height;

		public SWVert(BinaryReader reader) {
			depth = reader.readInt8();
			flow0Pct = reader.readInt8();
			flow1Pct = reader.readInt8();
			filler = reader.readInt8();
			height = reader.readFloat32();
		}
	}

	public static final class SOVert {
		public byte depth;
		public byte foam;
		public byte wet;
		public byte filler;
		public float height;

		public SOVert(BinaryReader reader) {
			depth = reader.readInt8();
			foam = reader.readInt8();
			wet = reader.readInt8();
			filler = reader.readInt8();
			height = reader.readFloat32();
		}
	}

	public static final class SMVert {
		public byte s;
		public byte t;
		public float height;

		public SMVert(BinaryReader reader) {
			s = reader.readInt8();
			t = reader.readInt8();
			height = reader.readFloat32();
		}
	}

	public static final class SWFlowv {
		public float[] center = new float[3];
		public float radius;
		public float[] dir = new float[3];
		public float velocity;
		public float amplitude;
		public float frequency;

		public SWFlowv(BinaryReader reader) {
			reader.readFloat32Array(center);
			radius = reader.readFloat32();
			reader.readFloat32Array(dir);
			velocity = reader.readFloat32();
			amplitude = reader.readFloat32();
			frequency = reader.readFloat32();
		}
	}
}

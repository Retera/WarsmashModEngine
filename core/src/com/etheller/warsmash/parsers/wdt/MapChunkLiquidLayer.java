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

	public MapChunkLiquidLayer(final VertReader<T> vertReader) {
		this.vertReader = vertReader;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.minHeight = reader.readFloat32();
		this.maxHeight = reader.readFloat32();

		this.verts = (T[][]) new Object[9][9];
		for (int i = 0; i < this.verts.length; i++) {
			for (int j = 0; j < this.verts[i].length; j++) {
				final T parsed = this.vertReader.parse(reader);
				this.verts[i][j] = parsed;
			}
		}
		this.tiles = new byte[8][8];
		for (final byte[] row : this.tiles) {
			reader.readInt8Array(row);
		}
		this.nFlowvs = reader.readUInt32();
		this.flowvs = new SWFlowv[] { new SWFlowv(reader), new SWFlowv(reader) };
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		throw new RuntimeException();
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		throw new RuntimeException();
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
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

		public SWVert(final BinaryReader reader) {
			this.depth = reader.readInt8();
			this.flow0Pct = reader.readInt8();
			this.flow1Pct = reader.readInt8();
			this.filler = reader.readInt8();
			this.height = reader.readFloat32();
		}

		@Override
		public String toString() {
			return "SWVert [depth=" + this.depth + ", flow0Pct=" + this.flow0Pct + ", flow1Pct=" + this.flow1Pct
					+ ", filler=" + this.filler + ", height=" + this.height + "]";
		}

	}

	public static final class SOVert {
		public byte depth;
		public byte foam;
		public byte wet;
		public byte filler;
		public float height;

		public SOVert(final BinaryReader reader) {
			this.depth = reader.readInt8();
			this.foam = reader.readInt8();
			this.wet = reader.readInt8();
			this.filler = reader.readInt8();
//			this.height = reader.readFloat32();
		}

		@Override
		public String toString() {
			return "SOVert [depth=" + this.depth + ", foam=" + this.foam + ", wet=" + this.wet + ", filler="
					+ this.filler + ", height=" + this.height + "]";
		}

	}

	public static final class SMVert {
		public byte s;
		public byte t;
		public float height;

		public SMVert(final BinaryReader reader) {
			this.s = reader.readInt8();
			this.t = reader.readInt8();
			this.height = reader.readFloat32();
		}

		@Override
		public String toString() {
			return "SMVert [s=" + this.s + ", t=" + this.t + ", height=" + this.height + "]";
		}

	}

	public static final class SWFlowv {
		public float[] center = new float[3];
		public float radius;
		public float[] dir = new float[3];
		public float velocity;
		public float amplitude;
		public float frequency;

		public SWFlowv(final BinaryReader reader) {
			reader.readFloat32Array(this.center);
			this.radius = reader.readFloat32();
			reader.readFloat32Array(this.dir);
			this.velocity = reader.readFloat32();
			this.amplitude = reader.readFloat32();
			this.frequency = reader.readFloat32();
		}
	}
}

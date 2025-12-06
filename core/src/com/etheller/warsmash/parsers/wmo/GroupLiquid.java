package com.etheller.warsmash.parsers.wmo;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class GroupLiquid<T> implements MdlxBlock, MdlxChunk {
	private final int[] vertexCount = new int[2];
	private final int[] tileCount = new int[2];
	private final float[] corner = new float[3];
	private int materialId;
	private Vertex[][] liquidVertices;
	private Tile[][] liquidTiles;

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		reader.readInt32Array(this.vertexCount);
		reader.readInt32Array(this.tileCount);
		reader.readFloat32Array(this.corner);
		this.materialId = reader.readUInt16();
		this.liquidVertices = new Vertex[this.vertexCount[0]][this.vertexCount[1]];
		for (int i = 0; i < this.liquidVertices.length; i++) {
			for (int j = 0; j < this.liquidVertices[i].length; j++) {
				final Vertex vertex = new Vertex();
				vertex.read(reader);
				this.liquidVertices[i][j] = vertex;
			}
		}
		this.liquidTiles = new Tile[this.tileCount[0]][this.tileCount[1]];
		for (int i = 0; i < this.liquidTiles.length; i++) {
			for (int j = 0; j < this.liquidTiles[i].length; j++) {
				final Tile tile = new Tile(reader.readInt8());
				this.liquidTiles[i][j] = tile;
			}
		}
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

	public int[] getVertexCount() {
		return this.vertexCount;
	}

	public int[] getTileCount() {
		return this.tileCount;
	}

	public float[] getCorner() {
		return this.corner;
	}

	public int getMaterialId() {
		return this.materialId;
	}

	public Vertex[][] getLiquidVertices() {
		return this.liquidVertices;
	}

	public Tile[][] getLiquidTiles() {
		return this.liquidTiles;
	}

	@Override
	public long getByteLength(final int version) {
		return 14 + (this.vertexCount[0] * this.vertexCount[1] * 8) + (this.tileCount[0] * this.tileCount[1]);
	}

	public static final class Vertex {
		public byte flow1;
		public byte flow2;
		public byte flow1Pct;
		public byte filler;
		public float height;

		public void read(final BinaryReader reader) {
			this.flow1 = reader.readInt8();
			this.flow2 = reader.readInt8();
			this.flow1Pct = reader.readInt8();
			this.filler = reader.readInt8();
			this.height = reader.readFloat32();
		}

		public int getS() {
			return ((this.flow2 & 0xFF) << 8) | (this.flow1 & 0xFF);
		}

		public int getT() {
			return ((this.filler & 0xFF) << 8) | (this.flow1Pct & 0xFF);
		}

		public byte getFlow1() {
			return this.flow1;
		}

		public byte getFlow2() {
			return this.flow2;
		}

		public byte getFlow1Pct() {
			return this.flow1Pct;
		}

		public byte getFiller() {
			return this.filler;
		}

		public float getHeight() {
			return this.height;
		}
	}

	public static final class Tile {
		public final byte value;

		public Tile(final byte value) {
			this.value = value;
		}

		public byte getLiquid() {
			return (byte) (this.value & ~0xC0);
		}

		public boolean isFishable() {
			return (this.value & 0x40) == 0x40;
		}

		public boolean isShared() {
			return (this.value & 0x80) == 0x80;
		}
	}
}

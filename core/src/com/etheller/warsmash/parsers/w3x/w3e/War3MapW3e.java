package com.etheller.warsmash.parsers.w3x.w3e;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.parsers.wdt.Chunk;
import com.etheller.warsmash.parsers.wdt.MapChunkLiquidLayer;
import com.etheller.warsmash.parsers.wdt.MapChunkLiquidLayer.SOVert;
import com.etheller.warsmash.parsers.wdt.MapChunkLiquidLayer.SWVert;
import com.etheller.warsmash.parsers.wdt.WdtMap;
import com.etheller.warsmash.parsers.wdt.WdtMap.TileHeader;
import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * war3map.w3e - the environment file.
 */
public class War3MapW3e {
	private static final War3ID MAGIC_NUMBER = War3ID.fromString("W3E!");
	private int version;
	private char tileset = 'A';
	private int hasCustomTileset;
	private final List<War3ID> groundTiles = new ArrayList<>();
	private final List<War3ID> cliffTiles = new ArrayList<>();
	private final int[] mapSize = new int[2];
	private final float[] centerOffset = new float[2];
	private Corner[][] corners;

	public War3MapW3e(final LittleEndianDataInputStream stream) throws IOException {
		if (stream != null) {
			this.load(stream);
		}
	}

	private boolean load(final LittleEndianDataInputStream stream) throws IOException {
		final War3ID firstId = ParseUtils.readWar3ID(stream);
		if (!MAGIC_NUMBER.equals(firstId)) {
			return false;
		}

		this.version = stream.readInt();
		this.tileset = (char) stream.read();
		this.hasCustomTileset = stream.readInt();

		for (int i = 0, l = stream.readInt(); i < l; i++) {
			this.groundTiles.add(ParseUtils.readWar3ID(stream));
		}

		for (int i = 0, l = stream.readInt(); i < l; i++) {
			this.cliffTiles.add(ParseUtils.readWar3ID(stream));
		}

		ParseUtils.readInt32Array(stream, this.mapSize);
		ParseUtils.readFloatArray(stream, this.centerOffset);

		this.corners = new Corner[this.mapSize[1]][];
		for (int row = 0, rows = this.mapSize[1]; row < rows; row++) {
			this.corners[row] = new Corner[this.mapSize[0]];

			for (int column = 0, columns = this.mapSize[0]; column < columns; column++) {
				final Corner corner = new Corner();

				corner.load(stream);

				this.corners[row][column] = corner;
			}
		}

		return true;
	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeWar3ID(stream, MAGIC_NUMBER);
		stream.writeInt(this.version);
		stream.write(this.tileset);
		stream.writeInt(this.hasCustomTileset);
		ParseUtils.writeUInt32(stream, this.groundTiles.size());

		for (final War3ID groundTile : this.groundTiles) {
			ParseUtils.writeWar3ID(stream, groundTile);
		}

		ParseUtils.writeUInt32(stream, this.cliffTiles.size());

		for (final War3ID cliffTile : this.cliffTiles) {
			ParseUtils.writeWar3ID(stream, cliffTile);
		}

		ParseUtils.writeInt32Array(stream, this.mapSize);
		ParseUtils.writeFloatArray(stream, this.centerOffset);

		for (final Corner[] row : this.corners) {
			for (final Corner corner : row) {
				corner.save(stream);
			}
		}
	}

	public int getByteLength() {
		return 37 + (this.groundTiles.size() * 4) + (this.cliffTiles.size() * 4)
				+ (this.mapSize[0] * this.mapSize[1] * 7);
	}

	public int getVersion() {
		return this.version;
	}

	public char getTileset() {
		return this.tileset;
	}

	public int getHasCustomTileset() {
		return this.hasCustomTileset;
	}

	public List<War3ID> getGroundTiles() {
		return this.groundTiles;
	}

	public List<War3ID> getCliffTiles() {
		return this.cliffTiles;
	}

	public int[] getMapSize() {
		return this.mapSize;
	}

	public float[] getCenterOffset() {
		return this.centerOffset;
	}

	public Corner[][] getCorners() {
		return this.corners;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public void setTileset(final char tileset) {
		this.tileset = tileset;
	}

	public void setHasCustomTileset(final int hasCustomTileset) {
		this.hasCustomTileset = hasCustomTileset;
	}

	public void setCorners(final Corner[][] corners) {
		this.corners = corners;
	}

	public static final float WC3_ASHENVALE_WATER_HEIGHT = 0.7f;

	public static War3MapW3e generateConverted(final WdtMap map, final TileHeader notused) {
		War3MapW3e terrainFile;
		try {
			terrainFile = new War3MapW3e(null);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final float tilesize = 533.3333f;
		final float wowToWc3Factor = 128.0f / ((tilesize / 16) / 8);
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		for (final TileHeader header : map.tileHeaders) {
			final int tileIdx = header.idx;
			final int blockX = tileIdx % 64;
			final float wowXOffset = blockX * tilesize;
			final int blockY = tileIdx / 64;
			final float wowYOffset = blockY * tilesize;

			minX = Math.min(blockX, minX);
			minY = Math.min(blockY, minY);
			maxX = Math.max(blockX, maxX);
			maxY = Math.max(blockY, maxY);
		}

		terrainFile.version = 0; // TODO
		terrainFile.tileset = 'I';
		terrainFile.hasCustomTileset = 1;

		terrainFile.groundTiles.add(War3ID.fromString("Wsnw"));
		terrainFile.cliffTiles.add(War3ID.fromString("CLdi"));

		terrainFile.mapSize[0] = (((maxX - minX) + 1) * 16 * 8) + 1;
		terrainFile.mapSize[1] = (((maxY - minY) + 1) * 16 * 8) + 1;
		terrainFile.centerOffset[0] = -((terrainFile.mapSize[0] - 1) * 128f) / 2.f;
		terrainFile.centerOffset[1] = -((terrainFile.mapSize[1] - 1) * 128f) / 2.f;

		terrainFile.corners = new Corner[terrainFile.mapSize[1]][];
		for (int row = 0, rows = terrainFile.mapSize[1]; row < rows; row++) {
			terrainFile.corners[row] = new Corner[terrainFile.mapSize[0]];

			for (int column = 0, columns = terrainFile.mapSize[0]; column < columns; column++) {
				final Corner corner = new Corner();

//				corner.load(stream);

				terrainFile.corners[row][column] = corner;
			}
		}

		for (final TileHeader header : map.tileHeaders) {
			final int tileIdx = header.idx;
			final int blockX = tileIdx % 64;
			final int blockY = tileIdx / 64;

			final int blockOffsetX = blockX * 16 * 8;
			final int blockOffsetY = blockY * 16 * 8;

			for (final Chunk chunk : header.chunks) {
				// internet says: floor((32 - (axis / 533.33333)))
				final long indexX = chunk.getIndexX();
				final long indexY = chunk.getIndexY();

				final long war3ChunkIndexX = 15 - indexY;
				final long war3ChunkIndexY = indexX;

				final float[][] heightMap = chunk.getHeightMap();
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						final float height = heightMap[i][j];

						final int war3IndexX = blockOffsetY + (int) ((war3ChunkIndexX * 8) + (9 - i - 1));
						final int war3IndexY = blockOffsetX + (int) ((war3ChunkIndexY * 8) + j);
						if ((war3IndexX < 0) || (war3IndexY < 0) || (war3IndexX >= terrainFile.corners.length)
								|| (war3IndexY >= terrainFile.corners[0].length)) {
							System.out.println("bad");
						}
						final Corner corner = terrainFile.corners[war3IndexX][war3IndexY];
						corner.setGroundHeight((height * wowToWc3Factor) + 2.0f);

					}
				}
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						final float height = heightMap[i + 9][j];

						final int war3IndexX = blockOffsetY + (int) ((war3ChunkIndexX * 8) + (8 - i - 1));
						final int war3IndexY = blockOffsetX + (int) ((war3ChunkIndexY * 8) + j);
						final Corner corner = terrainFile.corners[war3IndexX][war3IndexY];
						corner.setWdtInterpolatedCenterHeight(height * wowToWc3Factor);

					}
				}
				final List<MapChunkLiquidLayer> mapChunkLiquidLayers = chunk.getMapChunkLiquidLayers();
				for (final MapChunkLiquidLayer layer : mapChunkLiquidLayers) {
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 9; j++) {
							final Object vert = layer.verts[i][j];
							float height = 0;
							byte depth = 0;
							if (vert instanceof SOVert) {
								final SOVert soVert = (SOVert) vert;
								height = soVert.height;
								depth = soVert.depth;
							}
							else if (vert instanceof SWVert) {
								final SWVert swVert = (SWVert) vert;
								height = swVert.height;
								depth = swVert.depth;
							}
							height = Math.max(layer.minHeight, Math.min(layer.maxHeight, height));
							final int war3IndexX = blockOffsetY + (int) ((war3ChunkIndexX * 8) + (9 - i - 1));
							final int war3IndexY = blockOffsetX + (int) ((war3ChunkIndexY * 8) + j);
							final Corner corner = terrainFile.corners[war3IndexX][war3IndexY];
							corner.setWaterHeight((height * wowToWc3Factor) + WC3_ASHENVALE_WATER_HEIGHT);
							corner.setWater(1);
						}
					}
				}
			}
		}

		return terrainFile;
	}

	public static War3MapW3e generateConverted(final WdtMap map) {
		War3MapW3e terrainFile;
		try {
			terrainFile = new War3MapW3e(null);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final float tilesize = 533.3333f;
		final float wowToWc3Factor = 128.0f / ((tilesize / 16) / 8);
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		for (final TileHeader header : map.tileHeaders) {
			final int tileIdx = header.idx;
			final int blockX = tileIdx % 64;
			final float wowXOffset = blockX * tilesize;
			final int blockY = 63 - (tileIdx / 64);
			final float wowYOffset = blockY * tilesize;

			minX = Math.min(blockX, minX);
			minY = Math.min(blockY, minY);
			maxX = Math.max(blockX, maxX);
			maxY = Math.max(blockY, maxY);
		}

		terrainFile.version = 0; // TODO
		terrainFile.tileset = 'I';
		terrainFile.hasCustomTileset = 1;

		terrainFile.groundTiles.add(War3ID.fromString("Wsnw"));
		terrainFile.cliffTiles.add(War3ID.fromString("CLdi"));

//		terrainFile.mapSize[0] = (((maxX - minX) + 1) * 16 * 8) + 1;
//		terrainFile.mapSize[1] = (((maxY - minY) + 1) * 16 * 8) + 1;
		terrainFile.mapSize[0] = (((63) + 1) * 16 * 8) + 1;
		terrainFile.mapSize[1] = (((63) + 1) * 16 * 8) + 1;
		terrainFile.centerOffset[0] = (-minX * 16 * 8 * 128f) + ((-(((maxX - minX) + 1) * 16 * 8) * 128f) / 2.0f);
		terrainFile.centerOffset[1] = (-minY * 16 * 8 * 128f) + ((-(((maxY - minY) + 1) * 16 * 8) * 128f) / 2.0f);

		terrainFile.corners = null;

		return terrainFile;
	}
}

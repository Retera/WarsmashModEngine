package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.common.FetchDataTypeName;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.w3x.w3e.Corner;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.StandardObjectData;
import com.etheller.warsmash.util.MappedDataRow;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.GenericResource;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.w3x.TerrainModel;
import com.etheller.warsmash.viewer5.handlers.w3x.Variations;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xShaders;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer.CliffInfo;

public class Terrain {
	private static final float[] sizeHeap = new float[2];
	private static final Vector3 normalHeap1 = new Vector3();
	private static final Vector3 normalHeap2 = new Vector3();

	public ShaderProgram groundShader;
	public ShaderProgram waterShader;
	public ShaderProgram cliffShader;
	public float waterIndex;
	public float waterIncreasePerFrame;
	public float waterHeightOffset;
	public List<Texture> waterTextures = new ArrayList<>();
	public float[] maxDeepColor = new float[4];
	public float[] minDeepColor = new float[4];
	public float[] maxShallowColor = new float[4];
	public float[] minShallowColor = new float[4];

	public List<Texture> tilesetTextures = new ArrayList<>();
	public List<Texture> cliffTextures = new ArrayList<>();
	public List<TerrainModel> cliffModels = new ArrayList<>();

	public Corner[][] corners;
	public float[] centerOffset = new float[2];
	public int[] mapSize = new int[2];

	public List<MappedDataRow> tilesets = new ArrayList<>(); // TODO
	public int blightTextureIndex = -1;
	public List<MappedDataRow> cliffTilesets = new ArrayList<>();
	public int columns;
	public int rows;

	public int vertexBuffer;
	public int faceBuffer;
	public int instanceBuffer;
	public int textureBuffer;
	public int variationBuffer;
	public int waterBuffer;
	public int heightMap;
	public int waterHeightMap;
	public int cliffHeightMap;

	public Terrain(final WebGL webGL, final DataSource dataSource, final WorldEditStrings worldEditStrings)
			throws IOException {

		final DataTable terrainTable = new DataTable(worldEditStrings);
		try (InputStream terrainSlkStream = dataSource.getResourceAsStream("TerrainArt\\Terrain.slk")) {
			terrainTable.readSLK(terrainSlkStream);
		}
		final DataTable cliffTable = new DataTable(worldEditStrings);
		try (InputStream cliffSlkStream = dataSource.getResourceAsStream("TerrainArt\\CliffTypes.slk")) {
			cliffTable.readSLK(cliffSlkStream);
		}

		this.groundShader = webGL.createShaderProgram(W3xShaders.Ground.vert, W3xShaders.Ground.frag);
		this.waterShader = webGL.createShaderProgram(W3xShaders.Water.vert, W3xShaders.Water.frag);
		this.cliffShader = webGL.createShaderProgram(W3xShaders.Cliffs.vert, W3xShaders.Cliffs.frag);

	}

	public void load(final War3MapW3e w3e, final float[] centerOffset, final int[] mapSize,
			final War3MapViewer viewer) {

		this.corners = w3e.getCorners();
		System.arraycopy(centerOffset, 0, this.centerOffset, 0, centerOffset.length);
		System.arraycopy(mapSize, 0, this.mapSize, 0, mapSize.length);

		final String texturesExt = viewer.solverParams.reforged ? ".dds" : ".blp";
		final char tileset = w3e.getTileset();

		for (final War3ID groundTile : w3e.getGroundTiles()) {
			final MappedDataRow row = viewer.terrainData.getRow(groundTile.asStringValue());

			this.tilesets.add(row);
			this.tilesetTextures
					.add((Texture) viewer.load(row.get("dir").toString() + "\\" + row.get("file") + texturesExt,
							viewer.mapPathSolver, viewer.solverParams));
		}

		final StandardObjectData standardObjectData = new StandardObjectData(viewer.mapMpq.getCompoundDataSource());
		final DataTable worldEditData = standardObjectData.getWorldEditData();
		final Element tilesets = worldEditData.get("TileSets");

		this.blightTextureIndex = this.tilesetTextures.size();
		this.tilesetTextures
				.add((Texture) viewer.load(tilesets.getField(Character.toString(tileset)).split(",")[1] + texturesExt,
						viewer.mapPathSolver, viewer.solverParams));

		for (final War3ID cliffTile : w3e.getCliffTiles()) {
			final MappedDataRow row = viewer.cliffTypesData.getRow(cliffTile.asStringValue());

			this.cliffTilesets.add(row);
			this.cliffTextures
					.add((Texture) viewer.load(row.get("texDir").toString() + "\\" + row.get("texFile") + texturesExt,
							viewer.mapPathSolver, viewer.solverParams));
		}

		final MappedDataRow waterRow = viewer.waterData.getRow(tileset + "Sha");

		this.waterHeightOffset = ((Number) waterRow.get("height")).floatValue();
		this.waterIncreasePerFrame = ((Number) waterRow.get("texRate")).intValue() / (float) 60;
		this.waterTextures.clear();
		this.maxDeepColor[0] = ((Number) waterRow.get("Dmax_R")).floatValue();
		this.maxDeepColor[1] = ((Number) waterRow.get("Dmax_G")).floatValue();
		this.maxDeepColor[2] = ((Number) waterRow.get("Dmax_B")).floatValue();
		this.maxDeepColor[3] = ((Number) waterRow.get("Dmax_A")).floatValue();
		this.minDeepColor[0] = ((Number) waterRow.get("Dmin_R")).floatValue();
		this.minDeepColor[1] = ((Number) waterRow.get("Dmin_G")).floatValue();
		this.minDeepColor[2] = ((Number) waterRow.get("Dmin_B")).floatValue();
		this.minDeepColor[3] = ((Number) waterRow.get("Dmin_A")).floatValue();
		this.maxShallowColor[0] = ((Number) waterRow.get("Smax_R")).floatValue();
		this.maxShallowColor[1] = ((Number) waterRow.get("Smax_G")).floatValue();
		this.maxShallowColor[2] = ((Number) waterRow.get("Smax_B")).floatValue();
		this.maxShallowColor[3] = ((Number) waterRow.get("Smax_A")).floatValue();
		this.minShallowColor[0] = ((Number) waterRow.get("Smin_R")).floatValue();
		this.minShallowColor[1] = ((Number) waterRow.get("Smin_G")).floatValue();
		this.minShallowColor[2] = ((Number) waterRow.get("Smin_B")).floatValue();
		this.minShallowColor[3] = ((Number) waterRow.get("Smin_A")).floatValue();

		for (int i = 0, l = ((Number) waterRow.get("numTex")).intValue(); i < l; i++) {
			this.waterTextures.add(
					(Texture) viewer.load(waterRow.get("texFile").toString() + ((i < 10) ? "0" : "") + i + texturesExt,
							viewer.mapPathSolver, viewer.solverParams));
		}

		final GL20 gl = viewer.gl;

		final Corner[][] corners = w3e.getCorners();
		final int columns = this.mapSize[0];
		final int rows = this.mapSize[1];
		final int instanceCount = (columns - 1) * (rows - 1);
		final float[] cliffHeights = new float[columns * rows];
		final float[] cornerHeights = new float[columns * rows];
		final float[] waterHeights = new float[columns * rows];
		final short[] cornerTextures = new short[instanceCount * 4];
		final short[] cornerVariations = new short[instanceCount * 4];
		final short[] waterFlags = new short[instanceCount];
		int instance = 0;
		final Map<String, CliffInfo> cliffs = new HashMap<>();

		this.columns = columns - 1;
		this.rows = rows - 1;

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				final Corner bottomLeft = corners[y][x];
				final int index = (y * columns) + x;

				cliffHeights[index] = bottomLeft.getGroundHeight();
				cornerHeights[index] = (bottomLeft.getGroundHeight() + bottomLeft.getLayerHeight()) - 2;
				waterHeights[index] = bottomLeft.getWaterHeight();

				if ((y < (rows - 1)) && (x < (columns - 1))) {
					// Water can be used with cliffs and normal corners, so store water state
					// regardless.
					waterFlags[instance] = this.isWater(x, y);

					// Is this a cliff, or a normal corner?
					if (this.isCliff(x, y)) {
						final int bottomLeftLayer = bottomLeft.getLayerHeight();
						final int bottomRightLayer = corners[y][x + 1].getLayerHeight();
						final int topLeftLayer = corners[y + 1][x].getLayerHeight();
						final int topRightLayer = corners[y + 1][x + 1].getLayerHeight();
						final int base = Math.min(Math.min(bottomLeftLayer, bottomRightLayer),
								Math.min(topLeftLayer, topRightLayer));
						final String fileName = this.cliffFileName(bottomLeftLayer, bottomRightLayer, topLeftLayer,
								topRightLayer, base);

						if (!"AAAA".equals(fileName)) {
							int cliffTexture = bottomLeft.getCliffTexture();

							// ?
							if (cliffTexture == 15) {
								cliffTexture = 1;
							}

							final MappedDataRow cliffRow = this.cliffTilesets.get(cliffTexture);
							final String dir = cliffRow.get("cliffModelDir").toString();
							final String path = "Doodads\\Terrain\\" + dir + "\\" + dir + fileName
									+ Variations.getCliffVariation(dir, fileName, bottomLeft.getCliffVariation())
									+ ".mdx";

							if (!cliffs.containsKey(path)) {
								cliffs.put(path, new CliffInfo());
							}

							cliffs.get(path).locations.add(new float[] { ((x + 1) * 128) + centerOffset[0],
									(y * 128) + centerOffset[1], (base - 2) * 128 });
							cliffs.get(path).textures.add(cliffTexture);
						}
					}
					else {
						final int bottomLeftTexture = this.cornerTexture(x, y);
						final int bottomRightTexture = this.cornerTexture(x + 1, y);
						final int topLeftTexture = this.cornerTexture(x, y + 1);
						final int topRightTexture = this.cornerTexture(x + 1, y + 1);
						final LinkedHashSet<Integer> texturesUnique = new LinkedHashSet<>();
						texturesUnique.add(bottomLeftTexture);
						texturesUnique.add(bottomRightTexture);
						texturesUnique.add(topLeftTexture);
						texturesUnique.add(topRightTexture);
						final List<Integer> textures = new ArrayList<>(texturesUnique);
						Collections.sort(textures);

						int texture = textures.remove(0);

						cornerTextures[instance * 4] = (short) (texture + 1);
						cornerVariations[instance * 4] = this.getVariation(texture, bottomLeft.getGroundVariation());

						for (int i = 0, l = textures.size(); i < l; i++) {
							int bitset = 0;

							texture = textures.get(i);

							if (bottomRightTexture == texture) {
								bitset |= 0b0001;
							}

							if (bottomLeftTexture == texture) {
								bitset |= 0b0010;
							}

							if (topRightTexture == texture) {
								bitset |= 0b0100;
							}

							if (topLeftTexture == texture) {
								bitset |= 0b1000;
							}

							cornerTextures[(instance * 4) + 1 + i] = (short) (texture + 1);
							cornerVariations[(instance * 4) + 1 + i] = (short) (bitset);
						}
					}

					instance += 1;

				}
			}
		}

		this.vertexBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.vertexBuffer);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, 8 * 4, RenderMathUtils.wrap(new float[] { 0, 0, 1, 0, 0, 1, 1, 1 }),
				GL20.GL_STATIC_DRAW);

		this.faceBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.faceBuffer);
		gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, 6, RenderMathUtils.wrap(new byte[] { 0, 1, 2, 1, 3, 2 }),
				GL20.GL_STATIC_DRAW);

		this.cliffHeightMap = gl.glGenTexture();
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.cliffHeightMap);
		viewer.webGL.setTextureMode(GL20.GL_CLAMP_TO_EDGE, GL20.GL_CLAMP_TO_EDGE, GL20.GL_NEAREST, GL20.GL_NEAREST);
		gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL30.GL_R32F, columns, rows, 0, GL30.GL_RED, GL20.GL_FLOAT,
				RenderMathUtils.wrap(cliffHeights));

		this.heightMap = gl.glGenTexture();
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.heightMap);
		viewer.webGL.setTextureMode(GL20.GL_CLAMP_TO_EDGE, GL20.GL_CLAMP_TO_EDGE, GL20.GL_NEAREST, GL20.GL_NEAREST);
		gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL30.GL_R32F, columns, rows, 0, GL30.GL_RED, GL20.GL_FLOAT,
				RenderMathUtils.wrap(cornerHeights));

		this.waterHeightMap = gl.glGenTexture();
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.waterHeightMap);
		viewer.webGL.setTextureMode(GL20.GL_CLAMP_TO_EDGE, GL20.GL_CLAMP_TO_EDGE, GL20.GL_NEAREST, GL20.GL_NEAREST);
		gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL30.GL_R32F, columns, rows, 0, GL30.GL_RED, GL20.GL_FLOAT,
				RenderMathUtils.wrap(waterHeights));

		this.instanceBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.instanceBuffer);
		final float[] instanceBufferData = new float[instanceCount];
		for (int i = 0; i < instanceBufferData.length; i++) {
			instanceBufferData[i] = i;
		}
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, instanceBufferData.length * 4, RenderMathUtils.wrap(instanceBufferData),
				GL20.GL_STATIC_DRAW);

		this.textureBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.textureBuffer);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, cornerTextures.length, RenderMathUtils.wrap(cornerTextures),
				GL20.GL_STATIC_DRAW);

		this.variationBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.variationBuffer);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, cornerVariations.length, RenderMathUtils.wrap(cornerVariations),
				GL20.GL_STATIC_DRAW);

		this.waterBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.waterBuffer);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, waterFlags.length, RenderMathUtils.wrap(waterFlags), GL20.GL_STATIC_DRAW);

		final ShaderProgram cliffShader = this.cliffShader;
		this.cliffModels.clear();
		for (final Map.Entry<String, CliffInfo> entry : cliffs.entrySet()) {
			final String path = entry.getKey();
			final CliffInfo cliffInfo = entry.getValue();

			final GenericResource resource = viewer.loadMapGeneric(path, FetchDataTypeName.ARRAY_BUFFER,
					viewer.streamDataCallback);

			this.cliffModels.add(new TerrainModel(viewer, (InputStream) resource.data, cliffInfo.locations,
					cliffInfo.textures, cliffShader));
		}
	}

	public boolean isCliff(final int column, final int row) {
		if ((column < 1) || (column > (this.columns - 1)) || (row < 1) || (row > (this.rows - 1))) {
			return false;
		}

		final Corner[][] corners = this.corners;
		final int bottomLeft = corners[row][column].getLayerHeight();
		final int bottomRight = corners[row][column + 1].getLayerHeight();
		final int topLeft = corners[row + 1][column].getLayerHeight();
		final int topRight = corners[row + 1][column + 1].getLayerHeight();

		return (bottomLeft != bottomRight) || (bottomLeft != topLeft) || (bottomLeft != topRight);
	}

	public short isWater(final int column, final int row) {
		return ((this.corners[row][column].getWater() != 0) || (this.corners[row][column + 1].getWater() != 0)
				|| (this.corners[row + 1][column].getWater() != 0)
				|| (this.corners[row + 1][column + 1].getWater() != 0)) ? (short) 1 : (short) 0;
	}

	public String cliffFileName(final int bottomLeftLayer, final int bottomRightLayer, final int topLeftLayer,
			final int topRightLayer, final int base) {
		return Character.toString((char) ((65 + bottomLeftLayer) - base))
				+ Character.toString((char) ((65 + topLeftLayer) - base))
				+ Character.toString((char) ((65 + topRightLayer) - base))
				+ Character.toString((char) ((65 + bottomRightLayer) - base));
	}

	public short getVariation(final int groundTexture, final int variation) {
		final Texture texture = this.tilesetTextures.get(groundTexture);

		// Extended ?
		if (texture.getWidth() > texture.getHeight()) {
			if (variation < 16) {
				return (short) (16 + variation);
			}
			else if (variation == 16) {
				return 15;
			}
			else {
				return 0;
			}
		}
		else {
			if (variation == 0) {
				return 0;
			}
			else {
				return 15;
			}
		}
	}

	public int cliffGroundIndex(final int whichCliff) {
		final String whichTile = this.cliffTilesets.get(whichCliff).get("groundTile").toString();
		final List<MappedDataRow> tilesets = this.tilesets;

		for (int i = 0, l = tilesets.size(); i < l; i++) {
			if (tilesets.get(i).get("tileID").toString().equals(whichTile)) {
				return i;
			}
		}
		throw new IllegalArgumentException(Integer.toString(whichCliff));
	}

	public int cornerTexture(final int column, final int row) {
		final Corner[][] corners = this.corners;
		final int columns = this.columns;
		final int rows = this.rows;

		for (int y = -1; y < 1; y++) {
			for (int x = -1; x < 1; x++) {
				if (((column + x) > 0) && ((column + x) < (columns - 1)) && ((row + y) > 0)
						&& ((row + y) < (rows - 1))) {
					if (this.isCliff(column + x, row + y)) {
						int texture = corners[row + y][column + x].getCliffTexture();

						if (texture == 15) {
							texture = 1;
						}

						return this.cliffGroundIndex(texture);
					}
				}
			}
		}

		final Corner corner = corners[row][column];
		if (corner.getBlight() != 0) {
			return this.blightTextureIndex;
		}
		return corner.getGroundTexture();
	}

	public void update() {
		this.waterIndex += this.waterIncreasePerFrame;

		if (this.waterIndex >= this.waterTextures.size()) {
			this.waterIndex = 0;
		}
	}

	public void renderGround(final GL20 gl, final WebGL webgl, final Scene worldScene) {
		final ANGLEInstancedArrays instancedArrays = webgl.instancedArrays;
		final ShaderProgram shader = this.groundShader;
		final List<Texture> tilesetTextures = this.tilesetTextures;
		final int instanceAttrib = shader.getAttributeLocation("a_InstanceID");
		final int positionAttrib = shader.getAttributeLocation("a_position");
		final int texturesAttrib = shader.getAttributeLocation("a_textures");
		final int variationsAttrib = shader.getAttributeLocation("a_variations");
		final int tilesetCount = tilesetTextures.size();

		gl.glEnable(GL20.GL_BLEND);
		gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		webgl.useShaderProgram(shader);

		shader.setUniformMatrix("u_VP", worldScene.camera.viewProjectionMatrix);
		shader.setUniform2fv("u_offset", this.centerOffset, 0, 2);
		sizeHeap[0] = this.columns;
		sizeHeap[1] = this.rows;
		shader.setUniform2fv("u_size", sizeHeap, 0, 2);
		shader.setUniformi("u_heightMap", 15);

		gl.glActiveTexture(GL20.GL_TEXTURE15);
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.heightMap);

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.vertexBuffer);
		shader.setVertexAttribute(positionAttrib, 2, GL20.GL_FLOAT, false, 8, 0);

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.instanceBuffer);
		shader.setVertexAttribute(instanceAttrib, 1, GL20.GL_FLOAT, false, 4, 0);
		instancedArrays.glVertexAttribDivisorANGLE(instanceAttrib, 1);

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.textureBuffer);
		shader.setVertexAttribute(texturesAttrib, 4, GL20.GL_UNSIGNED_BYTE, false, 4, 0);
		instancedArrays.glVertexAttribDivisorANGLE(texturesAttrib, 1);

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.variationBuffer);
		shader.setVertexAttribute(variationsAttrib, 4, GL20.GL_UNSIGNED_BYTE, false, 4, 0);
		instancedArrays.glVertexAttribDivisorANGLE(variationsAttrib, 1);

		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.faceBuffer);

		shader.setUniformi("u_baseTileset", 0);

		for (int i = 0, l = Math.min(tilesetCount, 15); i < l; i++) {
			final int isExtended = (tilesetTextures.get(i).getWidth() > tilesetTextures.get(i).getHeight()) ? 1 : 0;

			shader.setUniformf("u_extended[" + i + "]", isExtended);
			shader.setUniformi("u_tilesets[" + i + "]", i);

			tilesetTextures.get(i).bind(i);
		}

		instancedArrays.glDrawElementsInstancedANGLE(GL20.GL_TRIANGLES, 6, GL20.GL_UNSIGNED_BYTE, 0,
				this.rows * this.columns);

		if (tilesetCount > 15) {
			shader.setUniformi("u_baseTileset", 15);

			for (int i = 0, l = tilesetCount - 15; i < l; i++) {
				final int isExtended = (tilesetTextures.get(i + 15).getWidth() > tilesetTextures.get(i + 15)
						.getHeight()) ? 1 : 0;

				shader.setUniformf("u_extended[" + i + "]", isExtended);

				tilesetTextures.get(i + 15).bind(i);
			}

			instancedArrays.glDrawElementsInstancedANGLE(GL20.GL_TRIANGLES, 6, GL20.GL_UNSIGNED_BYTE, 0,
					this.rows * this.columns);
		}

		instancedArrays.glVertexAttribDivisorANGLE(texturesAttrib, 0);
		instancedArrays.glVertexAttribDivisorANGLE(variationsAttrib, 0);
		instancedArrays.glVertexAttribDivisorANGLE(instanceAttrib, 0);
	}

	public void renderWater(final GL20 gl, final WebGL webgl, final Scene worldScene) {
		final ANGLEInstancedArrays instancedArrays = webgl.instancedArrays;
		final ShaderProgram shader = this.waterShader;
		final int instanceAttrib = shader.getAttributeLocation("a_InstanceID");
		final int positionAttrib = shader.getAttributeLocation("a_position");
		final int isWaterAttrib = shader.getAttributeLocation("a_isWater");

		gl.glDepthMask(false);

		gl.glEnable(GL20.GL_BLEND);
		gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		webgl.useShaderProgram(shader);

		shader.setUniformMatrix("u_VP", worldScene.camera.viewProjectionMatrix);
		shader.setUniform2fv("u_offset", this.centerOffset, 0, 2);
		sizeHeap[0] = this.columns;
		sizeHeap[1] = this.rows;
		shader.setUniform2fv("u_size", sizeHeap, 0, 2);
		shader.setUniformi("u_heightMap", 0);
		shader.setUniformi("u_waterHeightMap", 1);
		shader.setUniformi("u_waterTexture", 2);
		shader.setUniformf("u_offsetHeight", this.waterHeightOffset);
		shader.setUniform4fv("u_maxDeepColor", this.maxDeepColor, 0, 4);
		shader.setUniform4fv("u_minDeepColor", this.minDeepColor, 0, 4);
		shader.setUniform4fv("u_maxShallowColor", this.maxShallowColor, 0, 4);
		shader.setUniform4fv("u_minShallowColor", this.minShallowColor, 0, 4);

		gl.glActiveTexture(GL20.GL_TEXTURE0);
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.heightMap);

		gl.glActiveTexture(GL20.GL_TEXTURE1);
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.waterHeightMap);

		this.waterTextures.get((int) this.waterIndex).bind(2);

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.vertexBuffer);
		shader.setVertexAttribute(positionAttrib, 2, GL20.GL_FLOAT, false, 8, 0);

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.instanceBuffer);
		shader.setVertexAttribute(instanceAttrib, 1, GL20.GL_FLOAT, false, 4, 0);
		instancedArrays.glVertexAttribDivisorANGLE(instanceAttrib, 1);

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.waterBuffer);
		shader.setVertexAttribute(isWaterAttrib, 1, GL20.GL_UNSIGNED_BYTE, false, 1, 0);
		instancedArrays.glVertexAttribDivisorANGLE(isWaterAttrib, 1);

		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.faceBuffer);
		instancedArrays.glDrawElementsInstancedANGLE(GL20.GL_TRIANGLES, 6, GL20.GL_UNSIGNED_BYTE, 0,
				this.rows * this.columns);

		instancedArrays.glVertexAttribDivisorANGLE(isWaterAttrib, 0);
		instancedArrays.glVertexAttribDivisorANGLE(instanceAttrib, 0);
	}

	public void renderCliffs(final GL20 gl, final WebGL webGL, final Scene worldScene) {
		final ANGLEInstancedArrays instancedArrays = webGL.instancedArrays;
		final ShaderProgram shader = this.cliffShader;

		gl.glDisable(GL20.GL_BLEND);

		webGL.useShaderProgram(shader);

		shader.setUniformMatrix("u_VP", worldScene.camera.viewProjectionMatrix);
		shader.setUniformi("u_heightMap", 0);
		shader.setUniformf("u_pixel[0]", 1 / (float) (this.columns + 1));
		shader.setUniformf("u_pixel[1]", 1 / (float) (this.rows + 1));
		shader.setUniform2fv("u_centerOffset", this.centerOffset, 0, 2);
		shader.setUniformi("u_texture1", 1);
		shader.setUniformi("u_texture2", 2);

		gl.glActiveTexture(GL20.GL_TEXTURE0);
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.cliffHeightMap);

		gl.glActiveTexture(GL20.GL_TEXTURE1);
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.cliffTextures.get(0).getGlTarget());

		if (this.cliffTextures.size() > 1) {
			gl.glActiveTexture(GL20.GL_TEXTURE2);
			gl.glBindTexture(GL20.GL_TEXTURE_2D, this.cliffTextures.get(1).getGlTarget());
		}

		// Set instanced attributes.
		for (final TerrainModel cliff : this.cliffModels) {
			cliff.render(shader);
		}
	}

	public Vector3 groundNormal(final Vector3 out, int x, int y) {
		final float[] centerOffset = this.centerOffset;
		final int[] mapSize = this.mapSize;

		x = (int) ((x - centerOffset[0]) / 128);
		y = (int) ((y - centerOffset[1]) / 128);

		final int cellX = x;
		final int cellY = y;

		// See if this coordinate is in the map

		if ((cellX >= 0) && (cellX < (mapSize[0] - 1)) && (cellY >= 0) && (cellY < (mapSize[1] - 1))) {
			// See http://gamedev.stackexchange.com/a/24574
			final Corner[][] corners = this.corners;
			final float bottomLeft = corners[cellY][cellX].getGroundHeight();
			final float bottomRight = corners[cellY][cellX + 1].getGroundHeight();
			final float topLeft = corners[cellY + 1][cellX].getGroundHeight();
			final float topRight = corners[cellY + 1][cellX + 1].getGroundHeight();
			final int sqX = x - cellX;
			final int sqY = y - cellY;

			if ((sqX + sqY) < 1) {
				normalHeap1.set(1, 0, bottomRight - bottomLeft);
				normalHeap2.set(0, 1, topLeft - bottomLeft);
			}
			else {
				normalHeap1.set(-1, 0, topRight - topLeft);
				normalHeap2.set(0, 1, topRight - bottomRight);
			}

			out.set(normalHeap1.crs(normalHeap2)).nor();
		}
		else {
			out.set(0, 0, 1);
		}

		return out;
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.w3x.w3e.Corner;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.StandardObjectData;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.w3x.Variations;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class Terrain {
	private static final String[] colorTags = { "R", "G", "B", "A" };
	private static final float[] sizeHeap = new float[2];
	private static final Vector3 normalHeap1 = new Vector3();
	private static final Vector3 normalHeap2 = new Vector3();
	private static final float[] fourComponentHeap = new float[4];
	private static final Matrix4 tempMatrix = new Matrix4();

	public ShaderProgram groundShader;
	public ShaderProgram waterShader;
	public ShaderProgram cliffShader;
	public float waterIndex;
	public float waterIncreasePerFrame;
	public float waterHeightOffset;

//
	public List<GroundTexture> groundTextures = new ArrayList<>();
	public List<UnloadedTexture> cliffTextures = new ArrayList<>();
	public RenderCorner[][] corners;
	public int columns;
	public int rows;
	public int blightTextureIndex = -1;
	public float[] maxDeepColor = new float[4];
	public float[] minDeepColor = new float[4];
	public float[] maxShallowColor = new float[4];
	public float[] minShallowColor = new float[4];

	private final DataTable terrainTable;
	private final DataTable cliffTable;
	private final DataTable waterTable;
	private final int waterTextureCount;
	private int cliffTexturesSize;
	private final List<CliffMesh> cliffMeshes = new ArrayList<>();
	private final Map<String, Integer> pathToCliff = new HashMap<>();
	private final Map<String, Integer> groundTextureToId = new HashMap<>();
	private final List<Integer> cliffToGroundTexture = new ArrayList<>();
	private final List<IVec3> cliffs = new ArrayList<>();
	private final DataSource dataSource;
	private final float[] groundHeights;
	private final float[] groundCornerHeights;
	private final short[] groundTextureList;
	private final float[] waterHeights;
	private final byte[] waterExistsData;

	private int groundTextureData = -1;
	private final int groundHeight;
	private final int groundCornerHeight;
	private final int cliffTextureArray;
	private final int waterHeight;
	private final int waterExists;
	private final int waterTextureArray;
	private final Camera camera;
	private final War3MapViewer viewer;
	public float[] centerOffset;
	private final WebGL webGL;

	public Terrain(final War3MapW3e w3eFile, final WebGL webGL, final DataSource dataSource,
			final WorldEditStrings worldEditStrings, final War3MapViewer viewer) throws IOException {
		this.webGL = webGL;
		this.viewer = viewer;
		this.camera = viewer.worldScene.camera;
		this.dataSource = dataSource;
		final String texturesExt = ".blp";
		final Corner[][] corners = w3eFile.getCorners();
		this.corners = new RenderCorner[corners[0].length][corners.length];
		for (int i = 0; i < corners.length; i++) {
			for (int j = 0; j < corners[i].length; j++) {
				this.corners[j][i] = new RenderCorner(corners[i][j]);
			}
		}
		final int width = w3eFile.getMapSize()[0];
		final int height = w3eFile.getMapSize()[1];
		this.columns = width;
		this.rows = height;
		for (int i = 0; i < (width - 1); i++) {
			for (int j = 0; j < (height - 1); j++) {
				final RenderCorner bottomLeft = this.corners[i][j];
				final RenderCorner bottomRight = this.corners[i + 1][j];
				final RenderCorner topLeft = this.corners[i][j + 1];
				final RenderCorner topRight = this.corners[i + 1][j + 1];

				bottomLeft.cliff = (bottomLeft.getLayerHeight() != bottomRight.getLayerHeight())
						|| (bottomLeft.getLayerHeight() != topLeft.getLayerHeight())
						|| (bottomLeft.getLayerHeight() != topRight.getLayerHeight());
			}
		}

		this.terrainTable = new DataTable(worldEditStrings);
		try (InputStream terrainSlkStream = dataSource.getResourceAsStream("TerrainArt\\Terrain.slk")) {
			this.terrainTable.readSLK(terrainSlkStream);
		}
		this.cliffTable = new DataTable(worldEditStrings);
		try (InputStream cliffSlkStream = dataSource.getResourceAsStream("TerrainArt\\CliffTypes.slk")) {
			this.cliffTable.readSLK(cliffSlkStream);
		}
		this.waterTable = new DataTable(worldEditStrings);
		try (InputStream waterSlkStream = dataSource.getResourceAsStream("TerrainArt\\Water.slk")) {
			this.waterTable.readSLK(waterSlkStream);
		}

		final char tileset = w3eFile.getTileset();
		final Element waterInfo = this.waterTable.get(tileset + "Sha");
		this.waterHeightOffset = waterInfo.getFieldFloatValue("height");
		this.waterTextureCount = waterInfo.getFieldValue("numTex");
		this.waterIncreasePerFrame = waterInfo.getFieldValue("texRate") / 60f;

		loadWaterColor(this.minShallowColor, "Smin", waterInfo);
		loadWaterColor(this.maxShallowColor, "Smax", waterInfo);
		loadWaterColor(this.minDeepColor, "Dmin", waterInfo);
		loadWaterColor(this.maxDeepColor, "Dmax", waterInfo);

		// Cliff Meshes

		final Map<String, Integer> cliffVars = Variations.CLIFF_VARS;
		for (final Map.Entry<String, Integer> cliffVar : cliffVars.entrySet()) {
			final Integer maxVariations = cliffVar.getValue();
			for (int variation = 0; variation <= maxVariations; variation++) {
				final String fileName = "Doodads\\Terrain\\Cliffs\\Cliffs" + cliffVar.getKey() + variation + ".mdx";
				this.cliffMeshes.add(new CliffMesh(fileName, dataSource, Gdx.gl30));
				this.pathToCliff.put(cliffVar.getKey() + variation, this.cliffMeshes.size() - 1);
			}
		}

		// Ground textures
		for (final War3ID groundTile : w3eFile.getGroundTiles()) {
			final Element terrainTileInfo = this.terrainTable.get(groundTile.asStringValue());
			final String dir = terrainTileInfo.getField("dir");
			final String file = terrainTileInfo.getField("file");
			this.groundTextures.add(new GroundTexture(dir + "/" + file + texturesExt, dataSource, Gdx.gl30));
			this.groundTextureToId.put(groundTile.asStringValue(), this.groundTextures.size() - 1);
		}

		final StandardObjectData standardObjectData = new StandardObjectData(dataSource);
		final DataTable worldEditData = standardObjectData.getWorldEditData();
		final Element tilesets = worldEditData.get("TileSets");

		this.blightTextureIndex = this.groundTextures.size();
		this.groundTextures.add(new GroundTexture(
				tilesets.getField(Character.toString(tileset)).split(",")[1] + texturesExt, dataSource, Gdx.gl30));

		// Cliff Textures
		for (final War3ID cliffTile : w3eFile.getCliffTiles()) {
			final Element cliffInfo = this.cliffTable.get(cliffTile.asStringValue());
			final String texDir = cliffInfo.getField("texDir");
			final String texFile = cliffInfo.getField("texFile");
			try (InputStream imageStream = dataSource.getResourceAsStream(texDir + "/" + texFile + texturesExt)) {
				final BufferedImage image = ImageIO.read(imageStream);
				this.cliffTextures.add(new UnloadedTexture(image.getWidth(), image.getHeight(),
						ImageUtils.getTextureBuffer(ImageUtils.forceBufferedImagesRGB(image))));
			}
			this.cliffTexturesSize = Math.max(this.cliffTexturesSize,
					this.cliffTextures.get(this.cliffTextures.size() - 1).width);
			this.cliffToGroundTexture.add(this.groundTextureToId.get(cliffInfo.getField("groundTile")));
		}

		updateCliffMeshes(new Rectangle(0, 0, width - 1, height - 1));

		// prepare GPU data
		this.groundHeights = new float[width * height];
		this.groundCornerHeights = new float[width * height];
		this.groundTextureList = new short[(width - 1) * (height - 1) * 4];
		this.waterHeights = new float[width * height];
		this.waterExistsData = new byte[width * height];

		updateGroundTextures(new Rectangle(0, 0, width - 1, height - 1));
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				this.groundCornerHeights[(j * width) + i] = this.corners[i][j].computeFinalGroundHeight();
				this.waterExistsData[(j * width) + i] = (byte) this.corners[i][j].getWater();
				this.groundHeights[(j * width) + i] = this.corners[i][j].getGroundHeight();
				this.waterHeights[(j * width) + i] = this.corners[i][j].getWaterHeight();
			}
		}

		final GL30 gl = Gdx.gl30;
		// Ground
		this.groundTextureData = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundTextureData);
		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA16UI, width - 1, height - 1, 0, GL30.GL_RGBA_INTEGER,
				GL30.GL_UNSIGNED_SHORT, RenderMathUtils.wrapShort(this.groundTextureList));
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

		this.groundHeight = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundHeight);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, width, height, 0, GL30.GL_RED, GL30.GL_FLOAT,
				RenderMathUtils.wrap(this.groundHeights));
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

		this.groundCornerHeight = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeight);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, width, height, 0, GL30.GL_RED, GL30.GL_FLOAT,
				RenderMathUtils.wrap(this.groundCornerHeights));
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

		// Cliff
		this.cliffTextureArray = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.cliffTextureArray);
		gl.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL30.GL_RGBA8, this.cliffTexturesSize, this.cliffTexturesSize,
				this.cliffTextures.size(), 0, Extensions.GL_BGRA, GL30.GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_BASE_LEVEL, 0);

		int sub = 0;
		for (final UnloadedTexture i : this.cliffTextures) {
			gl.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, sub, i.width, i.height, 1, Extensions.GL_BGRA,
					GL30.GL_UNSIGNED_BYTE, i.data);
			sub += 1;
		}
		gl.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);

		// Water
		this.waterHeight = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterHeight);
		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R16F, width, height, 0, GL30.GL_RED, GL30.GL_FLOAT,
				RenderMathUtils.wrap(this.waterHeights));
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

		this.waterExists = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterExists);
		gl.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);
		gl.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_R8, width, height, 0, GL30.GL_RED, GL30.GL_UNSIGNED_BYTE,
				RenderMathUtils.wrap(this.waterExistsData));
		gl.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 4);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);

		// Water textures
		this.waterTextureArray = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.waterTextureArray);
		gl.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL30.GL_RGBA8, 128, 128, this.waterTextureCount, 0,
				Extensions.GL_BGRA, GL30.GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_BASE_LEVEL, 0);

		final String fileName = waterInfo.getField("texFile");
		for (int i = 0; i < this.waterTextureCount; i++) {

			try (InputStream imageStream = dataSource
					.getResourceAsStream(fileName + (i < 10 ? "0" : "") + Integer.toString(i) + texturesExt)) {
				final BufferedImage image = ImageIO.read(imageStream);
				if ((image.getWidth() != 128) || (image.getHeight() != 128)) {
					System.err.println(
							"Odd water texture size detected of " + image.getWidth() + " x " + image.getHeight());
				}

				gl.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, image.getWidth(), image.getHeight(), 1,
						GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE,
						ImageUtils.getTextureBuffer(ImageUtils.forceBufferedImagesRGB(image)));
			}
		}
		gl.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);

		updateGroundHeights(new Rectangle(0, 0, width - 1, height - 1));

		this.groundShader = webGL.createShaderProgram(HiveWEShaders.Terrain.vert, HiveWEShaders.Terrain.frag);
		this.cliffShader = webGL.createShaderProgram(HiveWEShaders.Cliffs.vert, HiveWEShaders.Cliffs.frag);
		this.waterShader = webGL.createShaderProgram(HiveWEShaders.Water.vert, HiveWEShaders.Water.frag);

		// TODO collision bodies (?)

		this.centerOffset = w3eFile.getCenterOffset();
	}

	private void updateGroundHeights(final Rectangle area) {
		for (int j = (int) area.y; j < (area.y + area.height); j++) {
			for (int i = (int) area.x; i < (area.x + area.width); i++) {
				this.groundHeights[(j * this.columns) + i] = this.corners[i][j].getGroundHeight();

				float rampHeight = 0f;
				// Check if in one of the configurations the bottom_left is a ramp
				XLoop: for (int xOffset = -1; xOffset <= 0; xOffset++) {
					for (int yOffset = -1; yOffset <= 0; yOffset++) {
						if (((i + xOffset) >= 0) && ((i + xOffset) < (this.columns - 1)) && ((j + yOffset) >= 0)
								&& ((j + yOffset) < (this.rows - 1))) {
							final RenderCorner bottomLeft = this.corners[i + xOffset][j + yOffset];
							final RenderCorner bottomRight = this.corners[i + 1 + xOffset][j + yOffset];
							final RenderCorner topLeft = this.corners[i + xOffset][j + 1 + yOffset];
							final RenderCorner topRight = this.corners[i + 1 + xOffset][j + 1 + yOffset];

							final int base = Math.min(
									Math.min(bottomLeft.getLayerHeight(), bottomRight.getLayerHeight()),
									Math.min(topLeft.getLayerHeight(), topRight.getLayerHeight()));
							if (this.corners[i][j].getLayerHeight() != base) {
								continue;
							}

							if (isCornerRampEntrance(i + xOffset, j + yOffset)) {
								rampHeight = 0.5f;
								break XLoop;
							}
						}
					}
				}

				this.groundCornerHeights[(j * this.columns) + i] = this.corners[i][j].computeFinalGroundHeight()
						+ rampHeight;
			}
		}
		updateGroundHeights();
		updateCornerHeights();
	}

	private void updateGroundHeights() {
		Gdx.gl30.glBindTexture(GL30.GL_TEXTURE_2D, this.groundHeight);
		Gdx.gl30.glTexSubImage2D(GL30.GL_TEXTURE_2D, 0, 0, 0, this.columns, this.rows, GL30.GL_RED, GL30.GL_FLOAT,
				RenderMathUtils.wrap(this.groundHeights));
	}

	private void updateCornerHeights() {
		Gdx.gl30.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeight);
		Gdx.gl30.glTexSubImage2D(GL30.GL_TEXTURE_2D, 0, 0, 0, this.columns, this.rows, GL30.GL_RED, GL30.GL_FLOAT,
				RenderMathUtils.wrap(this.groundCornerHeights));
	}

	/// TODO clean
	/// Function is a bit of a mess
	/// Updates the cliff and ramp meshes for an area
	private void updateCliffMeshes(final Rectangle area) throws IOException {
		// Remove all existing cliff meshes in area
		for (int i = this.cliffs.size(); i-- > 0;) {
			final IVec3 pos = this.cliffs.get(i);
			if (area.contains(pos.x, pos.y)) {
				this.cliffs.remove(i);
			}
		}

		for (int i = (int) area.getX(); i < (int) (area.getX() + area.getWidth()); i++) {
			for (int j = (int) area.getY(); j < (int) (area.getY() + area.getHeight()); j++) {
				this.corners[i][j].romp = false;
			}
		}

		final Rectangle adjusted = new Rectangle(area.x - 2, area.y - 2, area.width + 4, area.height + 4);
		final Rectangle rampArea = new Rectangle();
		Intersector.intersectRectangles(new Rectangle(0, 0, this.columns, this.rows), adjusted, rampArea);

		// Add new cliff meshes
		final int xLimit = (int) ((rampArea.getX() + rampArea.getWidth()) - 1);
		for (int i = (int) rampArea.getX(); i < xLimit; i++) {
			final int yLimit = (int) ((rampArea.getY() + rampArea.getHeight()) - 1);
			for (int j = (int) rampArea.getY(); j < yLimit; j++) {
				final RenderCorner bottomLeft = this.corners[i][j];
				final RenderCorner bottomRight = this.corners[i + 1][j];
				final RenderCorner topLeft = this.corners[i][j + 1];
				final RenderCorner topRight = this.corners[i + 1][j + 1];

				if (bottomLeft.cliff) {
					final int base = Math.min(Math.min(bottomLeft.getLayerHeight(), bottomRight.getLayerHeight()),
							Math.min(topLeft.getLayerHeight(), topRight.getLayerHeight()));

					final boolean facingDown = (topLeft.getLayerHeight() >= bottomLeft.getLayerHeight())
							&& (topRight.getLayerHeight() >= bottomRight.getLayerHeight());
					final boolean facingLeft = (bottomRight.getLayerHeight() >= bottomLeft.getLayerHeight())
							&& (topRight.getLayerHeight() >= topLeft.getLayerHeight());

					if (!(facingDown && (j == 0)) && !(!facingDown && (j >= (this.rows - 2)))
							&& !(facingLeft && (i == 0)) && !(!facingLeft && (i >= (this.columns - 2)))) {
						final boolean br = ((bottomLeft.getRamp() != 0) != (bottomRight.getRamp() != 0))
								&& ((topLeft.getRamp() != 0) != (topRight.getRamp() != 0))
								&& !this.corners[i + bottomRight.getRamp()][j + (facingDown ? -1 : 1)].cliff;

						final boolean bo = ((bottomLeft.getRamp() != 0) != (topLeft.getRamp() != 0))
								&& ((bottomRight.getRamp() != 0) != (topRight.getRamp() != 0))
								&& !this.corners[i + (facingLeft ? -1 : 1)][j + topLeft.getRamp()].cliff;

						if (br || bo) {
							String fileName = "" + (char) ((bottomLeft.getRamp() != 0 ? 'L' : 'A')
									+ ((bottomLeft.getLayerHeight() - base) * (bottomLeft.getRamp() != 0 ? -4 : 1)))
									+ (char) ((topLeft.getRamp() != 0 ? 'L' : 'A')
											+ ((topLeft.getLayerHeight() - base) * (topLeft.getRamp() != 0 ? -4 : 1)))
									+ (char) ((topRight.getRamp() != 0 ? 'L' : 'A')
											+ ((topRight.getLayerHeight() - base) * (topRight.getRamp() != 0 ? -4 : 1)))
									+ (char) ((bottomRight.getRamp() != 0 ? 'L' : 'A')
											+ ((bottomRight.getLayerHeight() - base)
													* (bottomRight.getRamp() != 0 ? -4 : 1)));

							fileName = "Doodads\\Terrain\\CliffTrans\\CliffTrans" + fileName + "0.mdx";

							if (this.dataSource.has(fileName)) {
								if (!this.pathToCliff.containsKey(fileName)) {
									this.cliffMeshes.add(new CliffMesh(fileName, this.dataSource, Gdx.gl30));
									this.pathToCliff.put(fileName, this.cliffMeshes.size() - 1);
								}

								for (int ji = this.cliffs.size(); ji-- > 0;) {
									final IVec3 pos = this.cliffs.get(ji);
									if ((pos.x == (i + ((bo ? 1 : 0) * (facingLeft ? 0 : 1))))
											&& (pos.y == (j - ((br ? 1 : 0) * (facingDown ? 1 : 0))))) {
										this.cliffs.remove(ji);
										break;
									}
								}

								this.cliffs.add(new IVec3((i + ((bo ? 1 : 0) * (facingDown ? 0 : 1))),
										(j - ((br ? 1 : 0) * (facingDown ? 1 : 0))), this.pathToCliff.get(fileName)));
								bottomLeft.romp = true;

								this.corners[i + ((facingLeft ? -1 : 1) * (bo ? 1 : 0))][j
										+ ((facingDown ? -1 : 1) * (br ? 1 : 0))].romp = true;

								continue;
							}
						}
					}

					if (isCornerRampEntrance(i, j)) {
						continue;
					}

					// Ramps move 1 right/down in some cases and thus their area is one bigger to
					// the top and left.
					if (!area.contains(i, j)) {
						continue;
					}

					// Cliff model path

					String fileName = "" + (char) (('A' + bottomLeft.getLayerHeight()) - base)
							+ (char) (('A' + topLeft.getLayerHeight()) - base)
							+ (char) (('A' + topRight.getLayerHeight()) - base)
							+ (char) (('A' + bottomRight.getLayerHeight()) - base);

					if ("AAAA".equals(fileName)) {
						continue;
					}

					// Clamp to within max variations
					fileName += Variations.getCliffVariation("Cliffs", fileName, bottomLeft.getCliffVariation());
					if (!this.pathToCliff.containsKey(fileName)) {
						throw new IllegalArgumentException("No such pathToCliff entry: " + fileName);
					}
					this.cliffs.add(new IVec3(i, j, this.pathToCliff.get(fileName)));
				}
			}
		}

	}

	private void updateGroundTextures(final Rectangle area) {
		final Rectangle adjusted = new Rectangle(area.x - 1, area.y - 1, area.width + 2, area.height + 2);
		final Rectangle updateArea = new Rectangle();
		Intersector.intersectRectangles(new Rectangle(0, 0, this.columns - 1, this.rows - 1), adjusted, updateArea);

		for (int j = (int) (updateArea.getY()); j <= (int) ((updateArea.getY() + updateArea.getHeight()) - 1); j++) {
			for (int i = (int) (updateArea.getX()); i <= (int) ((updateArea.getX() + updateArea.getWidth()) - 1); i++) {
				getTextureVariations(i, j, this.groundTextureList, ((j * (this.columns - 1)) + i) * 4);

				if (this.corners[i][j].cliff || this.corners[i][j].romp) {
					if (isCornerRampEntrance(i, j)) {
						continue;
					}
					this.groundTextureList[(((j * (this.columns - 1)) + i) * 4) + 3] |= 0b1000000000000000;
				}
			}
		}

		uploadGroundTexture();
	}

	private void uploadGroundTexture() {
		if (this.groundTextureData != -1) {
			Gdx.gl30.glBindTexture(GL30.GL_TEXTURE_2D, this.groundTextureData);
			Gdx.gl30.glTexSubImage2D(GL30.GL_TEXTURE_2D, 0, 0, 0, this.columns - 1, this.rows - 1, GL30.GL_RGBA_INTEGER,
					GL30.GL_UNSIGNED_SHORT, RenderMathUtils.wrapShort(this.groundTextureList));
		}
	}

	/// The 4 ground textures of the tilepoint. First 5 bits are which texture array
	/// to use and the next 5 bits are which subtexture to use
	private void getTextureVariations(final int x, final int y, final short[] out, final int outStartOffset) {
		final int bottomLeft = realTileTexture(x, y);
		final int bottomRight = realTileTexture(x + 1, y);
		final int topLeft = realTileTexture(x, y + 1);
		final int topRight = realTileTexture(x + 1, y + 1);

		final TreeSet<Integer> set = new TreeSet<>();
		set.add(bottomLeft);
		set.add(bottomRight);
		set.add(topLeft);
		set.add(topRight);
		Arrays.fill(out, outStartOffset, outStartOffset + 4, (short) 17);
		int component = outStartOffset + 1;

		final Iterator<Integer> iterator = set.iterator();
		iterator.hasNext();
		final short firstValue = iterator.next().shortValue();
		out[outStartOffset] = (short) (firstValue
				+ (getVariation(firstValue, this.corners[x][y].getGroundVariation()) << 5));

		int index;
		while (iterator.hasNext()) {
			index = 0;
			final int texture = iterator.next().intValue();
			index |= (bottomRight == texture ? 1 : 0) << 0;
			index |= (bottomLeft == texture ? 1 : 0) << 1;
			index |= (topRight == texture ? 1 : 0) << 2;
			index |= (topLeft == texture ? 1 : 0) << 3;

			out[component++] = (short) (texture + (index << 5));
		}
	}

	private int realTileTexture(final int x, final int y) {
		ILoop: for (int i = -1; i < 1; i++) {
			for (int j = -1; j < 1; j++) {
				if (((x + i) >= 0) && ((x + i) < this.columns) && ((y + j) >= 0) && ((y + j) < this.rows)) {
					if (this.corners[x + i][y + j].cliff) {
						if (((x + i) < (this.columns - 1)) && ((y + j) < (this.rows - 1))) {
							final RenderCorner bottomLeft = this.corners[x + i][y + j];
							final RenderCorner bottomRight = this.corners[x + i + 1][y + j];
							final RenderCorner topLeft = this.corners[x + i][y + j + 1];
							final RenderCorner topRight = this.corners[x + i + 1][y + j + 1];

							if ((bottomLeft.getRamp() != 0) && (topLeft.getRamp() != 0) && (bottomRight.getRamp() != 0)
									&& (topRight.getRamp() != 0) && (!bottomLeft.romp) && (!bottomRight.romp)
									&& (!topLeft.romp) && (!topRight.romp)) {
								break ILoop;
							}
						}
					}
					if (this.corners[x + i][y + j].romp || this.corners[x + i][y + j].cliff) {
						int texture = this.corners[x + i][y + j].getCliffTexture();
						// Number 15 seems to be something
						if (texture == 15) {
							texture -= 14;
						}

						return this.cliffToGroundTexture.get(texture);
					}
				}
			}
		}

		if (this.corners[x][y].getBlight() != 0) {
			return this.blightTextureIndex;
		}

		return this.corners[x][y].getGroundTexture();
	}

	private boolean isCornerRampEntrance(final int x, final int y) {
		if ((x == this.columns) || (y == this.rows)) {
			return false;
		}

		final RenderCorner bottomLeft = this.corners[x][y];
		final RenderCorner bottomRight = this.corners[x + 1][y];
		final RenderCorner topLeft = this.corners[x][y + 1];
		final RenderCorner topRight = this.corners[x + 1][y + 1];

		return (bottomLeft.getRamp() != 0) && (topLeft.getRamp() != 0) && (bottomRight.getRamp() != 0)
				&& (topRight.getRamp() != 0) && !((bottomLeft.getLayerHeight() == topRight.getLayerHeight())
						&& (topLeft.getLayerHeight() == bottomRight.getLayerHeight()));
	}

	private static void loadWaterColor(final float[] out, final String prefix, final Element waterInfo) {
		for (int i = 0; i < colorTags.length; i++) {
			final String colorTag = colorTags[i];
			out[i] = waterInfo.getFieldFloatValue(prefix + "_" + colorTag) / 255f;
		}
	}

	public short getVariation(final int groundTexture, final int variation) {
		final GroundTexture texture = this.groundTextures.get(groundTexture);

		// Extended ?
		if (texture.extended) {
			if (variation <= 15) {
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

	public void update() {
		this.waterIndex += this.waterIncreasePerFrame;

		if (this.waterIndex >= this.waterTextureCount) {
			this.waterIndex = 0;
		}
	}

	public void renderGround() {
		// Render tiles
//		this.groundShader.begin();
		this.webGL.useShaderProgram(this.groundShader);

		final GL30 gl = Gdx.gl30;
		gl.glDisable(GL30.GL_BLEND);

		gl.glUniformMatrix4fv(this.groundShader.getUniformLocation("MVP"), 1, false,
				this.camera.viewProjectionMatrix.val, 0);
		gl.glUniform1i(this.groundShader.getUniformLocation("show_pathing_map"), this.viewer.renderPathing);
		gl.glUniform1i(this.groundShader.getUniformLocation("show_lighting"), this.viewer.renderLighting);
		gl.glUniform1i(this.groundShader.getUniformLocation("height_texture"), 0);
		gl.glUniform1i(this.groundShader.getUniformLocation("height_cliff_texture"), 1);
		gl.glUniform1i(this.groundShader.getUniformLocation("terrain_texture_list"), 2);
		gl.glUniform1f(this.groundShader.getUniformLocation("centerOffsetX"), this.centerOffset[0]);
		gl.glUniform1f(this.groundShader.getUniformLocation("centerOffsetY"), this.centerOffset[1]);

		gl.glActiveTexture(GL30.GL_TEXTURE0);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundHeight);

		gl.glActiveTexture(GL30.GL_TEXTURE1);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeight);

		gl.glActiveTexture(GL30.GL_TEXTURE2);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundTextureData);

		for (int i = 0; i < this.groundTextures.size(); i++) {
			gl.glActiveTexture(GL30.GL_TEXTURE3 + i);
			gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.groundTextures.get(i).id);
		}

//		gl.glActiveTexture(GL30.GL_TEXTURE20, /*pathingMap.getTextureStatic()*/);
//		gl.glActiveTexture(GL30.GL_TEXTURE21, /*pathingMap.getTextureDynamic()*/);

//		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, Shapes.INSTANCE.vertexBuffer);
		gl.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 0, 0);

		gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, Shapes.INSTANCE.indexBuffer);
		gl.glDrawElementsInstanced(GL30.GL_TRIANGLES, Shapes.INSTANCE.quadIndices.length * 3, GL30.GL_UNSIGNED_INT, 0,
				(this.columns - 1) * (this.rows - 1));

//		gl.glDisableVertexAttribArray(0);

		gl.glEnable(GL30.GL_BLEND);

	}

	public void renderWater() {
		// Render water
		this.webGL.useShaderProgram(this.waterShader);

		final GL30 gl = Gdx.gl30;
		gl.glEnable(GL30.GL_BLEND);

		gl.glUniformMatrix4fv(0, 1, false, this.camera.viewProjectionMatrix.val, 0);
		gl.glUniform4fv(1, 1, this.minShallowColor, 0);
		gl.glUniform4fv(2, 1, this.maxShallowColor, 0);
		gl.glUniform4fv(3, 1, this.minDeepColor, 0);
		gl.glUniform4fv(4, 1, this.maxDeepColor, 0);
		gl.glUniform1f(5, this.waterHeightOffset);
		gl.glUniform1i(6, (int) this.waterIndex);
		gl.glUniform1f(this.waterShader.getUniformLocation("centerOffsetX"), this.centerOffset[0]);
		gl.glUniform1f(this.waterShader.getUniformLocation("centerOffsetY"), this.centerOffset[1]);

		gl.glActiveTexture(GL30.GL_TEXTURE0);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterHeight);
		gl.glActiveTexture(GL30.GL_TEXTURE1);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundCornerHeight);
		gl.glActiveTexture(GL30.GL_TEXTURE2);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.waterExists);
		gl.glActiveTexture(GL30.GL_TEXTURE3);
		gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.waterTextureArray);

		gl.glBindBuffer(GL30.GL_ARRAY_BUFFER, Shapes.INSTANCE.vertexBuffer);
		gl.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 0, 0);

		gl.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, Shapes.INSTANCE.indexBuffer);
		gl.glDrawElementsInstanced(GL30.GL_TRIANGLES, Shapes.INSTANCE.quadIndices.length * 3, GL30.GL_UNSIGNED_INT, 0,
				(this.columns - 1) * (this.rows - 1));

		gl.glEnable(GL30.GL_BLEND);
	}

	public void renderCliffs() {

		// Render cliffs
		for (final IVec3 i : this.cliffs) {
			final RenderCorner bottomLeft = this.corners[i.x][i.y];
			final RenderCorner bottomRight = this.corners[i.x + 1][i.y];
			final RenderCorner topLeft = this.corners[i.x][i.y + 1];
			final RenderCorner topRight = this.corners[i.x + 1][i.y + 1];

			final float min = Math.min(Math.min(bottomLeft.getLayerHeight() - 2, bottomRight.getLayerHeight() - 2),
					Math.min(topLeft.getLayerHeight() - 2, topRight.getLayerHeight() - 2));

			fourComponentHeap[0] = i.x;
			fourComponentHeap[1] = i.y;
			fourComponentHeap[2] = min;
			fourComponentHeap[3] = bottomLeft.getCliffTexture();
			this.cliffMeshes.get(i.z).renderQueue(fourComponentHeap);
		}

		this.cliffShader.begin();

		final GL30 gl = Gdx.gl30;

		// WC3 models are 128x too large
		tempMatrix.set(this.camera.viewProjectionMatrix);
		tempMatrix.scale(1 / 128f, 1 / 128f, 1 / 128f);
		gl.glUniformMatrix4fv(0, 1, false, tempMatrix.val, 0);
		gl.glUniform1i(1, this.viewer.renderPathing);
		gl.glUniform1i(2, this.viewer.renderLighting);

		gl.glActiveTexture(GL30.GL_TEXTURE0);
		gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.cliffTextureArray);
		gl.glActiveTexture(GL30.GL_TEXTURE1);
		gl.glBindTexture(GL30.GL_TEXTURE_2D, this.groundHeight);
//		gl.glActiveTexture(GL30.GL_TEXTURE2);
		for (final CliffMesh i : this.cliffMeshes) {
			i.render();
		}
	}

//	public Vector3 groundNormal(final Vector3 out, int x, int y) {
//		final float[] centerOffset = this.centerOffset;
//		final int[] mapSize = this.mapSize;
//
//		x = (int) ((x - centerOffset[0]) / 128);
//		y = (int) ((y - centerOffset[1]) / 128);
//
//		final int cellX = x;
//		final int cellY = y;
//
//		// See if this coordinate is in the map
//
//		if ((cellX >= 0) && (cellX < (mapSize[0] - 1)) && (cellY >= 0) && (cellY < (mapSize[1] - 1))) {
//			// See http://gamedev.stackexchange.com/a/24574
//			final Corner[][] corners = this.corners;
//			final float bottomLeft = corners[cellY][cellX].getGroundHeight();
//			final float bottomRight = corners[cellY][cellX + 1].getGroundHeight();
//			final float topLeft = corners[cellY + 1][cellX].getGroundHeight();
//			final float topRight = corners[cellY + 1][cellX + 1].getGroundHeight();
//			final int sqX = x - cellX;
//			final int sqY = y - cellY;
//
//			if ((sqX + sqY) < 1) {
//				normalHeap1.set(1, 0, bottomRight - bottomLeft);
//				normalHeap2.set(0, 1, topLeft - bottomLeft);
//			}
//			else {
//				normalHeap1.set(-1, 0, topRight - topLeft);
//				normalHeap2.set(0, 1, topRight - bottomRight);
//			}
//
//			out.set(normalHeap1.crs(normalHeap2)).nor();
//		}
//		else {
//			out.set(0, 0, 1);
//		}
//
//		return out;
//	}

	private static final class UnloadedTexture {
		private final int width;
		private final int height;
		private final Buffer data;

		public UnloadedTexture(final int width, final int height, final Buffer data) {
			this.width = width;
			this.height = height;
			this.data = data;
		}

	}
}

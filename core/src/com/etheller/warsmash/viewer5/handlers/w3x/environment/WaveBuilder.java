package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3iFlags;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class WaveBuilder {

	private final int[] mapSize;
	private final DataTable waterTable;
	private final War3MapViewer viewer;
	private final RenderCorner[][] corners;
	private final float[] centerOffset;
	private final float waterHeightOffset;
	private float[] locations;

	private final Map<String, MdxModel> models;
	private final War3MapW3e w3eFile;
	private final War3MapW3i w3iFile;

	public WaveBuilder(final int[] mapSize, final DataTable waterTable, final War3MapViewer viewer,
			final RenderCorner[][] corners, final float[] centerOffset, final float waterHeightOffset,
			final War3MapW3e w3eFile, final War3MapW3i w3iFile) {
		this.mapSize = mapSize;
		this.waterTable = waterTable;
		this.viewer = viewer;
		this.corners = corners;
		this.centerOffset = centerOffset;
		this.waterHeightOffset = waterHeightOffset;
		this.w3eFile = w3eFile;
		this.w3iFile = w3iFile;
		this.models = new HashMap<>();
	}

	public void createWaves(final Terrain terrain) {
		final int columns = this.mapSize[0];
		final int rows = this.mapSize[1];
		final float wavesDepth = 25f / 128f;
		final char tileset = this.w3eFile.getTileset();
		final Element waterRow = this.waterTable.get(tileset + "Sha");

		final long wavesCliff = (this.w3iFile.getFlags() & War3MapW3iFlags.SHOW_WATER_WAVES_ON_CLIFF_SHORES);
		final long wavesRolling = (this.w3iFile.getFlags() & War3MapW3iFlags.SHOW_WATER_WAVES_ON_ROLLING_SHORES);

		final String shoreline = waterRow.getField("shoreDir") + "\\" + waterRow.getField("shoreSFile") + "\\"
				+ waterRow.getField("shoreSFile") + "0.mdx";
		final String outsideCorner = waterRow.getField("shoreDir") + "\\" + waterRow.getField("shoreOCFile") + "\\"
				+ waterRow.getField("shoreOCFile") + "0.mdx";
		final String insideCorner = waterRow.getField("shoreDir") + "\\" + waterRow.getField("shoreICFile") + "\\"
				+ waterRow.getField("shoreICFile") + "0.mdx";
//		final String shoreline = "Buildings\\Other\\TempArtB\\TempArtB.mdx";
//		final String outsideCorner = "Buildings\\Other\\TempArtB\\TempArtB.mdx";
//		final String insideCorner = "Buildings\\Other\\TempArtB\\TempArtB.mdx";

		this.locations = new float[3];

		for (int y = 0; y < (rows - 1); ++y) {
			for (int x = 0; x < (columns - 1); ++x) {
				final RenderCorner a = this.corners[x][y];
				final RenderCorner b = this.corners[x + 1][y];
				final RenderCorner c = this.corners[x + 1][y + 1];
				final RenderCorner d = this.corners[x][y + 1];
				if ((a.getWater() != 0) || (b.getWater() != 0) || (c.getWater() != 0) || (d.getWater() != 0)) {
					final boolean isCliff = (a.getLayerHeight() != b.getLayerHeight())
							|| (a.getLayerHeight() != c.getLayerHeight()) || (a.getLayerHeight() != d.getLayerHeight());
					if (isCliff && (wavesCliff == 0)) {
						continue;
					}
					if (!isCliff && (wavesRolling == 0)) {
						continue;
					}
					final int ad = (a.depth > wavesDepth) ? 1 : 0;
					final int bd = (b.depth > wavesDepth) ? 1 : 0;
					final int cd = (c.depth > wavesDepth) ? 1 : 0;
					final int dd = (d.depth > wavesDepth) ? 1 : 0;
					final int count = ad + bd + cd + dd;
					this.locations[0] = (x * 128.0f) + this.centerOffset[0] + 64.0f;
					this.locations[1] = (y * 128.0f) + this.centerOffset[1] + 64.0f;
					this.locations[2] = ((((a.getWaterHeight() + b.getWaterHeight() + c.getWaterHeight()
							+ d.getWaterHeight()) / 4f) + this.waterHeightOffset) * 128.0f) + 1.0f;
					if (count == 1) {
						addModelInstance(terrain, insideCorner, rotation(ad, bd, cd/* , dd */) - ((3 * Math.PI) / 4));
					}
					else if (count == 2) {
						final double rot = rotation2(ad, bd, cd, dd);
						if (!Double.isNaN(rot)) {
							addModelInstance(terrain, shoreline, rot);
						}
					}
					else if (count == 3) {
						addModelInstance(terrain, outsideCorner,
								rotation(1 ^ ad, 1 ^ bd, 1 ^ cd/* , 1 ^ dd */) + ((5 * Math.PI) / 4));
					}
				}
			}
		}
	}

	private void addModelInstance(final Terrain terrain, final String path, final double rotation) {
		if (!this.models.containsKey(path)) {
			this.models.put(path,
					(MdxModel) this.viewer.load(path, this.viewer.wc3PathSolver, this.viewer.solverParams));
		}
		final MdxModel model = this.models.get(path);
		final MdxComplexInstance instance = (MdxComplexInstance) model.addInstance();
		instance.setLocation(this.locations);
		instance.setLocalRotation(new Quaternion().setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, (float) rotation));
		instance.setScene(this.viewer.worldScene);
		if (!terrain.inPlayableArea(this.locations[0], this.locations[1])) {
			instance.setVertexColor(new float[] { 51 / 255f, 51 / 255f, 51 / 255f, 1.0f });
		}
		this.viewer.standOnRepeat(instance);
	}

	private static double rotation(final int a, final int b, final int c) {
		if (a != 0) {
			return (-3 * Math.PI) / 4;
		}
		if (b != 0) {
			return -Math.PI / 4;
		}
		if (c != 0) {
			return Math.PI / 4;
		}
		return (3 * Math.PI) / 4;
	}

	private static double rotation2(final int a, final int b, final int c, final int d) {
		if ((a != 0) && (b != 0)) {
			return -Math.PI / 2;
		}
		if ((b != 0) && (c != 0)) {
			return 0;
		}
		if ((c != 0) && (d != 0)) {
			return Math.PI / 2;
		}
		if ((a != 0) && (d != 0)) {
			return Math.PI;
		}
		return Double.NaN;
	}
}

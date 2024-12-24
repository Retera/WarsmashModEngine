package com.etheller.warsmash.viewer5.handlers.w3x;

import java.awt.image.BufferedImage;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public class TerrainDoodad {
	private static final float[] locationHeap = new float[3];
	public final MdxComplexInstance instance;
	private final GameObject row;

	public TerrainDoodad(final War3MapViewer map, final MdxModel model, final GameObject row,
			final com.etheller.warsmash.parsers.w3x.doo.TerrainDoodad doodad, final BufferedImage pathingTextureImage) {
		final float[] centerOffset = map.terrain.centerOffset;
		final MdxComplexInstance instance = (MdxComplexInstance) model.addInstance(0);

		final int textureWidth = pathingTextureImage.getWidth();
		final int textureHeight = pathingTextureImage.getHeight();
		final int textureWidthTerrainCells = textureWidth / 4;
		final int textureHeightTerrainCells = textureHeight / 4;
		final int minCellX = ((int) doodad.getLocation()[0]);
		final int minCellY = ((int) doodad.getLocation()[1]);
		locationHeap[0] = ((minCellX * 128) + (textureWidthTerrainCells * 64) + centerOffset[0]);
		locationHeap[1] = ((minCellY * 128) + (textureHeightTerrainCells * 64) + centerOffset[1]);

		instance.move(locationHeap);
		instance.rotate(new Quaternion().setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z,
				(float) Math.toRadians(row.readSLKTagFloat("fixedRot"))));
		instance.setScene(map.worldScene);

		this.instance = instance;
		this.row = row;
	}
}

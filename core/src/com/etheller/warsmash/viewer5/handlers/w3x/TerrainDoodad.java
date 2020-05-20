package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxSimpleInstance;

public class TerrainDoodad {
	private static final float[] locationHeap = new float[3];
	public final MdxSimpleInstance instance;
	private final MutableGameObject row;

	public TerrainDoodad(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final com.etheller.warsmash.parsers.w3x.doo.TerrainDoodad doodad) {
		final float[] centerOffset = map.terrain.centerOffset;
		final MdxSimpleInstance instance = (MdxSimpleInstance) model.addInstance(1);

		locationHeap[0] = (doodad.getLocation()[0] * 128) + centerOffset[0] + 128;
		locationHeap[0] = (doodad.getLocation()[1] * 128) + centerOffset[1] + 128;

		instance.move(locationHeap);
		instance.rotate(new Quaternion().setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, row.readSLKTagFloat("fixedRot")));
		instance.setScene(map.worldScene);

		this.instance = instance;
		this.row = row;
	}
}
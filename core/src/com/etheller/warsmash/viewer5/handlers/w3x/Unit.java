package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.util.MappedDataRow;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public class Unit {
	private static final float[] heapZ = new float[3];
	public final MdxComplexInstance instance;
	public final MappedDataRow row;

	public Unit(final War3MapViewer map, final MdxModel model, final MappedDataRow row,
			final com.etheller.warsmash.parsers.w3x.unitsdoo.Unit unit) {
		final MdxComplexInstance instance = (MdxComplexInstance) model.addInstance();

		instance.move(unit.getLocation());
		instance.rotateLocal(new Quaternion().setFromAxis(RenderMathUtils.VEC3_UNIT_Z, unit.getAngle()));
		instance.scale(unit.getScale());
		instance.setTeamColor(unit.getPlayer());
		instance.setScene(map.worldScene);

		if (row != null) {
			heapZ[0] = (((Number) row.get("moveHeight")).floatValue());

			instance.move(heapZ);
			instance.setVertexColor(new float[] { ((Number) row.get("red")).intValue() / 255f,
					((Number) row.get("green")).intValue() / 255f, ((Number) row.get("blue")).intValue() / 255f });
			instance.uniformScale(((Number) row.get("modelScale")).floatValue());
		}

		this.instance = instance;
		this.row = row;
	}
}

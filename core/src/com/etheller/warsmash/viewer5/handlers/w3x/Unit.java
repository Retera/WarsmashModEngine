package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public class Unit {
	private static final float[] heapZ = new float[3];
	public final MdxComplexInstance instance;
	public final MutableGameObject row;

	public Unit(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final com.etheller.warsmash.parsers.w3x.unitsdoo.Unit unit) {
		final MdxComplexInstance instance = (MdxComplexInstance) model.addInstance();

		instance.move(unit.getLocation());
		float angle;
		if ((row != null) && row.readSLKTagBoolean("isBldg")) {
			angle = (float) Math.toRadians(270.0f);
		}
		else {
			angle = unit.getAngle();
		}
//		instance.localRotation.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle);
		instance.rotate(new Quaternion().setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle));
		instance.scale(unit.getScale());
		instance.setTeamColor(unit.getPlayer());
		instance.setScene(map.worldScene);

		if (row != null) {
			heapZ[2] = row.readSLKTagFloat("moveHeight");

			instance.move(heapZ);
			instance.setVertexColor(new float[] { (row.readSLKTagInt("red")) / 255f,
					(row.readSLKTagInt("green")) / 255f, (row.readSLKTagInt("blue")) / 255f });
			instance.uniformScale(row.readSLKTagFloat("modelScale"));

		}

		this.instance = instance;
		this.row = row;
	}
}

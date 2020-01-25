package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public class Unit {
	private static final War3ID IS_BLDG = War3ID.fromString("ubdg");
	private static final War3ID RED = War3ID.fromString("uclr");
	private static final War3ID GREEN = War3ID.fromString("uclg");
	private static final War3ID BLUE = War3ID.fromString("uclb");
	private static final War3ID MODEL_SCALE = War3ID.fromString("usca");
	private static final War3ID MOVE_HEIGHT = War3ID.fromString("umvh");
	private static final War3ID ITEM_MODEL_SCALE = War3ID.fromString("isca");
	private static final War3ID ITEM_RED = War3ID.fromString("iclr");
	private static final War3ID ITEM_GREEN = War3ID.fromString("iclg");
	private static final War3ID ITEM_BLUE = War3ID.fromString("iclb");
	private static final float[] heapZ = new float[3];
	public final MdxComplexInstance instance;
	public final MutableGameObject row;

	public Unit(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final com.etheller.warsmash.parsers.w3x.unitsdoo.Unit unit, final WorldEditorDataType type) {
		final MdxComplexInstance instance = (MdxComplexInstance) model.addInstance();

		instance.move(unit.getLocation());
		float angle;
		if ((row != null) && row.getFieldAsBoolean(IS_BLDG, 0)) {
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
			heapZ[2] = row.getFieldAsFloat(MOVE_HEIGHT, 0);

			instance.move(heapZ);
			War3ID red;
			War3ID green;
			War3ID blue;
			War3ID scale;
			if (type == WorldEditorDataType.UNITS) {
				scale = MODEL_SCALE;
				red = RED;
				green = GREEN;
				blue = BLUE;
			}
			else {
				scale = ITEM_MODEL_SCALE;
				red = ITEM_RED;
				green = ITEM_GREEN;
				blue = ITEM_BLUE;
			}
			instance.setVertexColor(new float[] { (row.getFieldAsInteger(red, 0)) / 255f,
					(row.getFieldAsInteger(green, 0)) / 255f, (row.getFieldAsInteger(blue, 0)) / 255f });
			instance.uniformScale(row.getFieldAsFloat(scale, 0));

		}

		this.instance = instance;
		this.row = row;
	}
}

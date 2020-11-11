package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSoundset;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class RenderItem {
	private static final War3ID ITEM_MODEL_SCALE = War3ID.fromString("isca");
	private static final War3ID ITEM_RED = War3ID.fromString("iclr");
	private static final War3ID ITEM_GREEN = War3ID.fromString("iclg");
	private static final War3ID ITEM_BLUE = War3ID.fromString("iclb");
	public final MdxComplexInstance instance;
	public final MutableGameObject row;
	public final float[] location = new float[3];
	public float radius;
	public UnitSoundset soundset;
	public final MdxModel portraitModel;

	public RenderItem(final War3MapViewer map, final MdxModel model, final MutableGameObject row, final float x,
			final float y, final float z, final float angle, final UnitSoundset soundset,
			final MdxModel portraitModel) {
		this.portraitModel = portraitModel;
		final MdxComplexInstance instance = (MdxComplexInstance) model.addInstance();

		this.location[0] = x;
		this.location[1] = y;
		this.location[2] = z;
		instance.move(this.location);
//		instance.localRotation.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle);
		instance.rotate(new Quaternion().setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle));
		instance.setScene(map.worldScene);

		if (row != null) {
			War3ID red;
			War3ID green;
			War3ID blue;
			War3ID scale;
			scale = ITEM_MODEL_SCALE;
			red = ITEM_RED;
			green = ITEM_GREEN;
			blue = ITEM_BLUE;
			instance.setVertexColor(new float[] { (row.getFieldAsInteger(red, 0)) / 255f,
					(row.getFieldAsInteger(green, 0)) / 255f, (row.getFieldAsInteger(blue, 0)) / 255f });
			instance.uniformScale(row.getFieldAsFloat(scale, 0));

			this.radius = 1 * 36;
		}

		this.instance = instance;
		this.row = row;
		this.soundset = soundset;
	}
}

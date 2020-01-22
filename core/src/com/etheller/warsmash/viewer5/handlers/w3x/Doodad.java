package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public class Doodad {
	private final ModelInstance instance;
	private final MutableGameObject row;

	public Doodad(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final com.etheller.warsmash.parsers.w3x.doo.Doodad doodad) {
		final boolean isSimple = row.readSLKTagBoolean("lightweight");
		ModelInstance instance;

		if (isSimple) {
			instance = model.addInstance(1);
		}
		else {
			instance = model.addInstance();
		}

		instance.move(doodad.getLocation());
		instance.rotate(new Quaternion().setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, doodad.getAngle()));
		instance.scale(doodad.getScale());
		instance.setScene(map.worldScene);

		this.instance = instance;
		this.row = row;
	}
}

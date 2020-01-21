package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.util.MappedDataRow;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.BatchedInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public class Doodad {
	private final BatchedInstance instance;
	private final MappedDataRow row;

	public Doodad(final War3MapViewer map, final MdxModel model, final MappedDataRow row,
			final com.etheller.warsmash.parsers.w3x.doo.Doodad doodad) {
		final boolean isSimple = ((Number) row.get("lightweight")).intValue() == 1;
		BatchedInstance instance;

		if (isSimple) {
			instance = (BatchedInstance) model.addInstance(1);
		}
		else {
			instance = (BatchedInstance) model.addInstance();
		}

		instance.move(doodad.getLocation());
		instance.rotateLocal(new Quaternion().setFromAxis(RenderMathUtils.VEC3_UNIT_Z, doodad.getAngle()));
		instance.scale(doodad.getScale());
		instance.setScene(map.worldScene);

		this.instance = instance;
		this.row = row;
	}
}

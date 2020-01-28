package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public class Doodad {
	private static final War3ID TEX_FILE = War3ID.fromString("btxf");
	private static final War3ID TEX_ID = War3ID.fromString("btxi");
	public final ModelInstance instance;
	private final MutableGameObject row;

	public Doodad(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final com.etheller.warsmash.parsers.w3x.doo.Doodad doodad, final WorldEditorDataType type) {
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
		if (type == WorldEditorDataType.DOODADS) {
			final float defScale = row.readSLKTagFloat("defScale");
			instance.uniformScale(defScale);
		}
		if (type == WorldEditorDataType.DESTRUCTIBLES) {
			// TODO destructables need to be their own type, game simulation, etc
			String replaceableTextureFile = row.getFieldAsString(TEX_FILE, 0);
			final int replaceableTextureId = row.getFieldAsInteger(TEX_ID, 0);
			if ((replaceableTextureFile != null) && (replaceableTextureFile.length() > 1)) {
				if (!replaceableTextureFile.toLowerCase().endsWith(".blp")) {
					replaceableTextureFile += ".blp";
				}
				instance.setReplaceableTexture(replaceableTextureId, replaceableTextureFile);
			}
		}
		instance.setScene(map.worldScene);

		this.instance = instance;
		this.row = row;
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.BuildingShadow;

public class RenderDestructable extends RenderDoodad {
	private static final War3ID TEX_FILE = War3ID.fromString("btxf");
	private static final War3ID TEX_ID = War3ID.fromString("btxi");

	private final float life;
	public Rectangle walkableBounds;

	public RenderDestructable(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final com.etheller.warsmash.parsers.w3x.doo.Doodad doodad, final WorldEditorDataType type,
			final float maxPitch, final float maxRoll, final float life, final BuildingShadow destructableShadow) {
		super(map, model, row, doodad, type, maxPitch, maxRoll);
		this.life = life;
		String replaceableTextureFile = row.getFieldAsString(TEX_FILE, 0);
		final int replaceableTextureId = row.getFieldAsInteger(TEX_ID, 0);
		if ((replaceableTextureFile != null) && (replaceableTextureFile.length() > 1)) {
			final int dotIndex = replaceableTextureFile.lastIndexOf('.');
			if (dotIndex != -1) {
				replaceableTextureFile = replaceableTextureFile.substring(0, dotIndex);
			}
			replaceableTextureFile += ".blp";
			this.instance.setReplaceableTexture(replaceableTextureId, replaceableTextureFile);
		}
	}

	@Override
	public PrimaryTag getAnimation() {
		if (this.life <= 0) {
			return PrimaryTag.DEATH;
		}
		return super.getAnimation();
	}
}

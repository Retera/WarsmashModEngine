package com.etheller.warsmash.viewer5.handlers.w3x;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;

public class Destructable extends Doodad {

	private final float life;

	public Destructable(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final com.etheller.warsmash.parsers.w3x.doo.Doodad doodad, final WorldEditorDataType type,
			final float maxPitch, final float maxRoll, final float life) {
		super(map, model, row, doodad, type, maxPitch, maxRoll);
		this.life = life;
	}

	@Override
	public PrimaryTag getAnimation() {
		if (this.life <= 0) {
			return PrimaryTag.DEATH;
		}
		return super.getAnimation();
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.etheller.warsmash.util.War3ID;

public class CItem extends CWidget {

	private final War3ID itemType;

	public CItem(final int handleId, final float x, final float y, final float life, final War3ID itemType) {
		super(handleId, x, y, life);
		this.itemType = itemType;
	}

}

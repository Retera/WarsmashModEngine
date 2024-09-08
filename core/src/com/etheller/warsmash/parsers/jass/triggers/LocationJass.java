package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;

public class LocationJass extends AbilityPointTarget implements CHandle {
	private final int handleId;

	public LocationJass(final float x, final float y, final int handleId) {
		super(x, y);
		this.handleId = handleId;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

}

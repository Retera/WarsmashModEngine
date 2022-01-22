package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.warsmash.util.QuadtreeIntersector;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public final class BuildOnBuildingIntersector implements QuadtreeIntersector<CUnit> {
	private CUnit unitToBuildOn;
	private float x;
	private float y;

	public BuildOnBuildingIntersector reset(final float x, final float y) {
		this.x = x;
		this.y = y;
		this.unitToBuildOn = null;
		return this;
	}

	@Override
	public boolean onIntersect(final CUnit intersectingObject) {
		if (intersectingObject.isHidden() || !intersectingObject.getUnitType().isCanBuildOnMe()) {
			return false;
		}
		if ((Math.abs(this.x - intersectingObject.getX()) <= 0.0001)
				&& (Math.abs(this.y - intersectingObject.getY()) <= 0.0001)) {
			this.unitToBuildOn = intersectingObject;
			return true;
		}
		return false;
	}

	public CUnit getUnitToBuildOn() {
		return this.unitToBuildOn;
	}
}
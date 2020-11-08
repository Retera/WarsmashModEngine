package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public abstract class CAbstractRangedWidgetTargetBehavior extends CAbstractRangedBehavior {

	protected CWidget target;

	public CAbstractRangedWidgetTargetBehavior(final CUnit unit) {
		super(unit);
	}

	protected final CAbstractRangedBehavior innerReset(final CWidget target) {
		this.target = target;
		return innerReset();
	}

	@Override
	protected float getTargetX() {
		return this.target.getX();
	}

	@Override
	protected float getTargetY() {
		return this.target.getY();
	}

	@Override
	protected CBehaviorMove setupMoveBehavior() {
		if ((this.target instanceof CUnit) && !((CUnit) this.target).getUnitType().isBuilding()) {
			return this.unit.getMoveBehavior().reset((CUnit) this.target, this);
		}
		else {
			return this.unit.getMoveBehavior().reset(this.target.getX(), this.target.getY(), this);
		}
	}

}

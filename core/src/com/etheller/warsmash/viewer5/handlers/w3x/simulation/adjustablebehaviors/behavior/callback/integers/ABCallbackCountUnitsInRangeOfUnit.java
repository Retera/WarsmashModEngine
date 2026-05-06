package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackCountUnitsInRangeOfUnit extends ABIntegerCallback {

	private static final Rectangle recycleRect = new Rectangle();
	private ABUnitCallback unit;
	private ABFloatCallback range;

	private int count = 0;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit originUnitTarget = unit.callback(caster, localStore, castId);
		Float rangeVal = range.callback(caster, localStore, castId);

		recycleRect.set(originUnitTarget.getX() - rangeVal, originUnitTarget.getY() - rangeVal, rangeVal * 2,
				rangeVal * 2);
		count = 0;

		localStore.game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
			@Override
			public boolean call(final CUnit enumUnit) {
				if (originUnitTarget.canReach(enumUnit, rangeVal)) {
					count++;
				}
				return false;
			}
		});
		return count;
	}

}

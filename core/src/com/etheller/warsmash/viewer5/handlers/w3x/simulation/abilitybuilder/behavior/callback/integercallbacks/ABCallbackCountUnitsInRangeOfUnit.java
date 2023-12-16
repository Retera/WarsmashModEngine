package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;

public class ABCallbackCountUnitsInRangeOfUnit extends ABIntegerCallback {

	private static final Rectangle recycleRect = new Rectangle();
	private ABUnitCallback unit;
	private ABFloatCallback range;
	
	private int count = 0;
	
	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit originUnitTarget = unit.callback(game, caster, localStore, castId);
		Float rangeVal = range.callback(game, caster, localStore, castId);
		
		recycleRect.set(originUnitTarget.getX() - rangeVal, originUnitTarget.getY() - rangeVal, rangeVal * 2,
				rangeVal * 2);
		count = 0; 
		
		game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
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

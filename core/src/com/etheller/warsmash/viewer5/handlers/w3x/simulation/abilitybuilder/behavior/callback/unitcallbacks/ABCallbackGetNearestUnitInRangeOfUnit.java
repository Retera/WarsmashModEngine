package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.iterstructs.UnitAndRange;

public class ABCallbackGetNearestUnitInRangeOfUnit extends ABUnitCallback {
	private static final Rectangle recycleRect = new Rectangle();

	private ABUnitCallback originUnit;
	private ABFloatCallback range;
	private List<ABCondition> conditions;
	
	@Override
	public CUnit callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit originUnitTarget = originUnit.callback(game, caster, localStore, castId);
		Float rangeVal = range.callback(game, caster, localStore, castId);
		
		final UnitAndRange ur = new UnitAndRange();
		
		recycleRect.set(originUnitTarget.getX() - rangeVal, originUnitTarget.getY() - rangeVal, rangeVal * 2,
				rangeVal * 2);
		game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
			@Override
			public boolean call(final CUnit enumUnit) {
				if (originUnitTarget.canReach(enumUnit, rangeVal)) {
					double dist = originUnitTarget.distance(enumUnit);
					if (ur.getUnit() == null || ur.getRange() > dist) {
						if (conditions != null) {
							boolean result = true;
							localStore.put(ABLocalStoreKeys.MATCHINGUNIT+castId, enumUnit);
							for (ABCondition condition : conditions) {
								result = result && condition.evaluate(game, caster, localStore, castId);
							}
							localStore.remove(ABLocalStoreKeys.MATCHINGUNIT+castId);
							if (result) {
								ur.setRange(dist);
								ur.setUnit(enumUnit);
							}
						} else {
							ur.setRange(dist);
							ur.setUnit(enumUnit);
						}
					}
				}
				return false;
			}
		});
		return ur.getUnit();
	}

}

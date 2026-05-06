package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.iterstructs.ABUnitAndRangeComparator;

public class ABCallbackGetNearestCorpseInRangeOfUnit extends ABUnitCallback {
	private static final Rectangle recycleRect = new Rectangle();

	private ABUnitCallback originUnit;
	private ABFloatCallback range;
	private List<ABBooleanCallback> conditions;

	@Override
	public CUnit callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit originUnitTarget = originUnit.callback(caster, localStore, castId);
		Float rangeVal = range.callback(caster, localStore, castId);

		final ABUnitAndRangeComparator ur = new ABUnitAndRangeComparator();

		recycleRect.set(originUnitTarget.getX() - rangeVal, originUnitTarget.getY() - rangeVal, rangeVal * 2,
				rangeVal * 2);
		localStore.game.getWorldCollision().enumCorpsesInRect(recycleRect, new CUnitEnumFunction() {
			@Override
			public boolean call(final CUnit enumUnit) {
				if (originUnitTarget.canReach(enumUnit, rangeVal)) {
					double dist = originUnitTarget.distance(enumUnit);
					if (ur.getUnit() == null || ur.getRange() > dist) {
						if (conditions != null) {
							boolean result = true;
							localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.MATCHINGUNIT, castId),
									enumUnit);
							for (ABBooleanCallback condition : conditions) {
								result = result && condition.callback(caster, localStore, castId);
							}
							localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.MATCHINGUNIT, castId));
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

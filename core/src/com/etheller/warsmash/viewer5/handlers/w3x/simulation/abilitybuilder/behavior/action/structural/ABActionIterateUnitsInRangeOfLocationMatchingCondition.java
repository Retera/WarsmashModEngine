package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionIterateUnitsInRangeOfLocationMatchingCondition implements ABAction {

	private static final Rectangle recycleRect = new Rectangle();

	private ABLocationCallback location;
	private ABFloatCallback range;
	private List<ABAction> iterationActions;
	private ABCondition condition;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		AbilityPointTarget target = location.callback(game, caster, localStore, castId);
		Float rangeVal = range.callback(game, caster, localStore, castId);
		
		recycleRect.set(target.getX() - rangeVal, target.getY() - rangeVal, rangeVal * 2,
				rangeVal * 2);
		game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
			@Override
			public boolean call(final CUnit enumUnit) {
				if (enumUnit.canReach(target, rangeVal)) {
					localStore.put(ABLocalStoreKeys.MATCHINGUNIT+castId, enumUnit);
					if (condition == null || condition.callback(game, caster, localStore, castId)) {
						localStore.put(ABLocalStoreKeys.ENUMUNIT+castId, enumUnit);
						for (ABAction iterationAction : iterationActions) {
							iterationAction.runAction(game, caster, localStore, castId);
						}
					}
				}
				return false;
			}
		});
		localStore.remove(ABLocalStoreKeys.ENUMUNIT+castId);
		localStore.remove(ABLocalStoreKeys.MATCHINGUNIT+castId);
	}
}

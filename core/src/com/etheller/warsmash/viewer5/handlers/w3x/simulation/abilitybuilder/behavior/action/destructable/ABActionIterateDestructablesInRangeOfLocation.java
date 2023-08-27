package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.destructable;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionIterateDestructablesInRangeOfLocation implements ABAction {

	private static final Rectangle recycleRect = new Rectangle();

	private ABLocationCallback location;
	private ABFloatCallback range;
	private List<ABAction> iterationActions;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		AbilityPointTarget target = location.callback(game, caster, localStore, castId);
		Float rangeVal = range.callback(game, caster, localStore, castId);
		
		recycleRect.set(target.getX() - rangeVal, target.getY() - rangeVal, rangeVal * 2,
				rangeVal * 2);
		game.getWorldCollision().enumDestructablesInRect(recycleRect, new CDestructableEnumFunction() {
			@Override
			public boolean call(final CDestructable enumDest) {
				if (enumDest.distance(target.getX(), target.getY()) < rangeVal) {
					localStore.put(ABLocalStoreKeys.ENUMDESTRUCTABLE+castId, enumDest);
					for (ABAction iterationAction : iterationActions) {
						iterationAction.runAction(game, caster, localStore, castId);
					}
				}
				return false;
			}
		});
		localStore.remove(ABLocalStoreKeys.ENUMDESTRUCTABLE+castId);
	}
}

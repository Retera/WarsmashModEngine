package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.item;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.item.ABItemCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionChargeItem implements ABAction {

	private ABItemCallback item;
	private ABBooleanCallback checkForPerish;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		CItem it = null;
		if (item == null) {
			AbilityBuilderAbility ability = (AbilityBuilderAbility) localStore.get(ABLocalStoreKeys.ABILITY);

			it = ability.getItem();
		} else {
			it = item.callback(game, caster, localStore, castId); 
		}
		
		it.setCharges(it.getCharges() - 1);
		if (checkForPerish != null && checkForPerish.callback(game, caster, localStore, castId) && it.getItemType().isPerishable() && it.getCharges() == 0) {
			it.forceDropIfHeld(game);
			game.removeItem(it);
		}
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.ability;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.GetABAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionStartCooldown implements ABAction {

	private ABIDCallback alias;
	private ABUnitCallback unit;
	private ABFloatCallback cooldown;
	
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(game, caster, localStore, castId);
		}
		if (alias != null) {
			War3ID aliasId = alias.callback(game, caster, localStore, castId);
			if (cooldown != null) {
				theUnit.beginCooldown(game, aliasId, cooldown.callback(game, caster, localStore, castId));
			} else {
				AbilityBuilderActiveAbility abil = theUnit.getAbility(GetABAbilityByRawcodeVisitor.getInstance().reset(aliasId));
				if (abil != null) {
					abil.startCooldown(game, theUnit);
				}
			}
		} else {
			if (cooldown != null) {
				War3ID aliasId = (War3ID) localStore.get(ABLocalStoreKeys.ALIAS);
				theUnit.beginCooldown(game, aliasId, cooldown.callback(game, caster, localStore, castId));
			} else {
				AbilityBuilderActiveAbility abil = (AbilityBuilderActiveAbility) localStore.get(ABLocalStoreKeys.ABILITY);
				abil.startCooldown(game, theUnit);
			}
		}
	}
}

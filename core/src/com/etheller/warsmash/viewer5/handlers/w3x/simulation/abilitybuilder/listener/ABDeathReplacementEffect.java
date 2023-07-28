package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerDamageModResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementEffect;

public class ABDeathReplacementEffect implements CUnitDeathReplacementEffect {

	private AbilityBuilderAbility ability;
	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	public ABDeathReplacementEffect(AbilityBuilderAbility ability, Map<String, Object> localStore, List<ABAction> actions) {
		this.ability = ability;
		this.localStore = localStore;
		this.actions = actions;
	}

	@Override
	public boolean onAttack(CUnit unit, AbilityTarget target,
			CUnitAttackPreDamageListenerDamageModResult damageResult) {
		// TODO Auto-generated method stub
		return false;
	}

}

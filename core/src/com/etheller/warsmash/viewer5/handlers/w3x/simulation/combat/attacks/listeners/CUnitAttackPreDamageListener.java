package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public interface CUnitAttackPreDamageListener {
	public CUnitAttackEffectListenerStacking onAttack(final CSimulation simulation, CUnit attacker,
			AbilityTarget target, AbilityPointTarget attackImpactLocation, CUnitAttack attack,
			CUnitAttackSettings settings, CUnitAttackPreDamageListenerDamageModResult damageResult);
}

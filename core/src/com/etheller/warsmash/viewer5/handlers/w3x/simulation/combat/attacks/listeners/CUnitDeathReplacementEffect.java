package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public interface CUnitDeathReplacementEffect {
	public static final int PRIORITY_MIN = 0;
	public static final int PRIORITY_MAX = 10;
	public boolean onAttack(CUnit unit, AbilityTarget target, CUnitAttackPreDamageListenerDamageModResult damageResult);
}

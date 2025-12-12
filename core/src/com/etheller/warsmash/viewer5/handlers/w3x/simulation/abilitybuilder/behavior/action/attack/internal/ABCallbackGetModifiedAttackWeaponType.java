package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.attack.internal;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABWeaponTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class ABCallbackGetModifiedAttackWeaponType extends ABWeaponTypeCallback {
	
	@Override
	public CWeaponType callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return ((CUnitAttack) localStore.get(ABLocalStoreKeys.THEATTACK+castId)).getWeaponType();
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;

public class ABCallbackRawWeaponType extends ABWeaponTypeCallback {

	private CWeaponType type;
	
	@Override
	public CWeaponType callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return type;
	}

}

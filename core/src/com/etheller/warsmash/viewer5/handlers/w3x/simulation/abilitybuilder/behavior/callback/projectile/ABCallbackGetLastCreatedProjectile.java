package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.projectile;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABCallbackGetLastCreatedProjectile extends ABProjectileCallback {

	@Override
	public CProjectile callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (CProjectile) localStore.get(ABLocalStoreKeys.LASTCREATEDPROJECTILE);
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABCallbackGetProjectileSourceUnit extends ABUnitCallback {

	private ABProjectileCallback proj;
	
	@Override
	public CUnit callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CProjectile p = proj.callback(game, caster, localStore, castId);
		return p.getSource();
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABCallbackGetProjectileSourceUnit extends ABUnitCallback {

	private ABProjectileCallback proj;

	@Override
	public CUnit callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CProjectile p = proj.callback(caster, localStore, castId);
		return p.getSource();
	}

}

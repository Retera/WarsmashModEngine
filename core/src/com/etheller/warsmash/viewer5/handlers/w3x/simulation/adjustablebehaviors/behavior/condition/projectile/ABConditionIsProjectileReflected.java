package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.projectile;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABConditionIsProjectileReflected extends ABBooleanCallback {

	private ABProjectileCallback projectile;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final CProjectile proj = this.projectile.callback(caster, localStore, castId);
		return proj.isReflected();
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "IsProjectileReflected(" + this.projectile.generateJassEquivalent(jassTextGenerator) + ")";
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.projectile;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABActionSetAttackProjectileDamage implements ABSingleAction {

	private ABProjectileCallback projectile;
	private ABFloatCallback damage;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {

		final CProjectile proj = this.projectile.callback(caster, localStore, castId);

		if ((proj != null) && (proj instanceof CAttackProjectile)) {
			final float dm = this.damage.callback(caster, localStore, castId);
			System.err.println("Setting proj damage from " + ((CAttackProjectile) proj).getDamage() + " to " + dm);
			((CAttackProjectile) proj).setDamage(dm);
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "SetAttackProjectileDamage(" + this.projectile.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.damage.generateJassEquivalent(jassTextGenerator) + ")";
	}
}

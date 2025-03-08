package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.projectile;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABActionSetAttackProjectileDamage implements ABSingleAction {

	private ABProjectileCallback projectile;
	private ABFloatCallback damage;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {

		final CProjectile proj = this.projectile.callback(game, caster, localStore, castId);

		if ((proj != null) && (proj instanceof CAttackProjectile)) {
			final float dm = this.damage.callback(game, caster, localStore, castId);
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

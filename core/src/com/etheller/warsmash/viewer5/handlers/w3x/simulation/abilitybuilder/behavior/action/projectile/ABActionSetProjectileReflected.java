package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.projectile;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABActionSetProjectileReflected implements ABSingleAction {

	private ABProjectileCallback projectile;
	private ABBooleanCallback reflected;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {

		final CProjectile proj = this.projectile.callback(game, caster, localStore, castId);

		if (this.reflected != null) {
			proj.setReflected(this.reflected.callback(game, caster, localStore, castId));
		}
		else {
			proj.setReflected(true);
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		String reflectedExpression = "true";
		if (this.reflected != null) {
			reflectedExpression = this.reflected.generateJassEquivalent(jassTextGenerator);
		}
		return "SetProjectileReflected(" + this.projectile.generateJassEquivalent(jassTextGenerator) + ", "
				+ reflectedExpression + ")";
	}
}

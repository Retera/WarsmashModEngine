package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateTemporarySpellEffectAtLocation implements ABSingleAction {

	private ABLocationCallback location;
	private ABFloatCallback facing;
	private ABIDCallback id;
	private CEffectType effectType;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget loc = this.location.callback(game, caster, localStore, castId);
		float dir = 0;
		if (this.facing != null) {
			dir = this.facing.callback(game, caster, localStore, castId);
		}
		game.spawnTemporarySpellEffectOnPoint(loc.getX(), loc.getY(), dir,
				this.id.callback(game, caster, localStore, castId), this.effectType, 0);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		if (this.facing != null) {
			throw new UnsupportedOperationException("AddSpellEffectByIdLoc with facing");
		}
		return "DestroyEffect(AddSpellEffectByIdLoc(" + this.id.generateJassEquivalent(jassTextGenerator)
				+ ", EFFECT_TYPE_" + this.effectType.name() + ", "
				+ this.location.generateJassEquivalent(jassTextGenerator) + ", null))";
	}
}

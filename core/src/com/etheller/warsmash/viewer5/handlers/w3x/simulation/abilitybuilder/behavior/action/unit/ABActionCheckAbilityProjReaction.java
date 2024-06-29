package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionCheckAbilityProjReaction implements ABAction {

	private ABUnitCallback target;
	private ABProjectileCallback projectile;
	private List<ABAction> onHitActions;
	private List<ABAction> onBlockActions;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CUnit tarU = caster;
		if (this.target != null) {
			tarU = this.target.callback(game, caster, localStore, castId);
		}
		if (tarU.checkForAbilityProjReaction(game, caster,
				this.projectile.callback(game, caster, localStore, castId))) {
			if (this.onHitActions != null) {
				for (final ABAction periodicAction : this.onHitActions) {
					periodicAction.runAction(game, caster, localStore, castId);
				}
			}
		}
		else {
			if (this.onBlockActions != null) {
				for (final ABAction periodicAction : this.onBlockActions) {
					periodicAction.runAction(game, caster, localStore, castId);
				}
			}
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String onBlockFunc = jassTextGenerator.createAnonymousFunction(this.onBlockActions,
				"CheckAbilityProjReactionAU_OnBlock");
		final String onHitFunc = jassTextGenerator.createAnonymousFunction(this.onHitActions,
				"CheckAbilityProjReactionAU_OnHit");
		String tarU;
		if (this.target != null) {
			tarU = jassTextGenerator.getCaster();
		}
		else {
			tarU = jassTextGenerator.getCaster();
		}
		return "CheckAbilityProjReactionAU(" + tarU + ", " + this.projectile.generateJassEquivalent(jassTextGenerator)
				+ ", " + jassTextGenerator.functionPointerByName(onHitFunc) + ", "
				+ jassTextGenerator.functionPointerByName(onBlockFunc) + ")";
	}
}

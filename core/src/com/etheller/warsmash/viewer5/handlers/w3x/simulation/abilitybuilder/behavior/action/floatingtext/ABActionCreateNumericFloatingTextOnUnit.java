package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.floatingtext;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class ABActionCreateNumericFloatingTextOnUnit implements ABSingleAction {

	private ABUnitCallback target;
	private TextTagConfigType textType;
	private ABFloatCallback amount;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		float theAmount = 0;
		if (this.amount != null) {
			theAmount = this.amount.callback(game, caster, localStore, castId);
		}

		game.spawnTextTag(this.target.callback(game, caster, localStore, castId), caster.getPlayerIndex(),
				this.textType, (int) (theAmount));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "CreateIntTextTagFromConfig(" + this.target.generateJassEquivalent(jassTextGenerator)
				+ ", TEXT_TAG_CONFIG_TYPE_" + this.textType.name() + ", R2I("
				+ this.amount.generateJassEquivalent(jassTextGenerator) + "))";
	}
}

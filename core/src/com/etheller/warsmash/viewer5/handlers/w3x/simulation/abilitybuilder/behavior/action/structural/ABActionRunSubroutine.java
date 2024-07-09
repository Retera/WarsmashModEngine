package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionRunSubroutine implements ABSingleAction {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;

	@Override
	@SuppressWarnings("unchecked")
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		List<ABAction> actions;
		if ((this.instanceValue == null) || this.instanceValue.callback(game, caster, localStore, castId)) {
			actions = (List<ABAction>) localStore.get(ABLocalStoreKeys
					.combineSubroutineInstanceKey(this.key.callback(game, caster, localStore, castId), castId));
		}
		else {
			actions = (List<ABAction>) localStore.get(
					ABLocalStoreKeys.combineSubroutineKey(this.key.callback(game, caster, localStore, castId), castId));
		}
		System.err.println("RUNNING SUBROUTINE: " + this.key.callback(game, caster, localStore, castId));
		if (actions != null) {
			for (final ABAction action : actions) {
				action.runAction(game, caster, localStore, castId);
			}
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String instanceValueExpression = "true";
		if (this.instanceValue != null) {
			instanceValueExpression = this.instanceValue.generateJassEquivalent(jassTextGenerator);
		}

		return "RunSubroutineAU(" + jassTextGenerator.getCaster() + ", " + jassTextGenerator.getTriggerLocalStore()
				+ ", " + jassTextGenerator.getCastId() + ", " + this.key.generateJassEquivalent(jassTextGenerator)
				+ ", " + instanceValueExpression + ")";
	}

}

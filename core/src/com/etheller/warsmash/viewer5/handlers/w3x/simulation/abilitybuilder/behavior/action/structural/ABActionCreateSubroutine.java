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

public class ABActionCreateSubroutine implements ABSingleAction {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;
	private List<ABAction> actions;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		if ((this.instanceValue == null) || this.instanceValue.callback(game, caster, localStore, castId)) {
			localStore.put(ABLocalStoreKeys.combineSubroutineInstanceKey(
					this.key.callback(game, caster, localStore, castId), castId), this.actions);
		}
		else {
			localStore.put(
					ABLocalStoreKeys.combineSubroutineKey(this.key.callback(game, caster, localStore, castId), castId),
					this.actions);
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		final String subroutineFunctionName = jassTextGenerator.createAnonymousFunction(this.actions,
				"CreateSubroutineAU");

		String instanceValueExpression = "true";
		if (this.instanceValue == null) {
			instanceValueExpression = this.instanceValue.generateJassEquivalent(jassTextGenerator);
		}

		return "CreateSubroutineAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.key.generateJassEquivalent(jassTextGenerator) + ", " + jassTextGenerator.getCastId() + ", "
				+ instanceValueExpression + ", " + jassTextGenerator.functionPointerByName(subroutineFunctionName)
				+ ")";
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.structural;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionCreateSubroutine implements ABSingleAction {

	private ABStringCallback name;
	private ABBooleanCallback instanceValue;
	private List<ABAction> actions;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		if ((this.instanceValue == null) || this.instanceValue.callback(caster, localStore, castId)) {
			localStore.put(ABLocalStoreKeys.combineSubroutineInstanceKey(
					this.name.callback(caster, localStore, castId), castId), this.actions);
		}
		else {
			localStore.put(
					ABLocalStoreKeys.combineSubroutineKey(this.name.callback(caster, localStore, castId), castId),
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
				+ this.name.generateJassEquivalent(jassTextGenerator) + ", " + jassTextGenerator.getCastId() + ", "
				+ instanceValueExpression + ", " + jassTextGenerator.functionPointerByName(subroutineFunctionName)
				+ ")";
	}
}

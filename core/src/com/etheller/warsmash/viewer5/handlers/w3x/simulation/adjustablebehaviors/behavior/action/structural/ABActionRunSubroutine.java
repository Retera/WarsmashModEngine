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

public class ABActionRunSubroutine implements ABSingleAction {

	private ABStringCallback name;
	private ABBooleanCallback instanceValue;

	@Override
	@SuppressWarnings("unchecked")
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		List<ABAction> actions;
		if ((this.instanceValue == null) || this.instanceValue.callback(caster, localStore, castId)) {
			actions = (List<ABAction>) localStore.get(ABLocalStoreKeys
					.combineSubroutineInstanceKey(this.name.callback(caster, localStore, castId), castId));
		} else {
			actions = (List<ABAction>) localStore.get(ABLocalStoreKeys
					.combineSubroutineKey(this.name.callback(caster, localStore, castId), castId));
		}
		if (actions != null) {
			for (final ABAction action : actions) {
				action.runAction(caster, localStore, castId);
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
				+ ", " + jassTextGenerator.getCastId() + ", " + this.name.generateJassEquivalent(jassTextGenerator)
				+ ", " + instanceValueExpression + ")";
	}

}

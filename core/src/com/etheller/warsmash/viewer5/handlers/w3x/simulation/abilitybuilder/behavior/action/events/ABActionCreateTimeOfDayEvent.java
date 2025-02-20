package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.events;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.event.ABTimeOfDayEvent;

public class ABActionCreateTimeOfDayEvent implements ABSingleAction {

	private List<ABAction> actions;
	private ABFloatCallback startTime;
	private ABFloatCallback endTime;

	private ABStringCallback equalityId;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		String eqId = null;
		float st = 0;
		float et = Float.MAX_VALUE;
		if (this.equalityId != null) {
			eqId = this.equalityId.callback(game, caster, localStore, castId);
		}
		if (this.startTime != null) {
			st = this.startTime.callback(game, caster, localStore, castId);
		}
		if (this.endTime != null) {
			et = this.endTime.callback(game, caster, localStore, castId);
		}

		final ABTimeOfDayEvent event = new ABTimeOfDayEvent(game, caster, localStore, castId, this.actions, st, et,
				eqId);

		localStore.put(ABLocalStoreKeys.LASTCREATEDTODEVENT, event);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String actionsFuncName = jassTextGenerator.createAnonymousFunction(this.actions,
				"CreateABTimeOfDayEventAU_Actions");

		String startTimeExpression = "0";
		String endTimeExpression = Long.toString(Long.MAX_VALUE) + ".0";
		String equalityIdExpression = null;
		if (this.equalityId != null) {
			equalityIdExpression = this.equalityId.generateJassEquivalent(jassTextGenerator);
		}
		if (this.startTime != null) {
			startTimeExpression = this.startTime.generateJassEquivalent(jassTextGenerator);
		}
		if (this.endTime != null) {
			endTimeExpression = this.endTime.generateJassEquivalent(jassTextGenerator);
		}

		return "CreateABTimeOfDayEventAU(" + jassTextGenerator.functionPointerByName(actionsFuncName) + ", "
				+ startTimeExpression + ", " + endTimeExpression + ", " + this.equalityId + ", "
				+ jassTextGenerator.getCaster() + ", " + jassTextGenerator.getTriggerLocalStore() + ", "
				+ jassTextGenerator.getCastId() + ")";
	}
}

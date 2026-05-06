package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.events.timeofday;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.event.ABTimeOfDayEvent;

public class ABActionCreateTimeOfDayEvent implements ABSingleAction {

	private List<ABAction> actions;
	private ABFloatCallback startTime;
	private ABFloatCallback endTime;

	private ABStringCallback equalityId;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		String eqId = null;
		float st = 0;
		float et = Float.MAX_VALUE;
		if (this.equalityId != null) {
			eqId = this.equalityId.callback(caster, localStore, castId);
		}
		if (this.startTime != null) {
			st = this.startTime.callback(caster, localStore, castId);
		}
		if (this.endTime != null) {
			et = this.endTime.callback(caster, localStore, castId);
		}

		final ABTimeOfDayEvent event = new ABTimeOfDayEvent(caster, localStore, castId, this.actions, st, et, eqId);

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

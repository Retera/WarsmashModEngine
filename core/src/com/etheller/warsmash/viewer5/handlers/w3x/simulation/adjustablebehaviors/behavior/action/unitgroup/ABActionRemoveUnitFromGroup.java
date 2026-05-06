package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitgroup;

import java.util.Set;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unitgroup.ABUnitGroupCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionRemoveUnitFromGroup implements ABSingleAction {

	private ABUnitGroupCallback group;
	private ABUnitCallback unit;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final Set<CUnit> groupSet = this.group.callback(caster, localStore, castId);
		final CUnit rUnit = this.unit.callback(caster, localStore, castId);
		groupSet.remove(rUnit);
		localStore.put(ABLocalStoreKeys.LASTREMOVEDDUNIT, rUnit);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "GroupRemoveUnitAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.group.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.unit.generateJassEquivalent(jassTextGenerator) + ")";
	}
}

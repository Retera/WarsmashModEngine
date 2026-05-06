package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitgroup;

import java.util.Set;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unitgroup.ABUnitGroupCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionAddUnitToGroup implements ABSingleAction {

	private ABUnitGroupCallback group;
	private ABUnitCallback unit;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final Set<CUnit> groupSet = this.group.callback(caster, localStore, castId);
		final CUnit rUnit = this.unit.callback(caster, localStore, castId);
		groupSet.add(rUnit);
		localStore.put(ABLocalStoreKeys.LASTADDEDUNIT, rUnit);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "GroupAddUnitAU(" + this.group.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.unit.generateJassEquivalent(jassTextGenerator) + ")";
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitgroup;

import java.util.HashSet;
import java.util.Set;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionCreateUnitGroup implements ABSingleAction {

	private String name;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final Set<CUnit> group = new HashSet<>();
		if (this.name != null) {
			localStore.put("_unitgroup_" + this.name, group);
		}
		localStore.put(ABLocalStoreKeys.LASTCREATEDUNITGROUP, group);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		if (this.name != null) {
			return "CreateNamedGroupAU(" + jassTextGenerator.getTriggerLocalStore() + ", \"" + this.name + "\")";
		}
		return "CreateGroupAU(" + jassTextGenerator.getTriggerLocalStore() + ")";
	}
}

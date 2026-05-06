package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.ability;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.visitor.ABGetABAbilityByRawcodeVisitor;

public class ABCallbackGetAbilityById extends ABAbilityCallback {

	private ABIDCallback id;
	private ABUnitCallback unit;

	@Override
	public CAbility callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit theUnit = caster;
		if (this.unit != null) {
			theUnit = this.unit.callback(caster, localStore, castId);
		}
		return theUnit.getAbility(
				ABGetABAbilityByRawcodeVisitor.getInstance().reset(this.id.callback(caster, localStore, castId)));
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.comparison;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABWeaponTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;

public class ABConditionIsWeaponTypeEqual extends ABBooleanCallback {

	private ABWeaponTypeCallback weaponType1;
	private ABWeaponTypeCallback weaponType2;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CWeaponType lD = weaponType1.callback(caster, localStore, castId);
		CWeaponType rD = weaponType2.callback(caster, localStore, castId);
		if (lD == null) {
			if (rD == null) {
				return true;
			}
			return false;
		}
		return lD.equals(rD);
	}

}

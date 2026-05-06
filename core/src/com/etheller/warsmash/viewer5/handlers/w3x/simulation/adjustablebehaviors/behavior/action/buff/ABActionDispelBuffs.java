package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.buff;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionDispelBuffs implements ABAction {

	private ABUnitCallback source;
	private ABUnitCallback unit;
	private ABBooleanCallback filter;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit theCaster = caster;
		if (source != null) {
			theCaster = source.callback(theCaster, localStore, castId);
		}
		CUnit theTarget = caster;
		if (unit != null) {
			theTarget = unit.callback(theCaster, localStore, castId);
		}

		if (theTarget != null) {
			List<CBuff> toRemove = new ArrayList<>();
			for (CAbility ability : theTarget.getAbilities()) {
				if (ability.getAbilityCategory() == CAbilityCategory.BUFF) {
					CBuff buff = (CBuff) ability;
					localStore.put(ABLocalStoreKeys.ENUMBUFF, buff);
					if (filter != null && filter.callback(theCaster, localStore, castId)) {
						toRemove.add(buff);
					}
				}
			}

			for (CBuff buff : toRemove) {
				theTarget.remove(localStore.game, buff);
			}
		}
	}

}

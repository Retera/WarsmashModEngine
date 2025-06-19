package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.destructable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.destructable.ABDestructableCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionDispelDestructableBuffs implements ABAction {

	private ABUnitCallback source;
	private ABDestructableCallback dest;
	private ABCondition filter;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit theCaster = caster;
		if (source != null) {
			theCaster = source.callback(game, theCaster, localStore, castId);
		}
		CDestructable theTarget = dest.callback(game, theCaster, localStore, castId);

		if (theTarget != null && theTarget.getBuffs() != null) {
			List<CDestructableBuff> toRemove = new ArrayList<>();
			for (CDestructableBuff buff : theTarget.getBuffs()) {
				localStore.put(ABLocalStoreKeys.ENUMDESTBUFF, buff);
				if (filter != null && filter.callback(game, theCaster, localStore, castId)) {
					toRemove.add(buff);
				}
			}
			localStore.remove(ABLocalStoreKeys.ENUMDESTBUFF);

			for (CDestructableBuff buff : toRemove) {
				theTarget.remove(game, buff);
			}
		}
	}

}

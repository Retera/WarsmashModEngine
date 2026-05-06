package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.destructablebuff;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.destructable.ABDestructableCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionDispelDestructableBuffs implements ABAction {

	private ABUnitCallback source;
	private ABDestructableCallback dest;
	private ABBooleanCallback filter;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit theCaster = caster;
		if (source != null) {
			theCaster = source.callback(theCaster, localStore, castId);
		}
		CDestructable theTarget = dest.callback(theCaster, localStore, castId);

		if (theTarget != null && theTarget.getBuffs() != null) {
			List<CDestructableBuff> toRemove = new ArrayList<>();
			for (CDestructableBuff buff : theTarget.getBuffs()) {
				localStore.put(ABLocalStoreKeys.ENUMDESTBUFF, buff);
				if (filter != null && filter.callback(theCaster, localStore, castId)) {
					toRemove.add(buff);
				}
			}
			localStore.remove(ABLocalStoreKeys.ENUMDESTBUFF);

			for (CDestructableBuff buff : toRemove) {
				theTarget.remove(localStore.game, buff);
			}
		}
	}

}

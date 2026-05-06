
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitstate;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.statemod.ABStateModBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;

public class ABActionAddStateModBuff implements ABAction {

	private ABUnitCallback targetUnit;
	private ABStateModBuffCallback buff;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnit target = targetUnit.callback(caster, localStore, castId);
		StateModBuff theBuff = buff.callback(caster, localStore, castId);

		target.addStateModBuff(theBuff);
		target.computeUnitState(localStore.game, theBuff.getBuffType());
	}
}
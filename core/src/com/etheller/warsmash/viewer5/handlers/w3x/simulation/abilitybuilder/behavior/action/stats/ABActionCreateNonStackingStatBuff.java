
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.stats;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABNonStackingStatBuffTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;

public class ABActionCreateNonStackingStatBuff implements ABAction {

	private ABNonStackingStatBuffTypeCallback buffType;
	private ABStringCallback stackingKey;
	private ABFloatCallback value;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		NonStackingStatBuff buff = new NonStackingStatBuff(buffType.callback(game, caster, localStore, castId), stackingKey.callback(game, caster, localStore, castId), value.callback(game, caster, localStore, castId));

		localStore.put(ABLocalStoreKeys.LASTCREATEDNSSB, buff);
	}
}
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionAddStunBuff implements ABAction {

	private ABUnitCallback target;
	private ABIDCallback buffId;
	private ABFloatCallback duration;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		CAbility ability = new CBuffStun(game.getHandleIdAllocator().createId(),
				buffId.callback(game, caster, localStore, castId), duration.callback(game, caster, localStore, castId));
		target.callback(game, caster, localStore, castId).add(game, ability);
		localStore.put(ABLocalStoreKeys.LASTADDEDBUFF, ability);
	}
}

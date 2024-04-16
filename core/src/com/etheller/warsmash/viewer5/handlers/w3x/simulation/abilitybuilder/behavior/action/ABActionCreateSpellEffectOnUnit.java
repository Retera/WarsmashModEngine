package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABActionCreateSpellEffectOnUnit implements ABAction {

	private ABUnitCallback target;
	private ABIDCallback id;
	private CEffectType effectType;
	private ABIntegerCallback index;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		if (index == null) {
			SimulationRenderComponent ret = game.createPersistentSpellEffectOnUnit(
					(target.callback(game, caster, localStore, castId)), this.id.callback(game, caster, localStore, castId),
					this.effectType);
			localStore.put(ABLocalStoreKeys.LASTCREATEDFX, ret);
		} else {
			SimulationRenderComponent ret = game.createPersistentSpellEffectOnUnit(
					(target.callback(game, caster, localStore, castId)), this.id.callback(game, caster, localStore, castId),
					this.effectType, this.index.callback(game, caster, localStore, castId));
			localStore.put(ABLocalStoreKeys.LASTCREATEDFX, ret);
		}
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.fx;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABActionCreateUberSplat implements ABAction {

	private ABIDCallback id;
	private ABLocationCallback location;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		AbilityPointTarget loc = location.callback(game, caster, localStore, castId);
		SimulationRenderComponent splat = game.createStaticUberSplat(loc.getX(), loc.getY(),
				id.callback(game, caster, localStore, castId));
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, splat);
	}

}

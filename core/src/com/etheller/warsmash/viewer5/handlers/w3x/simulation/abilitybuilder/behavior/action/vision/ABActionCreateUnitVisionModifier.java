package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.vision;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CUnitVisionFogModifier;

public class ABActionCreateUnitVisionModifier implements ABAction {
	
	private ABUnitCallback unit;
	private ABPlayerCallback player;
	private ABBooleanCallback alwaysNightVision;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		boolean alwaysNight = false;
		if (alwaysNightVision != null) {
			alwaysNight = alwaysNightVision.callback(game, caster, localStore, castId);
		}
		
		CUnitVisionFogModifier vision = new CUnitVisionFogModifier(unit.callback(game, caster, localStore, castId), alwaysNight);
		if (player != null) {
			player.callback(game, caster, localStore, castId).addFogModifer(game, vision);
		}
		localStore.put(ABLocalStoreKeys.LASTCREATEDVISIONMODIFIER, vision);
	}

}

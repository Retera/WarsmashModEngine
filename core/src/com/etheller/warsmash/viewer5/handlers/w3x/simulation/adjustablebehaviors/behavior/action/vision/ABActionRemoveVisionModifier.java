package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.visionmodifier.ABVisionModifierCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionRemoveVisionModifier implements ABAction {
	
	private ABVisionModifierCallback modifier;
	private ABPlayerCallback player;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, int castId) {
		player.callback(caster, localStore, castId).removeFogModifer(localStore.game,
				modifier.callback(caster, localStore, castId));
	}

}

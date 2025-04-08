package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.vision;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CProjectileVisionFogModifier;

public class ABActionCreateProjectileVisionModifier implements ABAction {

	private ABProjectileCallback projectile;
	private ABPlayerCallback player;
	private ABFloatCallback radius;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		final CProjectileVisionFogModifier vision = new CProjectileVisionFogModifier(
				this.projectile.callback(game, caster, localStore, castId), radius.callback(game, caster, localStore, castId));
		if (this.player != null) {
			this.player.callback(game, caster, localStore, castId).addFogModifer(game, vision, false);
		}
		localStore.put(ABLocalStoreKeys.LASTCREATEDVISIONMODIFIER, vision);
	}

}

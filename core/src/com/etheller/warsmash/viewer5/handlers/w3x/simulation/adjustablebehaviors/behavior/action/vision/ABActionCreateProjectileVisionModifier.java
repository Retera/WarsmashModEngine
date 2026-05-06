package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CProjectileVisionFogModifier;

public class ABActionCreateProjectileVisionModifier implements ABAction {

	private ABProjectileCallback projectile;
	private ABPlayerCallback player;
	private ABFloatCallback radius;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, int castId) {
		final CProjectileVisionFogModifier vision = new CProjectileVisionFogModifier(
				this.projectile.callback(caster, localStore, castId),
				radius.callback(caster, localStore, castId));
		if (this.player != null) {
			this.player.callback(caster, localStore, castId).addFogModifer(localStore.game, vision, false);
		}
		localStore.put(ABLocalStoreKeys.LASTCREATEDVISIONMODIFIER, vision);
	}

}

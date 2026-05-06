package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CCircleFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CTimedCircleFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class ABActionCreateLocationVisionModifier implements ABAction {

	private ABLocationCallback location;
	private ABFloatCallback radius;
	private ABFloatCallback duration;
	private ABPlayerCallback player;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, int castId) {
		CFogModifier vision;
		final AbilityPointTarget loc = this.location.callback(caster, localStore, castId);
		if (this.duration == null) {
			vision = new CCircleFogModifier(CFogState.VISIBLE, this.radius.callback(caster, localStore, castId),
					loc.getX(), loc.getY());
		}
		else {
			vision = new CTimedCircleFogModifier(CFogState.VISIBLE,
					this.radius.callback(caster, localStore, castId), loc.getX(), loc.getY(),
					this.duration.callback(caster, localStore, castId));
		}
		if (this.player != null) {
			this.player.callback(caster, localStore, castId).addFogModifer(localStore.game, vision, false);
		}
		localStore.put(ABLocalStoreKeys.LASTCREATEDVISIONMODIFIER, vision);
	}

}

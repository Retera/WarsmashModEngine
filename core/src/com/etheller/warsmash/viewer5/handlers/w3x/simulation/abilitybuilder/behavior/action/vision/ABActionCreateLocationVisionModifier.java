package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.vision;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
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
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		CFogModifier vision;
		final AbilityPointTarget loc = this.location.callback(game, caster, localStore, castId);
		if (this.duration == null) {
			vision = new CCircleFogModifier(CFogState.VISIBLE, this.radius.callback(game, caster, localStore, castId),
					loc.getX(), loc.getY());
		}
		else {
			vision = new CTimedCircleFogModifier(CFogState.VISIBLE,
					this.radius.callback(game, caster, localStore, castId), loc.getX(), loc.getY(),
					this.duration.callback(game, caster, localStore, castId));
		}
		if (this.player != null) {
			this.player.callback(game, caster, localStore, castId).addFogModifer(game, vision, false);
		}
		localStore.put(ABLocalStoreKeys.LASTCREATEDVISIONMODIFIER, vision);
	}

}

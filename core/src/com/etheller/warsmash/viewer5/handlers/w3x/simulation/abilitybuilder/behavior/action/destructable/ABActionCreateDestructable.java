package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.destructable;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionCreateDestructable implements ABAction {

	private ABIDCallback id;
	private ABLocationCallback location;
	private ABFloatCallback facing;
	private ABFloatCallback scale;
	private ABIntegerCallback variation;

	private ABBooleanCallback playBirthAnim;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		float theFacing = 0;
		float theScale = 1;
		int theVariation = 0;
		boolean play = true;
		if (this.facing != null) {
			theFacing = this.facing.callback(game, caster, localStore, castId);
		}
		if (this.scale != null) {
			theScale = this.scale.callback(game, caster, localStore, castId);
		}
		if (this.variation != null) {
			theVariation = this.variation.callback(game, caster, localStore, castId);
		}
		if (this.playBirthAnim != null) {
			play = this.playBirthAnim.callback(game, caster, localStore, castId);
		}
		final AbilityPointTarget location = this.location.callback(game, caster, localStore, castId);
		final CDestructable createdDest = game.createDestructable(this.id.callback(game, caster, localStore, castId),
				location.getX(), location.getY(), theFacing, theScale, theVariation);
		if (play) {
			createdDest.getUnitAnimationListener().playAnimation(true, PrimaryTag.BIRTH, SequenceUtils.EMPTY, 1.0f,
					true);
		}
		localStore.put(ABLocalStoreKeys.LASTCREATEDDESTRUCTABLE, createdDest);
	}

}

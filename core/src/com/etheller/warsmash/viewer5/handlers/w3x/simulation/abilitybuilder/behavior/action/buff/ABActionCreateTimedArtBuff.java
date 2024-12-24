package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedArtBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateTimedArtBuff implements ABSingleAction {

	private ABIDCallback buffId;
	private ABFloatCallback duration;
	private ABBooleanCallback showIcon;
	private CEffectType artType;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {

		if (this.showIcon != null) {
			final ABTimedArtBuff ability = new ABTimedArtBuff(game.getHandleIdAllocator().createId(),
					this.buffId.callback(game, caster, localStore, castId),
					this.duration.callback(game, caster, localStore, castId),
					this.showIcon.callback(game, caster, localStore, castId));
			if (this.artType != null) {
				ability.setArtType(this.artType);
			}
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		}
		else {
			final ABTimedArtBuff ability = new ABTimedArtBuff(game.getHandleIdAllocator().createId(),
					this.buffId.callback(game, caster, localStore, castId),
					this.duration.callback(game, caster, localStore, castId));
			if (this.artType != null) {
				ability.setArtType(this.artType);
			}
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		}
		if (!localStore.containsKey(ABLocalStoreKeys.BUFFCASTINGUNIT)) {
			localStore.put(ABLocalStoreKeys.BUFFCASTINGUNIT, caster);
		}

	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		CEffectType artTypeUsed;
		if (this.artType != null) {
			artTypeUsed = this.artType;
		}
		else {
			artTypeUsed = CEffectType.TARGET;
		}
		final String artTypeJass = "EFFECT_TYPE_" + artTypeUsed.name();
		String showIconJass;
		if (this.showIcon != null) {
			showIconJass = this.showIcon.generateJassEquivalent(jassTextGenerator);
		}
		else {
			showIconJass = "true";
		}
		return "CreateTimedArtBuffAU(" + jassTextGenerator.getCaster() + ", " + jassTextGenerator.getTriggerLocalStore()
				+ ", " + this.buffId.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.duration.generateJassEquivalent(jassTextGenerator) + ", " + showIconJass + ", " + artTypeJass
				+ ")";
	}
}

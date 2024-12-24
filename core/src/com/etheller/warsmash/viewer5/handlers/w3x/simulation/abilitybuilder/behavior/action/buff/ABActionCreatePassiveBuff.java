package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABPermanentPassiveBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreatePassiveBuff implements ABSingleAction {

	private ABIDCallback buffId;
	private ABBooleanCallback showIcon;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private CEffectType artType;

	private ABBooleanCallback showFx;
	private ABBooleanCallback playSfx;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		ABPermanentPassiveBuff ability = null;
		if (this.showIcon != null) {
			ability = new ABPermanentPassiveBuff(game.getHandleIdAllocator().createId(),
					this.buffId.callback(game, caster, localStore, castId), localStore, this.onAddActions,
					this.onRemoveActions, this.showIcon.callback(game, caster, localStore, castId), castId);
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		}
		else {
			ability = new ABPermanentPassiveBuff(game.getHandleIdAllocator().createId(),
					this.buffId.callback(game, caster, localStore, castId), localStore, this.onAddActions,
					this.onRemoveActions, true, castId);

			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		}
		if (this.artType != null) {
			ability.setArtType(this.artType);
		}
		if (this.showFx != null) {
			ability.setShowFx(this.showFx.callback(game, caster, localStore, castId));
		}
		if (this.playSfx != null) {
			ability.setPlaySfx(this.playSfx.callback(game, caster, localStore, castId));
		}
		if (!localStore.containsKey(ABLocalStoreKeys.BUFFCASTINGUNIT)) {
			localStore.put(ABLocalStoreKeys.BUFFCASTINGUNIT, caster);
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String addFunctionName = jassTextGenerator.createAnonymousFunction(this.onAddActions,
				"CreatePassiveBuffAU_OnAddActions");
		final String removeFunctionName = jassTextGenerator.createAnonymousFunction(this.onRemoveActions,
				"CreatePassiveBuffAU_OnRemoveActions");

		return "CreatePassiveBuffAU(" + jassTextGenerator.getCaster() + ", " + jassTextGenerator.getTriggerLocalStore()
				+ ", " + this.buffId.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.showIcon.generateJassEquivalent(jassTextGenerator) + ", "
				+ jassTextGenerator.functionPointerByName(addFunctionName) + ", "
				+ jassTextGenerator.functionPointerByName(removeFunctionName) + ", EFFECT_TYPE_" + this.artType.name()
				+ ", " + this.showFx.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.playSfx.generateJassEquivalent(jassTextGenerator) + ", " + jassTextGenerator.getCastId() + ")";
	}
}

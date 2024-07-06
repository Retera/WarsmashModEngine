package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedTickingBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateTimedTickingBuff implements ABSingleAction {

	private ABIDCallback buffId;
	private ABFloatCallback duration;
	private ABBooleanCallback showTimedLifeBar;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onExpireActions;
	private List<ABAction> onTickActions;
	private ABBooleanCallback showIcon;
	private CEffectType artType;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		boolean showTimedLife = false;
		if (this.showTimedLifeBar != null) {
			showTimedLife = this.showTimedLifeBar.callback(game, caster, localStore, castId);
		}

		if (this.showIcon != null) {
			final ABTimedTickingBuff ability = new ABTimedTickingBuff(game.getHandleIdAllocator().createId(),
					this.buffId.callback(game, caster, localStore, castId),
					this.duration.callback(game, caster, localStore, castId), showTimedLife, localStore,
					this.onAddActions, this.onRemoveActions, this.onExpireActions, this.onTickActions,
					this.showIcon.callback(game, caster, localStore, castId), castId);
			if (this.artType != null) {
				ability.setArtType(this.artType);
			}
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		}
		else {
			final ABTimedTickingBuff ability = new ABTimedTickingBuff(game.getHandleIdAllocator().createId(),
					this.buffId.callback(game, caster, localStore, castId),
					this.duration.callback(game, caster, localStore, castId), showTimedLife, localStore,
					this.onAddActions, this.onRemoveActions, this.onExpireActions, this.onTickActions, castId);
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
		final String addFunctionName = jassTextGenerator.createAnonymousFunction(this.onAddActions,
				"CreateTimedTickingBuffAU_OnAddActions");
		final String removeFunctionName = jassTextGenerator.createAnonymousFunction(this.onRemoveActions,
				"CreateTimedTickingBuffAU_OnRemoveActions");
		final String expireFunctionName = jassTextGenerator.createAnonymousFunction(this.onExpireActions,
				"CreateTimedTickingBuffAU_OnExpireActions");
		final String tickFunctionName = jassTextGenerator.createAnonymousFunction(this.onTickActions,
				"CreateTimedTickingBuffAU_OnTickActions");

		String showTimedLife = "false";
		if (this.showTimedLifeBar != null) {
			showTimedLife = this.showTimedLifeBar.generateJassEquivalent(jassTextGenerator);
		}

		String showIconExpression;
		if (this.showIcon != null) {
			showIconExpression = this.showIcon.generateJassEquivalent(jassTextGenerator);
		}
		else {
			showIconExpression = "true";
		}

		CEffectType artTypeUsed = CEffectType.TARGET;
		if (this.artType != null) {
			artTypeUsed = this.artType;
		}
		String artTypeExpression;
		artTypeExpression = "EFFECT_TYPE_" + artTypeUsed.name();

		return "CreateTimedTickingBuffAU(" + jassTextGenerator.getCaster() + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.buffId.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.duration.generateJassEquivalent(jassTextGenerator) + ", " + showTimedLife + ", "
				+ jassTextGenerator.functionPointerByName(addFunctionName) + ", "
				+ jassTextGenerator.functionPointerByName(removeFunctionName) + ", "
				+ jassTextGenerator.functionPointerByName(expireFunctionName) + ", "
				+ jassTextGenerator.functionPointerByName(tickFunctionName) + ", " + showIconExpression + ", "
				+ artTypeExpression + ", " + jassTextGenerator.getCastId() + ")";
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABPermanentPassiveBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreatePassiveBuff implements ABSingleAction {

	private ABIDCallback buffId;
	private ABBooleanCallback showIcon;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private CEffectType artType;

	private ABBooleanCallback showFx;
	private ABBooleanCallback playSfx;

	private ABBooleanCallback leveled;
	private ABBooleanCallback positive;

	private ABStringCallback visibilityGroup;

	private List<ABStringCallback> uniqueFlags;
	private Map<String, ABCallback> uniqueValues;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		ABPermanentPassiveBuff ability = null;
		boolean isLeveled = false;
		if (leveled != null) {
			isLeveled = leveled.callback(caster, localStore, castId);
		} else {
			isLeveled = localStore.getBooleanOrDefault(ABLocalStoreKeys.ISABILITYLEVELED, false);
		}
		boolean isPositive = true;
		if (positive != null) {
			isPositive = positive.callback(caster, localStore, castId);
		}

		if (showIcon != null) {
			ability = new ABPermanentPassiveBuff(localStore.game.getHandleIdAllocator().createId(),
					buffId.callback(caster, localStore, castId), localStore.originAbility, caster, localStore,
					onAddActions, onRemoveActions, showIcon.callback(caster, localStore, castId), castId, isLeveled,
					isPositive);
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		} else {
			ability = new ABPermanentPassiveBuff(localStore.game.getHandleIdAllocator().createId(),
					buffId.callback(caster, localStore, castId), localStore.originAbility, caster, localStore,
					onAddActions, onRemoveActions, true, castId, isLeveled, isPositive);
			localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		}
		if (this.artType != null) {
			ability.setArtType(this.artType);
		}
		if (this.showFx != null) {
			ability.setShowFx(this.showFx.callback(caster, localStore, castId));
		}
		if (this.playSfx != null) {
			ability.setPlaySfx(this.playSfx.callback(caster, localStore, castId));
		}
		if (uniqueFlags != null) {
			for (ABStringCallback flag : uniqueFlags) {
				ability.addUniqueFlag(flag.callback(caster, localStore, castId));
			}
		}
		if (uniqueValues != null) {
			for (String key : uniqueValues.keySet()) {
				ability.addUniqueValue(uniqueValues.get(key).callback(caster, localStore, castId), key);
			}
		}
		if (visibilityGroup != null) {
			ability.setVisibilityGroup(visibilityGroup.callback(caster, localStore, castId));
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

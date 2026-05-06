package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABTimedArtBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateTimedArtBuff implements ABSingleAction {

	private ABIDCallback buffId;
	private ABFloatCallback duration;
	private ABBooleanCallback showIcon;
	private CEffectType artType;

	private ABBooleanCallback leveled;
	private ABBooleanCallback positive;
	private ABBooleanCallback dispellable;

	private ABBooleanCallback stacks;
	private ABStringCallback visibilityGroup;

	private List<ABStringCallback> uniqueFlags;
	private Map<String, ABCallback> uniqueValues;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
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
		boolean isDispellable = true;
		if (dispellable != null) {
			isDispellable = dispellable.callback(caster, localStore, castId);
		} else {
			isDispellable = localStore.originAbility.isMagic();
		}

		ABTimedArtBuff ability;
		if (showIcon != null) {
			ability = new ABTimedArtBuff(localStore.game.getHandleIdAllocator().createId(),
					buffId.callback(caster, localStore, castId), localStore, localStore.originAbility, caster,
					duration.callback(caster, localStore, castId), showIcon.callback(caster, localStore, castId),
					isLeveled, isPositive, isDispellable);
		} else {
			ability = new ABTimedArtBuff(localStore.game.getHandleIdAllocator().createId(),
					buffId.callback(caster, localStore, castId), localStore, localStore.originAbility, caster,
					duration.callback(caster, localStore, castId), isLeveled, isPositive, isDispellable);
		}
		if (artType != null) {
			ability.setArtType(artType);
		}
		boolean isStacks = false;
		if (stacks != null) {
			isStacks = stacks.callback(caster, localStore, castId);
		}
		ability.setStacks(isStacks);
		if (visibilityGroup != null) {
			ability.setVisibilityGroup(visibilityGroup.callback(caster, localStore, castId));
		}
		localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
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
		if (!localStore.containsKey(ABLocalStoreKeys.BUFFCASTINGUNIT)) {
			localStore.put(ABLocalStoreKeys.BUFFCASTINGUNIT, caster);
		}

	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		CEffectType artTypeUsed;
		if (this.artType != null) {
			artTypeUsed = this.artType;
		} else {
			artTypeUsed = CEffectType.TARGET;
		}
		final String artTypeJass = "EFFECT_TYPE_" + artTypeUsed.name();
		String showIconJass;
		if (this.showIcon != null) {
			showIconJass = this.showIcon.generateJassEquivalent(jassTextGenerator);
		} else {
			showIconJass = "true";
		}
		return "CreateTimedArtBuffAU(" + jassTextGenerator.getCaster() + ", " + jassTextGenerator.getTriggerLocalStore()
				+ ", " + this.buffId.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.duration.generateJassEquivalent(jassTextGenerator) + ", " + showIconJass + ", " + artTypeJass
				+ ")";
	}
}

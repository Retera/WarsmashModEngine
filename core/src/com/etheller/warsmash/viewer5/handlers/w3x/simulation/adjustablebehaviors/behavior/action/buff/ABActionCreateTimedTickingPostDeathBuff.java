package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.buff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.statbuff.ABNonStackingStatBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.statemod.ABStateModBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABTimedTickingPostDeathBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;

public class ABActionCreateTimedTickingPostDeathBuff implements ABSingleAction {

	private ABIDCallback buffId;
	private ABFloatCallback duration;
	private ABBooleanCallback showTimedLifeBar;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onExpireActions;
	private List<ABAction> onTickActions;

	private List<ABNonStackingStatBuffCallback> statBuffs;
	private List<ABStateModBuffCallback> stateMods;

	private ABBooleanCallback showIcon;
	private CEffectType artType;

	private ABBooleanCallback leveled;
	private ABBooleanCallback positive;
	private ABBooleanCallback dispellable;
	private ABBooleanCallback magic;
	private ABBooleanCallback physical;

	private ABBooleanCallback stacks;
	private ABStringCallback visibilityGroup;

	private List<ABStringCallback> uniqueFlags;
	private Map<String, ABCallback> uniqueValues;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		boolean showTimedLife = false;
		if (this.showTimedLifeBar != null) {
			showTimedLife = this.showTimedLifeBar.callback(caster, localStore, castId);
		}
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

		boolean isMagic = localStore.originAbility.isMagic();
		boolean isPhysical = localStore.originAbility.isPhysical();
		if (magic != null) {
			isMagic = magic.callback(caster, localStore, castId);
		}
		if (physical != null) {
			isPhysical = physical.callback(caster, localStore, castId);
		}

		ABTimedTickingPostDeathBuff ability;
		if (showIcon != null) {
			ability = new ABTimedTickingPostDeathBuff(localStore.game.getHandleIdAllocator().createId(),
					buffId.callback(caster, localStore, castId), localStore, localStore.originAbility, caster,
					duration.callback(caster, localStore, castId), showTimedLife, onAddActions, onRemoveActions,
					onExpireActions, onTickActions, showIcon.callback(caster, localStore, castId), castId, isLeveled,
					isPositive, isDispellable);
			if (artType != null) {
				ability.setArtType(artType);
			}
		} else {
			ability = new ABTimedTickingPostDeathBuff(localStore.game.getHandleIdAllocator().createId(),
					buffId.callback(caster, localStore, castId), localStore, localStore.originAbility, caster,
					duration.callback(caster, localStore, castId), showTimedLife, onAddActions, onRemoveActions,
					onExpireActions, onTickActions, castId, isLeveled, isPositive, isDispellable);
			if (artType != null) {
				ability.setArtType(artType);
			}
		}

		if (stateMods != null) {
			List<StateModBuff> buffMods = new ArrayList<>();
			for (ABStateModBuffCallback mod : stateMods) {
				buffMods.add(mod.callback(caster, localStore, castId));
			}
			ability.setStateMods(buffMods);
		}
		if (statBuffs != null) {
			List<NonStackingStatBuff> buffStats = new ArrayList<>();
			for (ABNonStackingStatBuffCallback mod : statBuffs) {
				buffStats.add(mod.callback(caster, localStore, castId));
			}
			ability.setStatBuffs(buffStats);
		}

		ability.setMagic(isMagic);
		ability.setPhysical(isPhysical);
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
		final String addFunctionName = jassTextGenerator.createAnonymousFunction(this.onAddActions,
				"CreateTimedTickingPostDeathBuffAU_OnAddActions");
		final String removeFunctionName = jassTextGenerator.createAnonymousFunction(this.onRemoveActions,
				"CreateTimedTickingPostDeathBuffAU_OnRemoveActions");
		final String expireFunctionName = jassTextGenerator.createAnonymousFunction(this.onExpireActions,
				"CreateTimedTickingPostDeathBuffAU_OnExpireActions");
		final String tickFunctionName = jassTextGenerator.createAnonymousFunction(this.onTickActions,
				"CreateTimedTickingPostDeathBuffAU_OnTickActions");

		String showTimedLife = "false";
		if (this.showTimedLifeBar != null) {
			showTimedLife = this.showTimedLifeBar.generateJassEquivalent(jassTextGenerator);
		}

		String showIconExpression;
		if (this.showIcon != null) {
			showIconExpression = this.showIcon.generateJassEquivalent(jassTextGenerator);
		} else {
			showIconExpression = "true";
		}

		CEffectType artTypeUsed = CEffectType.TARGET;
		if (this.artType != null) {
			artTypeUsed = this.artType;
		}
		String artTypeExpression;
		artTypeExpression = "EFFECT_TYPE_" + artTypeUsed.name();

		return "CreateTimedTickingPostDeathBuffAU(" + jassTextGenerator.getCaster() + ", "
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

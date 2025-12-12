package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statbuffcallbacks.ABNonStackingStatBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statemodcallbacks.ABStateModBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedTickingPausedBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;

public class ABActionCreateTimedTickingPausedBuff implements ABSingleAction {

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
	private Map<ABStringCallback, ABCallback> uniqueValues;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		boolean showTimedLife = false;
		if (this.showTimedLifeBar != null) {
			showTimedLife = this.showTimedLifeBar.callback(game, caster, localStore, castId);
		}
		boolean isLeveled = false;
		if (leveled != null) {
			isLeveled = leveled.callback(game, caster, localStore, castId);
		} else {
			isLeveled = (boolean) localStore.getOrDefault(ABLocalStoreKeys.ISABILITYLEVELED, false);
		}
		boolean isPositive = true;
		if (positive != null) {
			isPositive = positive.callback(game, caster, localStore, castId);
		}
		boolean isDispellable = true;
		if (dispellable != null) {
			isDispellable = dispellable.callback(game, caster, localStore, castId);
		} else {
			isDispellable = ((boolean) localStore.getOrDefault(ABLocalStoreKeys.ISABILITYMAGIC, true));
		}

		boolean isMagic = ((boolean) localStore.getOrDefault(ABLocalStoreKeys.ISABILITYMAGIC, true));
		boolean isPhysical = ((boolean) localStore.getOrDefault(ABLocalStoreKeys.ISABILITYPHYSICAL, false));
		if (magic != null) {
			isMagic = magic.callback(game, caster, localStore, castId);
		}
		if (physical != null) {
			isPhysical = physical.callback(game, caster, localStore, castId);
		}

		ABTimedTickingPausedBuff ability;
		if (showIcon != null) {
			ability = new ABTimedTickingPausedBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore, castId), localStore,
					(CAbility) localStore.get(ABLocalStoreKeys.ABILITY), caster,
					duration.callback(game, caster, localStore, castId), showTimedLife, onAddActions, onRemoveActions,
					onExpireActions, onTickActions, showIcon.callback(game, caster, localStore, castId), castId,
					isLeveled, isPositive, isDispellable);
		} else {
			ability = new ABTimedTickingPausedBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore, castId), localStore,
					(CAbility) localStore.get(ABLocalStoreKeys.ABILITY), caster,
					duration.callback(game, caster, localStore, castId), showTimedLife, onAddActions, onRemoveActions,
					onExpireActions, onTickActions, castId, isLeveled, isPositive, isDispellable);
		}

		if (stateMods != null) {
			List<StateModBuff> buffMods = new ArrayList<>();
			for (ABStateModBuffCallback mod : stateMods) {
				buffMods.add(mod.callback(game, caster, localStore, castId));
			}
			ability.setStateMods(buffMods);
		}
		if (statBuffs != null) {
			List<NonStackingStatBuff> buffStats = new ArrayList<>();
			for (ABNonStackingStatBuffCallback mod : statBuffs) {
				buffStats.add(mod.callback(game, caster, localStore, castId));
			}
			ability.setStatBuffs(buffStats);
		}

		if (artType != null) {
			ability.setArtType(artType);
		}
		ability.setMagic(isMagic);
		ability.setPhysical(isPhysical);
		boolean isStacks = false;
		if (stacks != null) {
			isStacks = stacks.callback(game, caster, localStore, castId);
		}
		ability.setStacks(isStacks);
		if (visibilityGroup != null) {
			ability.setVisibilityGroup(visibilityGroup.callback(game, caster, localStore, castId));
		}

		localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		if (uniqueFlags != null) {
			for (ABStringCallback flag : uniqueFlags) {
				ability.addUniqueFlag(flag.callback(game, caster, localStore, castId));
			}
		}
		if (uniqueValues != null) {
			for (ABStringCallback key : uniqueValues.keySet()) {
				ability.addUniqueValue(uniqueValues.get(key).callback(game, caster, localStore, castId),
						key.callback(game, caster, localStore, castId));
			}
		}
		if (!localStore.containsKey(ABLocalStoreKeys.BUFFCASTINGUNIT)) {
			localStore.put(ABLocalStoreKeys.BUFFCASTINGUNIT, caster);
		}

	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String addFunctionName = jassTextGenerator.createAnonymousFunction(this.onAddActions,
				"CreateTimedTickingPausedBuffAU_OnAddActions");
		final String removeFunctionName = jassTextGenerator.createAnonymousFunction(this.onRemoveActions,
				"CreateTimedTickingPausedBuffAU_OnRemoveActions");
		final String expireFunctionName = jassTextGenerator.createAnonymousFunction(this.onExpireActions,
				"CreateTimedTickingPausedBuffAU_OnExpireActions");
		final String tickFunctionName = jassTextGenerator.createAnonymousFunction(this.onTickActions,
				"CreateTimedTickingPausedBuffAU_OnTickActions");

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

		return "CreateTimedTickingPausedBuffAU(" + jassTextGenerator.getCaster() + ", "
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

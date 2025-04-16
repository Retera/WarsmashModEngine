package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedArtBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
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

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
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

		ABTimedArtBuff ability;
		if (showIcon != null) {
			ability = new ABTimedArtBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore, castId),
					(CAbility) localStore.get(ABLocalStoreKeys.ABILITY), caster,
					duration.callback(game, caster, localStore, castId),
					showIcon.callback(game, caster, localStore, castId), isLeveled, isPositive, isDispellable);
		} else {
			ability = new ABTimedArtBuff(game.getHandleIdAllocator().createId(),
					buffId.callback(game, caster, localStore, castId),
					(CAbility) localStore.get(ABLocalStoreKeys.ABILITY), caster,
					duration.callback(game, caster, localStore, castId), isLeveled, isPositive, isDispellable);
		}
		if (artType != null) {
			ability.setArtType(artType);
		}
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

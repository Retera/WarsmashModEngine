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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedTargetingBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionCreateTimedTargetingBuff implements ABSingleAction {

	private ABIDCallback buffId;
	private ABFloatCallback duration;

	private ABBooleanCallback stacks;

	private List<ABStringCallback> uniqueFlags;
	private Map<String, ABCallback> uniqueValues;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final ABTimedTargetingBuff ability = new ABTimedTargetingBuff(game.getHandleIdAllocator().createId(),
				this.buffId.callback(game, caster, localStore, castId), localStore,
				(CAbility) localStore.get(ABLocalStoreKeys.ABILITY), caster,
				this.duration.callback(game, caster, localStore, castId));
		if (uniqueFlags != null) {
			for (ABStringCallback flag : uniqueFlags) {
				ability.addUniqueFlag(flag.callback(game, caster, localStore, castId));
			}
		}
		if (uniqueValues != null) {
			for (String key : uniqueValues.keySet()) {
				ability.addUniqueValue(uniqueValues.get(key).callback(game, caster, localStore, castId), key);
			}
		}
		boolean isStacks = false;
		if (stacks != null) {
			isStacks = stacks.callback(game, caster, localStore, castId);
		}
		ability.setStacks(isStacks);

		localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		if (!localStore.containsKey(ABLocalStoreKeys.BUFFCASTINGUNIT)) {
			localStore.put(ABLocalStoreKeys.BUFFCASTINGUNIT, caster);
		}

	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "CreateTimedTargetingBuffAU(" + jassTextGenerator.getCaster() + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.buffId.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.duration.generateJassEquivalent(jassTextGenerator) + ")";
	}
}

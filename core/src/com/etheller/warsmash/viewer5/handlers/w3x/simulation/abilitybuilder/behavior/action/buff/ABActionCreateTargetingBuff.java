package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTargetingBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionCreateTargetingBuff implements ABSingleAction {

	private ABIDCallback buffId;
	
	private List<ABStringCallback> uniqueFlags;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final ABTargetingBuff ability = new ABTargetingBuff(game.getHandleIdAllocator().createId(),
				this.buffId.callback(game, caster, localStore, castId));
		if (uniqueFlags != null) {
			for (ABStringCallback flag : uniqueFlags) {
				ability.addUniqueFlag(flag.callback(game, caster, localStore, castId));
			}
		}

		localStore.put(ABLocalStoreKeys.LASTCREATEDBUFF, ability);
		if (!localStore.containsKey(ABLocalStoreKeys.BUFFCASTINGUNIT)) {
			localStore.put(ABLocalStoreKeys.BUFFCASTINGUNIT, caster);
		}

	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "CreateTargetingBuffAU(" + jassTextGenerator.getCaster() + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ", " + jassTextGenerator.getCastId() + ", "
				+ this.buffId.generateJassEquivalent(jassTextGenerator) + ")";
	}
}

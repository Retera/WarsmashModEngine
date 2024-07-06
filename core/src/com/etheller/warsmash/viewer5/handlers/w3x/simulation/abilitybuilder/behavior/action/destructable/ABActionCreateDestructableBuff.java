package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.destructable;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionCreateDestructableBuff implements ABSingleAction {

	private ABIDCallback buffId;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onDeathActions;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final CDestructableBuff ability = new ABDestructableBuff(game.getHandleIdAllocator().createId(),
				this.buffId.callback(game, caster, localStore, castId),
				(int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL), localStore, this.onAddActions,
				this.onRemoveActions, this.onDeathActions, castId, caster);

		localStore.put(ABLocalStoreKeys.LASTCREATEDDESTBUFF, ability);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String addFunctionName = jassTextGenerator.createAnonymousFunction(this.onAddActions,
				"CreateDestructableBuffAU_OnAddActions");
		final String removeFunctionName = jassTextGenerator.createAnonymousFunction(this.onRemoveActions,
				"CreateDestructableBuffAU_OnRemoveActions");
		final String deathFunctionName = jassTextGenerator.createAnonymousFunction(this.onDeathActions,
				"CreateDestructableBuffAU_OnDeathActions");

		return "CreateDestructableBuffAU(" + jassTextGenerator.getCaster() + ", "
				+ this.buffId.generateJassEquivalent(jassTextGenerator) + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ", "
				+ jassTextGenerator.functionPointerByName(addFunctionName) + ", "
				+ jassTextGenerator.functionPointerByName(removeFunctionName) + ", "
				+ jassTextGenerator.functionPointerByName(deathFunctionName) + ", " + jassTextGenerator.getCastId()
				+ ")";
	}
}

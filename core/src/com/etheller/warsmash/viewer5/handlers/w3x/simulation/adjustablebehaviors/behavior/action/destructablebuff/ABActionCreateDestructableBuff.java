package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.destructablebuff;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionCreateDestructableBuff implements ABSingleAction {

	private ABIDCallback buffId;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onDeathActions;

	private ABBooleanCallback dispellable;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		boolean isDispellable = false;
		if (dispellable != null) {
			isDispellable = dispellable.callback(caster, localStore, castId);
		}
		CDestructableBuff ability = new ABDestructableBuff(localStore.game.getHandleIdAllocator().createId(),
				buffId.callback(caster, localStore, castId),
				castId != ABConstants.NO_CAST_ID ? localStore.getIntOrDefault(
						ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, castId),
						localStore.originAbility.getLevel()) : localStore.originAbility.getLevel(),
				localStore, onAddActions, onRemoveActions, onDeathActions, castId, caster, isDispellable);

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

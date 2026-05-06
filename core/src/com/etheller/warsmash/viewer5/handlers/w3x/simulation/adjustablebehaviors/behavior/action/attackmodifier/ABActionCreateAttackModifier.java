
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.attackmodifier;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener.ABAttackModifier;

public class ABActionCreateAttackModifier implements ABAction {

	private ABIntegerCallback priority;
	private ABBooleanCallback preLaunchCondition;
	private List<ABAction> preLaunchModification;
	private ABBooleanCallback condition;
	private List<ABAction> modification;

	private ABBooleanCallback useCastId;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		boolean isUseCastId = true;
		if (useCastId != null) {
			isUseCastId = useCastId.callback(caster, localStore, castId);
		}
		ABAttackModifier modifier = new ABAttackModifier(localStore, castId, priority, preLaunchCondition,
				preLaunchModification, condition, modification, isUseCastId);

		localStore.put(ABLocalStoreKeys.LASTCREATEDAMod, modifier);
	}
}
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitPriorityLoopData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class ABAttackModifier implements CUnitAttackModifier {
	private ABIntegerCallback priority;
	private ABBooleanCallback preLaunchCondition;
	private List<ABAction> preLaunchModification;
	private ABBooleanCallback condition;
	private List<ABAction> modification;

	private ABLocalDataStore localStore;
	private int castId = Integer.MAX_VALUE;
	private boolean useCastId;

	public ABAttackModifier(ABLocalDataStore localStore, int castId, ABIntegerCallback priority,
			ABBooleanCallback preLaunchCondition, List<ABAction> preLaunchModification, ABBooleanCallback condition,
			List<ABAction> modification, boolean useCastId) {
		this.localStore = localStore;
		this.priority = priority;
		this.preLaunchCondition = preLaunchCondition;
		this.condition = condition;
		this.preLaunchModification = preLaunchModification;
		this.modification = modification;
		this.useCastId = useCastId;
		if (useCastId) {
			this.castId = castId;
		}
	}

	@Override
	public int getPriority(CSimulation simulation, CUnit source, AbilityTarget target, CUnitAttack attack) {
		if (this.priority == null) {
			return 0;
		}
		if (!this.useCastId) {
			this.castId--;
		}
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT + this.castId, source);
		localStore.put(ABLocalStoreKeys.ATTACKTARGET + this.castId, target);
		localStore.put(ABLocalStoreKeys.THEATTACK + this.castId, attack);
		int prio = this.priority.callback(source, this.localStore, this.castId);
		localStore.remove(ABLocalStoreKeys.ATTACKINGUNIT + this.castId);
		localStore.remove(ABLocalStoreKeys.ATTACKTARGET + this.castId);
		localStore.remove(ABLocalStoreKeys.THEATTACK + this.castId);
		return prio;
	}

	@Override
	public boolean checkPreLaunchApplication(CSimulation simulation, CUnit source, AbilityTarget target,
			CUnitAttack attack) {
		if (!this.useCastId) {
			this.castId--;
		}
		boolean ret = true;
		if (this.preLaunchCondition != null) {
			localStore.put(ABLocalStoreKeys.ATTACKINGUNIT + this.castId, source);
			localStore.put(ABLocalStoreKeys.ATTACKTARGET + this.castId, target);
			localStore.put(ABLocalStoreKeys.THEATTACK + this.castId, attack);
			ret = this.preLaunchCondition.callback(source, this.localStore, this.castId);
			localStore.remove(ABLocalStoreKeys.ATTACKINGUNIT + this.castId);
			localStore.remove(ABLocalStoreKeys.ATTACKTARGET + this.castId);
			localStore.remove(ABLocalStoreKeys.THEATTACK + this.castId);
		}
		return ret;
	}

	@Override
	public void applyPreLaunchModification(CSimulation simulation, CUnit source, AbilityTarget target,
			CUnitAttack attack, CUnitAttackSettings settings, CUnitPriorityLoopData loop) {
		if (this.preLaunchModification != null) {
			localStore.put(ABLocalStoreKeys.ATTACKINGUNIT + this.castId, source);
			localStore.put(ABLocalStoreKeys.ATTACKTARGET + this.castId, target);
			localStore.put(ABLocalStoreKeys.THEATTACK + this.castId, attack);

			localStore.put(ABLocalStoreKeys.ATTACKSETTINGS + this.castId, settings);
			localStore.put(ABLocalStoreKeys.ATTACKMODLOOP + this.castId, loop);
			for (ABAction action : this.preLaunchModification) {
				action.runAction(source, this.localStore, this.castId);
			}
			localStore.remove(ABLocalStoreKeys.ATTACKINGUNIT + this.castId);
			localStore.remove(ABLocalStoreKeys.ATTACKTARGET + this.castId);
			localStore.remove(ABLocalStoreKeys.THEATTACK + this.castId);
			localStore.remove(ABLocalStoreKeys.ATTACKSETTINGS + this.castId);
			localStore.remove(ABLocalStoreKeys.ATTACKMODLOOP + this.castId);
		}
	}

	@Override
	public boolean checkApplication(CSimulation simulation, CUnit source, AbilityTarget target, CUnitAttack attack) {
		if (!this.useCastId) {
			this.castId--;
		}
		boolean ret = true;
		if (this.condition != null) {
			localStore.put(ABLocalStoreKeys.ATTACKINGUNIT + this.castId, source);
			localStore.put(ABLocalStoreKeys.ATTACKTARGET + this.castId, target);
			localStore.put(ABLocalStoreKeys.THEATTACK + this.castId, attack);
			ret = this.condition.callback(source, this.localStore, this.castId);
			localStore.remove(ABLocalStoreKeys.ATTACKINGUNIT + this.castId);
			localStore.remove(ABLocalStoreKeys.ATTACKTARGET + this.castId);
			localStore.remove(ABLocalStoreKeys.THEATTACK + this.castId);
		}
		return ret;
	}

	@Override
	public void applyModification(CSimulation simulation, CUnit source, AbilityTarget target, CUnitAttack attack,
			CUnitAttackSettings settings, CUnitPriorityLoopData loop) {
		if (this.modification != null) {
			localStore.put(ABLocalStoreKeys.ATTACKINGUNIT + this.castId, source);
			localStore.put(ABLocalStoreKeys.ATTACKTARGET + this.castId, target);
			localStore.put(ABLocalStoreKeys.THEATTACK + this.castId, attack);

			localStore.put(ABLocalStoreKeys.ATTACKSETTINGS + this.castId, settings);
			localStore.put(ABLocalStoreKeys.ATTACKMODLOOP + this.castId, loop);
			for (ABAction action : this.modification) {
				action.runAction(source, this.localStore, this.castId);
			}
			localStore.remove(ABLocalStoreKeys.ATTACKINGUNIT + this.castId);
			localStore.remove(ABLocalStoreKeys.ATTACKTARGET + this.castId);
			localStore.remove(ABLocalStoreKeys.THEATTACK + this.castId);
			localStore.remove(ABLocalStoreKeys.ATTACKSETTINGS + this.castId);
			localStore.remove(ABLocalStoreKeys.ATTACKMODLOOP + this.castId);
		}
	}

}

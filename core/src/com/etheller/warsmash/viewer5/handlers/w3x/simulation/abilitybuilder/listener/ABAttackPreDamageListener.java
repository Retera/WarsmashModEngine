package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackEffectListenerStacking;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerDamageModResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class ABAttackPreDamageListener implements CUnitAttackPreDamageListener {

	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	private int triggerId = 0;
	private boolean useCastId;
	
	public ABAttackPreDamageListener(Map<String, Object> localStore, List<ABAction> actions, int castId, boolean useCastId) {
		this.localStore = localStore;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}
	
	@Override
	public CUnitAttackEffectListenerStacking onAttack(CSimulation simulation, CUnit attacker, AbilityTarget target,
			CWeaponType weaponType, CAttackType attackType, CDamageType damageType,
			CUnitAttackPreDamageListenerDamageModResult damageResult) {
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT+triggerId, attacker);
		localStore.put(ABLocalStoreKeys.ATTACKEDUNIT+triggerId, target);
		localStore.put(ABLocalStoreKeys.WEAPONTYPE+triggerId, weaponType);
		localStore.put(ABLocalStoreKeys.ATTACKTYPE+triggerId, attackType);
		localStore.put(ABLocalStoreKeys.DAMAGETYPE+triggerId, damageType);
		localStore.put(ABLocalStoreKeys.BASEDAMAGEDEALT+triggerId, damageResult.getBaseDamage());
		localStore.put(ABLocalStoreKeys.BONUSDAMAGEDEALT+triggerId, damageResult.getBonusDamage());
		localStore.put(ABLocalStoreKeys.PREDAMAGERESULT+triggerId, damageResult);
		CUnitAttackEffectListenerStacking stacking = new CUnitAttackEffectListenerStacking();
		localStore.put(ABLocalStoreKeys.PREDAMAGESTACKING+triggerId, stacking);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, attacker, localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.ATTACKINGUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.ATTACKEDUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.WEAPONTYPE+triggerId);
		localStore.remove(ABLocalStoreKeys.ATTACKTYPE+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGETYPE+triggerId);
		localStore.remove(ABLocalStoreKeys.BASEDAMAGEDEALT+triggerId);
		localStore.remove(ABLocalStoreKeys.BONUSDAMAGEDEALT+triggerId);
		localStore.remove(ABLocalStoreKeys.PREDAMAGERESULT+triggerId);
		localStore.remove(ABLocalStoreKeys.PREDAMAGESTACKING+triggerId);
		if (!this.useCastId) {
			this.triggerId++;
		}
		return stacking;
	}

}

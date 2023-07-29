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
	
	public ABAttackPreDamageListener(Map<String, Object> localStore, List<ABAction> actions) {
		this.localStore = localStore;
		this.actions = actions;
	}
	
	@Override
	public CUnitAttackEffectListenerStacking onAttack(CSimulation simulation, CUnit attacker, AbilityTarget target,
			CWeaponType weaponType, CAttackType attackType, CDamageType damageType,
			CUnitAttackPreDamageListenerDamageModResult damageResult) {
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT, attacker);
		localStore.put(ABLocalStoreKeys.ATTACKEDUNIT, target);
		localStore.put(ABLocalStoreKeys.WEAPONTYPE, weaponType);
		localStore.put(ABLocalStoreKeys.ATTACKTYPE, attackType);
		localStore.put(ABLocalStoreKeys.DAMAGETYPE, damageType);
		localStore.put(ABLocalStoreKeys.BASEDAMAGEDEALT, damageResult.getBaseDamage());
		localStore.put(ABLocalStoreKeys.BONUSDAMAGEDEALT, damageResult.getBonusDamage());
		localStore.put(ABLocalStoreKeys.PREDAMAGERESULT, damageResult);
		CUnitAttackEffectListenerStacking stacking = new CUnitAttackEffectListenerStacking();
		localStore.put(ABLocalStoreKeys.PREDAMAGESTACKING, stacking);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, attacker, localStore);
			}
		}
		return stacking;
	}

}

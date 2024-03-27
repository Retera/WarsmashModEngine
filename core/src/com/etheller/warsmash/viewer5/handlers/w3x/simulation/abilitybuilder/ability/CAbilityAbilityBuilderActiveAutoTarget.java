package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.ABBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityAbilityBuilderActiveAutoTarget extends CAbilityAbilityBuilderGenericActive {
	private ABBehavior behavior;

	public CAbilityAbilityBuilderActiveAutoTarget(int handleId, War3ID code, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, code, alias, levelData, config, localStore);
	}
	
	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		this.behavior = this.createRangedBehavior(unit);
		super.onAdd(game, unit);
	}

	@Override
	protected boolean innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		return true;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		return true;
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		castId++;
		CWidget target = autoTarget(game, caster);
		if (target != null) {
			this.runOnOrderIssuedActions(game, caster, orderId);
			this.behavior.setCastId(castId);
			return this.behavior.reset(game, target);
		} else {
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
			return null;
		}
	}
	
	public CWidget autoTarget(CSimulation game, CUnit caster) {
		CWidget target = null;

		if (this.config.getSpecialFields() != null && this.config.getSpecialFields().getAutoAquireTarget() != null) {
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
			
			for (ABAction action : this.config.getSpecialFields().getAutoAquireTarget()) {
				action.runAction(game, caster, this.localStore, castId);
			}
			
			target = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
			if (target == null) {
				target = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
				if (target == null) {
					target = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
				}
			}
		}
		return target;
	}

	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		CWidget prevTarget = null;
		prevTarget = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
		if (prevTarget == null) {
			prevTarget = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
			if (prevTarget == null) {
				prevTarget = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
			}
		}
		if (target == prevTarget) {
			return true;
		} else {
			receiver.orderIdNotAccepted();
			return false;
		}
	}
	


	// Unused

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		return null;
	}
	
	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		return null;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
		return false;
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class ABAbilityBuilderActiveAutoTarget extends ABAbilityBuilderGenericActive {
	private ABBehavior behavior;

	public ABAbilityBuilderActiveAutoTarget(int handleId, War3ID code, War3ID alias,
			List<ABAbilityBuilderAbilityTypeLevelData> levelData, ABAbilityBuilderConfiguration config,
			ABLocalDataStore localStore) {
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
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId, boolean autoOrder) {
		this.castId = ABConstants.incrementCastId(this.castId);
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, castId), this.getLevel());
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), autoOrder);
		CWidget target = autoTarget(game, caster);
		if (target != null) {
			this.runOnOrderIssuedActions(game, caster, orderId);
			this.behavior.setCastId(castId);
			return this.behavior.reset(game, target, orderId, autoOrder);
		} else {
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, castId));
			return null;
		}
	}

	@Override
	public void internalBegin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, AbilityTarget noTarget) {
		this.castId = ABConstants.incrementCastId(this.castId);
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, castId), this.getLevel());
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), autoOrder);
		this.localStore.put(ABLocalStoreKeys.PREVIOUSBEHAVIOR, caster.getCurrentBehavior());
		CWidget target = autoTarget(game, caster);
		if (target != null) {
			this.runOnOrderIssuedActions(game, caster, orderId);
		} else {
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, castId));
		}
	}

	public CWidget autoTarget(CSimulation game, CUnit caster) {
		CWidget target = null;

		if (this.config.getSpecialFields() != null && this.config.getSpecialFields().getAutoAquireTarget() != null) {
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, castId));

			for (ABAction action : this.config.getSpecialFields().getAutoAquireTarget()) {
				action.runAction(caster, this.localStore, castId);
			}

			target = this.localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, castId),
					CWidget.class);
			if (target == null) {
				target = this.localStore.get(
						ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, castId),
						CWidget.class);
				if (target == null) {
					target = this.localStore.get(
							ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, castId), CWidget.class);
				}
			}
		}
		return target;
	}

	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		CWidget prevTarget = null;
		prevTarget = this.localStore.get(
				ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, ABConstants.NO_CAST_ID),
				CWidget.class);
		if (prevTarget == null) {
			prevTarget = this.localStore.get(
					ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, ABConstants.NO_CAST_ID),
					CWidget.class);
			if (prevTarget == null) {
				prevTarget = this.localStore.get(
						ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, ABConstants.NO_CAST_ID),
						CWidget.class);
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
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, AbilityPointTarget point) {
		return null;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
		return false;
	}

}

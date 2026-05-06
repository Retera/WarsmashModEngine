package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class ABAbilityBuilderActiveUnitTarget extends ABAbilityBuilderGenericActive {

	private ABBehavior behavior;

	public ABAbilityBuilderActiveUnitTarget(int handleId, War3ID code, War3ID alias,
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
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, CWidget target) {
		this.internalBegin(game, caster, orderId, autoOrder, target);
		this.behavior.setCastId(castId);
		return this.behavior.reset(game, target, orderId, autoOrder);
	}

	@Override
	public void internalBegin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, AbilityTarget target) {
		this.castId = ABConstants.incrementCastId(this.castId);
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, castId), this.getLevel());
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), autoOrder);
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, castId),
				target.visit(AbilityTargetVisitor.UNIT));
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, castId),
				target.visit(AbilityTargetVisitor.ITEM));
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, castId),
				target.visit(AbilityTargetVisitor.DESTRUCTABLE));
		this.localStore.put(ABLocalStoreKeys.PREVIOUSBEHAVIOR, caster.getCurrentBehavior());
		this.runOnOrderIssuedActions(game, caster, orderId);
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId, boolean autoOrder) {
		return null;
	}

	@Override
	protected boolean innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		return true;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		return true;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
		return false;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
		return false;
	}

}

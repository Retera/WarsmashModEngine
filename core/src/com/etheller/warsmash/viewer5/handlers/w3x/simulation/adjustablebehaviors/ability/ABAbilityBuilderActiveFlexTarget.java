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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class ABAbilityBuilderActiveFlexTarget extends ABAbilityBuilderGenericActive {
	private ABBehavior behavior;

	private boolean targetedSpell = false;
	private boolean pointTarget = false;
	private boolean castless;

	public ABAbilityBuilderActiveFlexTarget(int handleId, War3ID alias, War3ID code,
			List<ABAbilityBuilderAbilityTypeLevelData> levelData, ABAbilityBuilderConfiguration config,
			ABLocalDataStore localStore) {
		super(handleId, alias, code, levelData, config, localStore);
	}

	private void setTargeted(CSimulation game, CUnit unit) {
		if (config.getSpecialFields() != null && config.getSpecialFields().getTargetedSpell() != null) {
			boolean result = true;
			for (ABBooleanCallback condition : config.getSpecialFields().getTargetedSpell()) {
				result = result && condition.callback(unit, localStore, ABConstants.NO_CAST_ID);
			}
			this.targetedSpell = result;
		}
	}

	private void setPointTarget(CSimulation game, CUnit unit) {
		if (config.getSpecialFields() != null && config.getSpecialFields().getPointTargeted() != null) {
			boolean result = true;
			for (ABBooleanCallback condition : config.getSpecialFields().getPointTargeted()) {
				result = result && condition.callback(unit, localStore, ABConstants.NO_CAST_ID);
			}
			this.pointTarget = result;
		}
	}

	protected void determineCastless(CUnit unit) {
		if (this.item != null || this.config.getDisplayFields() != null
				&& this.config.getDisplayFields().getCastlessNoTarget() != null && this.config.getDisplayFields()
						.getCastlessNoTarget().callback(unit, localStore, ABConstants.NO_CAST_ID)) {
			this.castless = true;
			this.behavior = null;
		} else {
			this.castless = false;
			if (this.behavior == null || !(this.behavior instanceof ABBehaviorAbilityBuilderNoTarget)) {
				this.behavior = this.createNoTargetBehavior(unit);
			}
		}
	}

	private void setBehavior(final CUnit unit) {
		if (this.targetedSpell) {
			if (this.behavior == null || !(this.behavior instanceof ABBehaviorAbilityBuilderBase)) {
				this.behavior = this.createRangedBehavior(unit);
			}
		} else {
			this.determineCastless(unit);
		}
	}

	public boolean isTargetedSpell() {
		return this.targetedSpell;
	}

	public boolean isPointTarget() {
		return this.pointTarget;
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		this.setTargeted(game, unit);
		this.setPointTarget(game, unit);
		this.setBehavior(unit);

	}

	@Override
	public void onAddDisabled(CSimulation game, CUnit unit) {
		localStore.put(ABLocalStoreKeys.ISFLEXABILITY, this);
		super.onAddDisabled(game, unit);
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		this.behavior = new ABBehaviorAbilityBuilderBase(unit, localStore, this);
		this.setTargeted(game, unit);
		this.setPointTarget(game, unit);
		this.setBehavior(unit);
		super.onAdd(game, unit);
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId, boolean autoOrder,
			final AbilityTarget target) {
		this.localStore.put(ABLocalStoreKeys.ISAUTOCASTTARGETING, autoOrder);
		if (!this.isTargetedSpell() && castless && orderId == this.getBaseOrderId()) {
			this.runBeginCastingActions(game, caster, orderId);
			this.runEndCastingActions(game, caster, orderId);
			this.localStore.remove(ABLocalStoreKeys.ISAUTOCASTTARGETING);
			return false;
		}
		this.localStore.remove(ABLocalStoreKeys.ISAUTOCASTTARGETING);
		return super.checkBeforeQueue(game, caster, orderId, autoOrder, target);
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, CWidget target) {
		if (this.isTargetedSpell() && !this.isPointTarget()) {
			this.castId = ABConstants.incrementCastId(this.castId);
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, castId),
					this.getLevel());
			this.behavior.setCastId(this.castId);
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), autoOrder);
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, castId),
					target.visit(AbilityTargetVisitor.UNIT));
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, castId),
					target.visit(AbilityTargetVisitor.ITEM));
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, castId),
					target.visit(AbilityTargetVisitor.DESTRUCTABLE));
			this.runOnOrderIssuedActions(game, caster, orderId);
			return this.behavior.reset(game, target, orderId, autoOrder);
		} else {
			return null;
		}
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, AbilityPointTarget point) {
		if (this.isTargetedSpell() && this.isPointTarget()) {
			this.castId = ABConstants.incrementCastId(this.castId);
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, castId),
					this.getLevel());
			this.behavior.setCastId(this.castId);
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), autoOrder);
			localStore.put(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + this.castId, point);
			this.runOnOrderIssuedActions(game, caster, orderId);
			return this.behavior.reset(game, point, orderId, autoOrder);
		} else {
			return null;
		}
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId, boolean autoOrder) {
		if (!this.isTargetedSpell()) {
			if (castless) {
				return null;
			} else {
				this.castId = ABConstants.incrementCastId(this.castId);
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, castId),
						this.getLevel());
				this.behavior.setCastId(this.castId);
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), autoOrder);
				this.runOnOrderIssuedActions(game, caster, orderId);
				return this.behavior.reset(orderId, autoOrder);
			}
		} else {
			return null;
		}
	}

	@Override
	public void internalBegin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, AbilityTarget target) {
		this.castId = ABConstants.incrementCastId(this.castId);
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, castId), this.getLevel());
		this.localStore.put(ABLocalStoreKeys.PREVIOUSBEHAVIOR, caster.getCurrentBehavior());
		if (this.isTargetedSpell()) {
			if (this.isPointTarget()) {
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), autoOrder);
				localStore.put(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + this.castId,
						target.visit(AbilityTargetVisitor.POINT));
				this.runOnOrderIssuedActions(game, caster, orderId);
			} else {
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), autoOrder);
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, castId),
						target.visit(AbilityTargetVisitor.UNIT));
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, castId),
						target.visit(AbilityTargetVisitor.ITEM));
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, castId),
						target.visit(AbilityTargetVisitor.DESTRUCTABLE));
				this.runOnOrderIssuedActions(game, caster, orderId);
			}
		} else {
			if (!castless) {
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), autoOrder);
				this.runOnOrderIssuedActions(game, caster, orderId);
			}
		}
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		if (this.isTargetedSpell() && !this.isPointTarget()) {
			return true;
		} else {
			receiver.orderIdNotAccepted();
			return false;
		}
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (this.isTargetedSpell() && this.isPointTarget()) {
			return true;
		} else {
			receiver.orderIdNotAccepted();
			return false;
		}
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		if (!this.isTargetedSpell()) {
			return true;
		} else {
			receiver.orderIdNotAccepted();
			return false;
		}
	}

	@Override
	protected boolean innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		return true;
	}

}

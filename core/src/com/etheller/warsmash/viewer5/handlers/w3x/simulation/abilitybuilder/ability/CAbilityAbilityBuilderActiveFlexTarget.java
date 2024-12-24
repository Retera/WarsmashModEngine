package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.ABBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityAbilityBuilderActiveFlexTarget extends CAbilityAbilityBuilderGenericActive {
	private ABBehavior behavior;

	private boolean targetedSpell = false;
	private boolean pointTarget = false;
	private boolean castless;

	public CAbilityAbilityBuilderActiveFlexTarget(int handleId, War3ID alias, War3ID code,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, alias, code, levelData, config, localStore);
	}
	
	private void setTargeted(CSimulation game, CUnit unit) {
		if (config.getSpecialFields() != null && config.getSpecialFields().getTargetedSpell() != null) {
			boolean result = true;
			for (ABCondition condition : config.getSpecialFields().getTargetedSpell()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			this.targetedSpell = result;
		}
	}
	private void setPointTarget(CSimulation game, CUnit unit) {
		if (config.getSpecialFields() != null && config.getSpecialFields().getPointTargeted() != null) {
			boolean result = true;
			for (ABCondition condition : config.getSpecialFields().getPointTargeted()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			this.pointTarget = result;
		}
	}
	protected void determineCastless(CUnit unit) {
		if (this.item != null || this.config.getDisplayFields() != null && this.config.getDisplayFields().getCastlessNoTarget() != null
				&& this.config.getDisplayFields().getCastlessNoTarget().callback(null, unit, localStore, castId)) {
			this.castless = true;
			this.behavior = null;
		} else {
			this.castless = false;
			if (this.behavior == null || !(this.behavior instanceof CBehaviorAbilityBuilderNoTarget)) {
				this.behavior = this.createNoTargetBehavior(unit);
			}
		}
	}
	private void setBehavior(final CUnit unit) {
		if (this.targetedSpell) {
			if (this.behavior == null || !(this.behavior instanceof CBehaviorAbilityBuilderBase)) {
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
		localStore.put(ABLocalStoreKeys.FLEXABILITY, this);
		super.onAddDisabled(game, unit);
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		this.behavior = new CBehaviorAbilityBuilderBase(unit, localStore, this);
		this.setTargeted(game, unit);
		this.setPointTarget(game, unit);
		this.setBehavior(unit);
		super.onAdd(game, unit);
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		if (!this.isTargetedSpell() && castless && orderId == this.getBaseOrderId()) {
			this.runBeginCastingActions(game, caster, orderId);
			this.runEndCastingActions(game, caster, orderId);
			return false;
		}
		return super.checkBeforeQueue(game, caster, orderId, target);
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		if (this.isTargetedSpell() && !this.isPointTarget()) {
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId, target.visit(AbilityTargetVisitor.UNIT));
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId, target.visit(AbilityTargetVisitor.ITEM));
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId, target.visit(AbilityTargetVisitor.DESTRUCTABLE));
			this.runOnOrderIssuedActions(game, caster, orderId);
			return this.behavior.reset(game, target);
		} else {
			return null;
		}
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		if (this.isTargetedSpell() && this.isPointTarget()) {
			localStore.put(ABLocalStoreKeys.ABILITYTARGETEDLOCATION+this.castId, point);
			this.runOnOrderIssuedActions(game, caster, orderId);
			return this.behavior.reset(game, point);
		} else {
			return null;
		}
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		if (!this.isTargetedSpell()) {
			if (castless) {
				return null;
			} else {
				this.runOnOrderIssuedActions(game, caster, orderId);
				return this.behavior.reset();
			}
		} else {
			return null;
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

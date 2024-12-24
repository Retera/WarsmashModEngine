package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.CBehaviorNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.CBehaviorTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

@Deprecated
public class CAbilityAbilityBuilderActiveFlexTargetSimple extends CAbilitySpellBase {
	private CBehavior behavior;

	List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private AbilityBuilderConfiguration config;
	private Map<String, Object> localStore;
	private int orderId;
	private int autoCastOnId = 0;
	private int autoCastOffId = 0;
	private boolean autocasting = false;
	private boolean initialized;

	private int castId = 0;
	
	private boolean targetedSpell = false;
	private boolean pointTarget = false;

	public CAbilityAbilityBuilderActiveFlexTargetSimple(int handleId, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, alias);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		orderId = OrderIdUtils.getOrderId(config.getCastId());
		if (config.getAutoCastOnId() != null) {
			autoCastOnId = OrderIdUtils.getOrderId(config.getAutoCastOnId());
		}
		if (config.getAutoCastOffId() != null) {
			autoCastOffId = OrderIdUtils.getOrderId(config.getAutoCastOffId());
		}
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
	private void setBehavior(final CUnit unit) {
		if (this.targetedSpell) {
			if (this.behavior == null || !(this.behavior instanceof CBehaviorTargetSpellBase)) {
				this.behavior = new CBehaviorTargetSpellBase(unit, this);
			}
		} else {
			if (this.behavior == null || !(this.behavior instanceof CBehaviorNoTargetSpellBase)) {
				this.behavior = new CBehaviorNoTargetSpellBase(unit, this);
			}
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
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
	}

	@Override
	public void populateData(GameObject worldEditorAbility, int level) {
		if (this.initialized) {
			CSimulation game = (CSimulation) this.localStore.get(ABLocalStoreKeys.GAME);
			CUnit unit = (CUnit) this.localStore.get(ABLocalStoreKeys.THISUNIT);
			this.setTargeted(game, unit);
			this.setPointTarget(game, unit);
			this.setBehavior(unit);
			if (config.getOnLevelChange() != null) {
				for (ABAction action : config.getOnLevelChange()) {
					action.runAction(game, unit, this.localStore, castId);
				}
			}
		}
		this.initialized = true;
	}

	@Override
	public int getBaseOrderId() {
		return this.orderId;
	}

	public List<CAbilityTypeAbilityBuilderLevelData> getLevelData() {
		return this.levelData;
	}

	public AbilityBuilderConfiguration getConfig() {
		return this.config;
	}

	public Map<String, Object> getLocalStore() {
		return this.localStore;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		localStore.put(ABLocalStoreKeys.FLEXABILITY, this);
		this.setTargeted(game, unit);
		this.setPointTarget(game, unit);
		this.setBehavior(unit);
		localStore.put(ABLocalStoreKeys.GAME, game);
		localStore.put(ABLocalStoreKeys.THISUNIT, unit);
		if (config.getOnAddAbility() != null) {
			for (ABAction action : config.getOnAddAbility()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit unit, AbilityTarget target) {
		this.castId++;
		if (this.config.getOnBeginCasting() != null) {
			if (this.isTargetedSpell()) {
				if (this.isPointTarget() && target instanceof AbilityPointTarget) {
					localStore.put(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + this.castId, target);
				} else if (!this.isPointTarget()) {
					final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
					this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId, targetUnit);
					final CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
					this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId, targetDest);
					final CItem targetItem = target.visit(AbilityTargetVisitor.ITEM);
					this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId, targetItem);
				}
			}

			for (ABAction action : this.config.getOnBeginCasting()) {
				action.runAction(simulation, unit, this.localStore, castId);
			}

			if (this.isTargetedSpell()) {
				if (this.isPointTarget()) {
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + castId);
				} else if (!this.isPointTarget()) {
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
				}
			}
		}
		return false;
	}

	@Override
	protected void innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		if (config.getExtraCastConditions() != null) {
			boolean result = true;
			for (ABCondition condition : config.getExtraCastConditions()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			if (result) {
				receiver.useOk();
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					receiver.activationCheckFailed(failReason);
				} else {
					receiver.unknownReasonUseNotOk();
				}
			}
		} else {
			receiver.useOk();
		}
	}

	protected void innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		if (this.config.getExtraTargetConditions() != null) {
			final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId, targetUnit);
			final CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId, targetDest);
			final CItem targetItem = target.visit(AbilityTargetVisitor.ITEM);
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId, targetItem);
			boolean result = true;
			for (ABCondition condition : config.getExtraTargetConditions()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			if (result) {
				receiver.targetOk(targetUnit);
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					receiver.targetCheckFailed(failReason);
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT);
				}
			}
		} else {
			receiver.targetOk(target);
		}
	}

	protected void innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (this.config.getExtraTargetConditions() != null) {
			localStore.put(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + this.castId, target);
			boolean result = true;
			for (ABCondition condition : config.getExtraTargetConditions()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			if (result) {
				receiver.targetOk(target);
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					receiver.targetCheckFailed(failReason);
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THERE);
				}
			}
			localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + this.castId);
		} else {
			receiver.targetOk(target);
		}
	}

	@Override
	public int getAutoCastOnOrderId() {
		return this.autoCastOnId;
	}

	@Override
	public int getAutoCastOffOrderId() {
		return this.autoCastOffId;
	}

	@Override
	public boolean isAutoCastOn() {
		return this.autocasting;
	}

	@Override
	public void setAutoCastOn(final CUnit caster, final boolean autoCastOn) {
		this.autocasting = autoCastOn;
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		if (this.isTargetedSpell() && !this.isPointTarget()) {
			if (this.behavior instanceof CBehaviorTargetSpellBase) {
				return ((CBehaviorTargetSpellBase) this.behavior).reset(game, target);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		if (this.isTargetedSpell() && this.isPointTarget()) {
			if (this.behavior instanceof CBehaviorTargetSpellBase) {
				return ((CBehaviorTargetSpellBase) this.behavior).reset(game, point);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		if (!this.isTargetedSpell()) {
			if (this.behavior instanceof CBehaviorNoTargetSpellBase) {
				return ((CBehaviorNoTargetSpellBase) this.behavior).reset();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		if (this.isTargetedSpell() && !this.isPointTarget()) {
			if (target.canBeTargetedBy(game, unit, getTargetsAllowed(), receiver)) {
				if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
					this.innerCheckCanTargetSpell(game, unit, orderId, target, receiver);
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
				}
			}
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (this.isTargetedSpell() && this.isPointTarget()) {
			if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
				this.innerCheckCanTargetSpell(game, unit, orderId, target, receiver);
			} else {
				receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
			}
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		if (!this.isTargetedSpell()) {
			receiver.targetOk(null);
		} else {
			receiver.orderIdNotAccepted();
		}
	}

}

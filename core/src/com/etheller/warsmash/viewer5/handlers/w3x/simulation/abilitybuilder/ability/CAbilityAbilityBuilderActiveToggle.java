package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.ManaDepletedCheckTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.MeleeUIAbilityActivationReceiver;

@Deprecated
public class CAbilityAbilityBuilderActiveToggle extends CAbilityAbilityBuilderGenericActive {

	protected int unorderId;

	protected boolean active;

	protected int bufferMana = 0;
	protected int manaDrainedPerSecond = 0;
	protected float duration = 0;

	private ManaDepletedCheckTimer timer;
	private NonStackingStatBuff manaDrain;

	public CAbilityAbilityBuilderActiveToggle(int handleId, War3ID code, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, code, alias, levelData, config, localStore);
		unorderId = OrderIdUtils.getOrderId(config.getUncastId());
		active = false;

		CAbilityTypeAbilityBuilderLevelData levelDataLevel = levelData.get(this.getLevel() - 1);
		this.duration = levelDataLevel.getDurationNormal();
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		CAbilityTypeAbilityBuilderLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
		this.duration = levelDataLevel.getDurationNormal();
		if (config.getSpecialFields() != null) {
			if (config.getSpecialFields().getBufferManaRequired() != null) {
				this.bufferMana = config.getSpecialFields().getBufferManaRequired().callback(game, unit, localStore, castId);
			}
			if (config.getSpecialFields().getManaDrainedPerSecond() != null) {
				this.manaDrainedPerSecond = config.getSpecialFields().getManaDrainedPerSecond().callback(game, unit, localStore, castId);
			}
		}
		manaDrain.setValue((-1 * this.manaDrainedPerSecond / this.duration));
	}

	@Override
	public int getBaseOrderId() {
		return this.active ? this.unorderId : this.orderId;
	}

	@Override
	public int getUIManaCost() {
		return this.active ? 0 :this.manaCost + this.bufferMana;
	}

	@Override
	public boolean isToggleOn() {
		return this.active;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		timer = new ManaDepletedCheckTimer(unit, this);
		localStore.put(ABLocalStoreKeys.TOGGLEDABILITY, this);
		if (config.getSpecialFields() != null) {
			if (config.getSpecialFields().getBufferManaRequired() != null) {
				this.bufferMana = config.getSpecialFields().getBufferManaRequired().callback(game, unit, localStore, castId);
			}
			if (config.getSpecialFields().getManaDrainedPerSecond() != null) {
				this.manaDrainedPerSecond = config.getSpecialFields().getManaDrainedPerSecond().callback(game, unit, localStore, castId);
			}
		}
		manaDrain = new NonStackingStatBuff(NonStackingStatBuffType.MPGEN, NonStackingStatBuff.ALLOW_STACKING_KEY, (-1 * this.manaDrainedPerSecond / this.duration));
		super.onAdd(game, unit);
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		if (this.active) {
			deactivate(game, unit);
		}
		super.onRemove(game, unit);
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
		if (this.active) {
			deactivate(game, unit);
		}
		super.onDeath(game, unit);
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		if (this.active && (orderId == this.unorderId)) {
			this.runOnOrderIssuedActions(game, caster, orderId);
			deactivate(game, caster);
			return false;
		} else if (!this.active && (orderId == this.orderId)) {
			if (caster.chargeMana(this.manaCost)) {
				this.runOnOrderIssuedActions(game, caster, orderId);
				activate(game, caster);
			}
			return false;
		}
		return super.checkBeforeQueue(game, caster, orderId, target);
	}
	
	@Override
	public void activate(final CSimulation game, final CUnit caster) {
		this.castId++;
		this.active = true;
		this.startCooldown(game, caster);
		if (this.manaDrainedPerSecond > 0) {
			this.timer.start(game);
			caster.addNonStackingStatBuff(manaDrain);
		}
		if (config.getOnBeginCasting() != null) {
			for (ABAction action : config.getOnBeginCasting()) {
				action.runAction(game, caster, localStore, castId);
			}
		}
	}

	@Override
	public void deactivate(final CSimulation game, final CUnit caster) {
		this.active = false;
		if (this.manaDrainedPerSecond > 0) {
			timer.pause(game);
			caster.removeNonStackingStatBuff(manaDrain);
		}
		if (config.getOnEndCasting() != null) {
			for (ABAction action : config.getOnEndCasting()) {
				action.runAction(game, caster, localStore, castId);
			}
		}
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		return null;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
		return false;
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
		if (this.active && (orderId == this.unorderId)) {
			return true;
		} else if (!this.active && (orderId == this.orderId)) {
			return true;
		} else {
			receiver.orderIdNotAccepted();
			return false;
		}
	}

	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		int cooldownRemaining = unit.getCooldownRemainingTicks(game, getAlias());
		if (!this.active) {
			if (cooldownRemaining > 0) {
				float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getAlias())
						* WarsmashConstants.SIMULATION_STEP_TIME;
				receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME, cooldownLengthDisplay);
			} else if (unit.getMana() < (this.manaCost + this.bufferMana)) {
				receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_MANA);
			} else {
				innerCheckExtraCastConditions(game, unit, cooldownRemaining, receiver);
			}
		} else {
			if (cooldownRemaining > 0 && !(receiver instanceof MeleeUIAbilityActivationReceiver)) {
				float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getAlias())
						* WarsmashConstants.SIMULATION_STEP_TIME;
				receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME, cooldownLengthDisplay);
			}
			receiver.useOk();
		}
	}

	@Override
	protected boolean innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		return true;
	}

}

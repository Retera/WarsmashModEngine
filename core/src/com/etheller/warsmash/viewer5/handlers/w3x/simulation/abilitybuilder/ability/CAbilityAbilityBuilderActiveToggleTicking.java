package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityAbilityBuilderActiveToggleTicking extends AbstractGenericSingleIconNoSmartActiveAbility
		implements AbilityBuilderAbility, AbilityBuilderToggleAbility {

	private List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private AbilityBuilderConfiguration config;
	private Map<String, Object> localStore;

	private int orderId;
	private int unorderId;

	private boolean active;

	private int bufferMana = 0;
	private int manaDrainedPerSecond = 0;
	private int nextChargeManaTick;
	private float duration = 0;
	private float cooldown = 0;
	private int manaCost = 0;
	
	private int castId = 0;

	public CAbilityAbilityBuilderActiveToggleTicking(int handleId, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, alias);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		orderId = OrderIdUtils.getOrderId(config.getCastId());
		unorderId = OrderIdUtils.getOrderId(config.getUncastId());
		active = false;

		CAbilityTypeAbilityBuilderLevelData levelDataLevel = levelData.get(this.getLevel() - 1);
		this.duration = levelDataLevel.getDurationNormal();
		this.manaCost = levelDataLevel.getManaCost();
		this.cooldown = levelDataLevel.getCooldown();
		if (config.getSpecialFields() != null && config.getSpecialFields().getBufferManaRequired() != null) {
			try {
				String bufferManaStr = levelDataLevel.getData().get(config.getSpecialFields().getBufferManaRequired());
				String perSecManaStr = levelDataLevel.getData()
						.get(config.getSpecialFields().getManaDrainedPerSecond());
				this.bufferMana = Integer.parseInt(bufferManaStr);
				this.manaDrainedPerSecond = Integer.parseInt(perSecManaStr);
			} catch (Exception e) {

			}
		}
	}

	@Override
	public void setLevel(int level) {
		super.setLevel(level);
		this.localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
		CAbilityTypeAbilityBuilderLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
		this.duration = levelDataLevel.getDurationNormal();
		this.manaCost = levelDataLevel.getManaCost();
		this.cooldown = levelDataLevel.getCooldown();

		try {
			String bufferManaStr = levelDataLevel.getData().get(this.config.getSpecialFields().getBufferManaRequired());
			String perSecManaStr = levelDataLevel.getData()
					.get(this.config.getSpecialFields().getManaDrainedPerSecond());
			this.bufferMana = Integer.parseInt(bufferManaStr);
			this.manaDrainedPerSecond = Integer.parseInt(perSecManaStr);
		} catch (Exception e) {

		}
	}

	@Override
	public int getBaseOrderId() {
		return this.active ? this.unorderId : this.orderId;
	}

	@Override
	public int getUIManaCost() {
		return this.active ? 0 :this.manaCost;
	}

	@Override
	public boolean isToggleOn() {
		return this.active;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		if (this.config.getOnAddAbility() != null) {
			for (ABAction action : this.config.getOnAddAbility()) {
				action.runAction(game, unit, this.localStore, castId);
			}
		}
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		if (this.active) {
			deactivate(game, unit);
		}
		if (this.config.getOnRemoveAbility() != null) {
			for (ABAction action : this.config.getOnRemoveAbility()) {
				action.runAction(game, unit, this.localStore, castId);
			}
		}
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		if (this.active) {
			final int currentTick = game.getGameTurnTick();
			if (currentTick >= this.nextChargeManaTick) {
				final int delayTicks = (int) (this.duration / WarsmashConstants.SIMULATION_STEP_TIME);
				this.nextChargeManaTick = currentTick + delayTicks;
				if (unit.getMana() >= this.manaDrainedPerSecond) {
					unit.setMana(unit.getMana() - this.manaDrainedPerSecond);
				} else {
					deactivate(game, unit);
				}
			}
		}
		if (config.getOnTickPreCast() != null) {
			for (ABAction action : config.getOnTickPreCast()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
		if (this.active) {
			deactivate(game, unit);
		}
		if (config.getOnDeathPreCast() != null) {
			for (ABAction action : config.getOnDeathPreCast()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		if (this.active && (orderId == this.unorderId)) {
			deactivate(game, caster);
			return false;
		} else if (!this.active && (orderId == this.orderId)) {
			if (caster.chargeMana(this.getUIManaCost())) {
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
	}

	@Override
	public void deactivate(final CSimulation game, final CUnit caster) {
		this.active = false;
		this.startCooldown(game, caster);
	}

	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {
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
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		if (this.active && (orderId == this.unorderId)) {
			receiver.targetOk(null);
		} else if (!this.active && (orderId == this.orderId)) {
			receiver.targetOk(null);
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		if (!this.active) {
			int cooldownRemaining = unit.getCooldownRemainingTicks(game, getAlias());
			if (cooldownRemaining > 0) {
				receiver.cooldownNotYetReady(cooldownRemaining, this.cooldown);
			} else if (unit.getMana() < (this.manaCost + this.bufferMana)) {
				receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_MANA);
			} else if (config.getExtraCastConditions() != null) {
				boolean result = true;
				for (ABCondition condition : config.getExtraCastConditions()) {
					result = result && condition.evaluate(game, unit, localStore, castId);
				}
				if (result) {
					receiver.useOk();
				} else {
					receiver.unknownReasonUseNotOk();
				}
			} else {
				receiver.useOk();
			}
		} else {
			receiver.useOk();
		}
	}

	public void startCooldown(CSimulation game, CUnit unit) {
		unit.beginCooldown(game, getAlias(), this.cooldown);
	}

	@Override
	public List<CAbilityTypeAbilityBuilderLevelData> getLevelData() {
		return levelData;
	}

	@Override
	public AbilityBuilderConfiguration getConfig() {
		return config;
	}

	@Override
	public Map<String, Object> getLocalStore() {
		return localStore;
	}

}

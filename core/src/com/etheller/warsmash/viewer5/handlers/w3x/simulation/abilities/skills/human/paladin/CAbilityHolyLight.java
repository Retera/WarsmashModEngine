package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.human.paladin.CBehaviorHolyLight;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CAbilityHolyLight extends AbstractGenericSingleIconNoSmartActiveAbility {
	private int manaCost;
	private int healAmount;
	private float castRange;
	private float cooldown;
	private EnumSet<CTargetType> targetsAllowed;
	private float cooldownRemaining;
	private CBehaviorHolyLight behaviorHolyLight;

	public CAbilityHolyLight(final int handleId, final War3ID alias, final int manaCost, final int healAmount,
			final float castRange, final float cooldown, final EnumSet<CTargetType> targetsAllowed) {
		super(handleId, alias);
		this.manaCost = manaCost;
		this.healAmount = healAmount;
		this.castRange = castRange;
		this.cooldown = cooldown;
		this.targetsAllowed = targetsAllowed;
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.holybolt;
	}

	@Override
	public boolean isToggleOn() {
		return false;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorHolyLight = new CBehaviorHolyLight(unit, this);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		if (this.cooldownRemaining > 0) {
			this.cooldownRemaining -= WarsmashConstants.SIMULATION_STEP_TIME;
		}
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {

	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behaviorHolyLight.reset(target);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if ((target instanceof CUnit) && target.canBeTargetedBy(game, unit, this.targetsAllowed)) {
			if (!unit.isMovementDisabled() || unit.canReach(target, this.castRange)) {
				final CUnit targetUnit = (CUnit) target;
				final CPlayer player = game.getPlayer(unit.getPlayerIndex());
				final boolean undead = targetUnit.getClassifications().contains(CUnitClassification.UNDEAD);
				final boolean ally = player.hasAlliance(targetUnit.getPlayerIndex(), CAllianceType.PASSIVE);
				if (undead != ally) {
					if (ally && (targetUnit.getLife() >= targetUnit.getMaximumLife())) {
						receiver.alreadyFullHealth();
					}
					else {
						receiver.targetOk(targetUnit);
					}
				}
				else {
					receiver.notHolyBoltTarget();
				}
			}
			else {
				receiver.targetOutsideRange();
			}
		}
		else {
			receiver.mustTargetType(TargetType.UNIT);
		}
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if (this.cooldownRemaining > 0) {
			receiver.cooldownNotYetReady(this.cooldownRemaining, this.cooldown);
		}
		else if (unit.getMana() < this.manaCost) {
			receiver.notEnoughResources(ResourceType.MANA);
		}
		else {
			receiver.useOk();
		}
	}

	public void setManaCost(final int manaCost) {
		this.manaCost = manaCost;
	}

	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	public void setCooldown(final float cooldown) {
		this.cooldown = cooldown;
	}

	public void setHealAmount(final int healAmount) {
		this.healAmount = healAmount;
	}

	public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	public void setCooldownRemaining(final float cooldownRemaining) {
		this.cooldownRemaining = cooldownRemaining;
	}

	public int getManaCost() {
		return this.manaCost;
	}

	@Override
	public int getUIManaCost() {
		return getManaCost();
	}

	public int getHealAmount() {
		return this.healAmount;
	}

	public float getCastRange() {
		return this.castRange;
	}

	public float getCooldown() {
		return this.cooldown;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	public float getCooldownRemaining() {
		return this.cooldownRemaining;
	}

}

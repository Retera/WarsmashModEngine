package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.skills.human.mountainking.CBehaviorThunderBolt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CAbilityThunderBolt extends AbstractGenericSingleIconNoSmartActiveAbility {
	private int manaCost;
	private float damage;
	private float castRange;
	private float cooldown;
	private float duration;
	private float heroDuration;
	private float projectileSpeed;
	private boolean projectileHomingEnabled;
	private War3ID buffId;
	private EnumSet<CTargetType> targetsAllowed;
	private float cooldownRemaining;
	private CBehaviorThunderBolt behaviorThunderBolt;

	public CAbilityThunderBolt(int handleId, War3ID alias, int manaCost, float damage, float castRange, float cooldown,
			float duration, float heroDuration, float projectileSpeed, boolean projectileHomingEnabled, War3ID buffId,
			EnumSet<CTargetType> targetsAllowed) {
		super(handleId, alias);
		this.manaCost = manaCost;
		this.damage = damage;
		this.castRange = castRange;
		this.cooldown = cooldown;
		this.duration = duration;
		this.heroDuration = heroDuration;
		this.projectileSpeed = projectileSpeed;
		this.projectileHomingEnabled = projectileHomingEnabled;
		this.buffId = buffId;
		this.targetsAllowed = targetsAllowed;
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.thunderbolt;
	}

	@Override
	public boolean isToggleOn() {
		return false;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorThunderBolt = new CBehaviorThunderBolt(unit, this);
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
		return this.behaviorThunderBolt.reset(target);
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
		if(target instanceof CUnit) {
			if (target.canBeTargetedBy(game, unit, this.targetsAllowed, receiver)) {
				if (!unit.isMovementDisabled() || unit.canReach(target, this.castRange)) {
					receiver.targetOk(target);
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
				}
			}
		} else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
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
		} else if (unit.getMana() < this.manaCost) {
			receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_MANA);
		} else {
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

	public void setDamage(float damage) {
		this.damage = damage;
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

	public float getDamage() {
		return damage;
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

	public float getDuration(CUnit unitTarget) {
		if (unitTarget.isHero()) {
			return heroDuration;
		}
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public float getHeroDuration() {
		return heroDuration;
	}

	public void setHeroDuration(float heroDuration) {
		this.heroDuration = heroDuration;
	}

	public float getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setProjectileSpeed(float projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}

	public boolean isProjectileHomingEnabled() {
		return projectileHomingEnabled;
	}

	public void setProjectileHomingEnabled(boolean projectileHomingEnabled) {
		this.projectileHomingEnabled = projectileHomingEnabled;
	}

	public War3ID getBuffId() {
		return buffId;
	}

	public void setBuffId(War3ID buffId) {
		this.buffId = buffId;
	}
}

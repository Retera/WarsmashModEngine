package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.demonhunter;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityImmolation extends AbstractGenericSingleIconNoSmartActiveAbility {
	private float bufferManaRequired;
	private float damagePerInterval;
	private float manaDrainedPerSecond;
	private float areaOfEffect;
	private int manaCost;
	private float duration;
	private EnumSet<CTargetType> targetsAllowed;

	private boolean active;

	public CAbilityImmolation(final int handleId, final War3ID alias, final float bufferManaRequired,
			final float damagePerInterval, final float manaDrainedPerSecond, final float areaOfEffect,
			final int manaCost, final float duration, final EnumSet<CTargetType> targetsAllowed) {
		super(handleId, alias);
		this.bufferManaRequired = bufferManaRequired;
		this.damagePerInterval = damagePerInterval;
		this.manaDrainedPerSecond = manaDrainedPerSecond;
		this.areaOfEffect = areaOfEffect;
		this.manaCost = manaCost;
		this.duration = duration;
		this.targetsAllowed = targetsAllowed;
	}

	@Override
	public int getBaseOrderId() {
		return this.active ? OrderIds.unimmolation : OrderIds.immolation;
	}

	@Override
	public boolean isToggleOn() {
		return this.active;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		if (this.active && (orderId == OrderIds.unimmolation)) {
			this.active = false;
			return false;
		}
		else if (!this.active && (orderId == OrderIds.immolation)) {
			this.active = true;
			return false;
		}
		return super.checkBeforeQueue(game, caster, orderId, target);
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
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
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if (this.active && (orderId == OrderIds.unimmolation)) {
			receiver.targetOk(null);
		}
		else if (!this.active && (orderId == OrderIds.immolation)) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	public float getBufferManaRequired() {
		return this.bufferManaRequired;
	}

	public void setBufferManaRequired(final float bufferManaRequired) {
		this.bufferManaRequired = bufferManaRequired;
	}

	public float getDamagePerInterval() {
		return this.damagePerInterval;
	}

	public void setDamagePerInterval(final float damagePerInterval) {
		this.damagePerInterval = damagePerInterval;
	}

	public float getManaDrainedPerSecond() {
		return this.manaDrainedPerSecond;
	}

	public void setManaDrainedPerSecond(final float manaDrainedPerSecond) {
		this.manaDrainedPerSecond = manaDrainedPerSecond;
	}

	public float getAreaOfEffect() {
		return this.areaOfEffect;
	}

	public void setAreaOfEffect(final float areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}

	public int getManaCost() {
		return this.manaCost;
	}

	public void setManaCost(final int manaCost) {
		this.manaCost = manaCost;
	}

	public float getDuration() {
		return this.duration;
	}

	public void setDuration(final float duration) {
		this.duration = duration;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}
}

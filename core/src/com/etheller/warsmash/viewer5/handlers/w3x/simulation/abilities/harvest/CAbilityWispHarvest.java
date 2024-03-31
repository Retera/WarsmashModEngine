package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityWispHarvest extends AbstractGenericSingleIconActiveAbility {
	public static final EnumSet<CTargetType> TREE_ALIVE_TYPE_ONLY = EnumSet.of(CTargetType.TREE, CTargetType.ALIVE);

	private int lumberPerInterval;
	private float artAttachmentHeight;
	private float castRange;
	private float periodicIntervalLength;
	private int periodicIntervalLengthTicks;
	private CBehaviorWispHarvest behaviorWispHarvest;

	public CAbilityWispHarvest(final int handleId, final War3ID code, final War3ID alias, final int lumberPerInterval,
			final float artAttachmentHeight, final float castRange, final float periodicIntervalLength) {
		super(handleId, code, alias);
		this.lumberPerInterval = lumberPerInterval;
		this.artAttachmentHeight = artAttachmentHeight;
		this.castRange = castRange;
		this.periodicIntervalLength = periodicIntervalLength;
		this.periodicIntervalLengthTicks = (int) (periodicIntervalLength / WarsmashConstants.SIMULATION_STEP_TIME);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorWispHarvest = new CBehaviorWispHarvest(unit, this);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behaviorWispHarvest.reset(game, target);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public int getBaseOrderId() {
		return isToggleOn() ? OrderIds.returnresources : OrderIds.wispharvest;
	}

	@Override
	public boolean isToggleOn() {
		return false;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target instanceof CDestructable) {
			if (target.canBeTargetedBy(game, unit, TREE_ALIVE_TYPE_ONLY, receiver)) {
				receiver.targetOk(target);
			}
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_TREE);
		}
	}

	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		innerCheckCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if ((orderId == OrderIds.returnresources) && isToggleOn()) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	public float getArtAttachmentHeight() {
		return this.artAttachmentHeight;
	}

	public float getPeriodicIntervalLength() {
		return this.periodicIntervalLength;
	}

	public int getPeriodicIntervalLengthTicks() {
		return this.periodicIntervalLengthTicks;
	}

	public int getLumberPerInterval() {
		return this.lumberPerInterval;
	}

	public float getCastRange() {
		return this.castRange;
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	public void setLumberPerInterval(final int lumberPerInterval) {
		this.lumberPerInterval = lumberPerInterval;
	}

	public void setArtAttachmentHeight(final float artAttachmentHeight) {
		this.artAttachmentHeight = artAttachmentHeight;
	}

	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	public void setPeriodicIntervalLength(final float periodicIntervalLength) {
		this.periodicIntervalLength = periodicIntervalLength;
		this.periodicIntervalLengthTicks = (int) (periodicIntervalLength / WarsmashConstants.SIMULATION_STEP_TIME);
	}

	@Override
	public boolean isPhysical() {
		return true;
	}

	@Override
	public boolean isUniversal() {
		return false;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}

}

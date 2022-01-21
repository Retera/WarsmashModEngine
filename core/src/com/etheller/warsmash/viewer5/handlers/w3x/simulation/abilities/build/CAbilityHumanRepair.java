package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build.CBehaviorHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityHumanRepair extends AbstractGenericSingleIconActiveAbility {
	private EnumSet<CTargetType> targetsAllowed;
	private float navalRangeBonus;
	private float repairCostRatio;
	private float repairTimeRatio;
	private float castRange;
	private CBehaviorHumanRepair behaviorRepair;

	public CAbilityHumanRepair(final int handleId, final War3ID alias, final EnumSet<CTargetType> targetsAllowed,
			final float navalRangeBonus, final float repairCostRatio, final float repairTimeRatio,
			final float castRange) {
		super(handleId, alias);
		this.targetsAllowed = targetsAllowed;
		this.navalRangeBonus = navalRangeBonus;
		this.repairCostRatio = repairCostRatio;
		this.repairTimeRatio = repairTimeRatio;
		this.castRange = castRange;
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target.canBeTargetedBy(game, unit, this.targetsAllowed) && (target.getLife() < target.getMaxLife())) {
			receiver.targetOk(target);
		}
		else {
			receiver.orderIdNotAccepted();
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
		receiver.mustTargetType(AbilityTargetCheckReceiver.TargetType.UNIT);
	}

	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		innerCheckCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorRepair = new CBehaviorHumanRepair(unit, this);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {

	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behaviorRepair.reset(target);
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
	public int getBaseOrderId() {
		return OrderIds.repair;
	}

	@Override
	public boolean isToggleOn() {
		return false;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	public float getNavalRangeBonus() {
		return this.navalRangeBonus;
	}

	public float getRepairCostRatio() {
		return this.repairCostRatio;
	}

	public float getRepairTimeRatio() {
		return this.repairTimeRatio;
	}

	public float getCastRange() {
		return this.castRange;
	}

	public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	public void setNavalRangeBonus(final float navalRangeBonus) {
		this.navalRangeBonus = navalRangeBonus;
	}

	public void setRepairCostRatio(final float repairCostRatio) {
		this.repairCostRatio = repairCostRatio;
	}

	public void setRepairTimeRatio(final float repairTimeRatio) {
		this.repairTimeRatio = repairTimeRatio;
	}

	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.CAutocastAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build.CBehaviorRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityRepair extends AbstractGenericSingleIconActiveAbility implements CAutocastAbility {
	private EnumSet<CTargetType> targetsAllowed;
	private float navalRangeBonus;
	private float repairCostRatio;
	private float repairTimeRatio;
	private float castRange;
	private CBehaviorRepair behaviorRepair;
	private boolean autocasting = false;

	public CAbilityRepair(final int handleId, final War3ID code, final War3ID alias, final EnumSet<CTargetType> targetsAllowed,
			final float navalRangeBonus, final float repairCostRatio, final float repairTimeRatio,
			final float castRange) {
		super(handleId, code, alias);
		this.targetsAllowed = targetsAllowed;
		this.navalRangeBonus = navalRangeBonus;
		this.repairCostRatio = repairCostRatio;
		this.repairTimeRatio = repairTimeRatio;
		this.castRange = castRange;
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if(target.getLife() < target.getMaxLife()) {
			if (target.canBeTargetedBy(game, unit, this.targetsAllowed, receiver)) {
				final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
				if ((targetUnit != null) && targetUnit.isConstructing()) {
					if(orderId == OrderIds.smart) {
						receiver.orderIdNotAccepted();
					} else {
						receiver.targetCheckFailed(CommandStringErrorKeys.THAT_BUILDING_IS_CURRENTLY_UNDER_CONSTRUCTION);
					}
				}
				else {
					receiver.targetOk(target);
				}
			}
			// else receiver called by canBeTargetedBy
		}
		else {
			if(orderId == OrderIds.smart) {
				receiver.orderIdNotAccepted();
			} else {
				receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_NOT_DAMAGED);
			}
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
		receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
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
		this.behaviorRepair = new CBehaviorRepair(unit, this);
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
		return this.behaviorRepair.reset(game, target);
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

	@Override
	public AutocastType getAutocastType() {
		return AutocastType.NEARESTVALID;
	}

	@Override
	public void setAutoCastOn(final CUnit caster, final boolean autoCastOn) {
		this.autocasting = autoCastOn;
		caster.setAutocastAbility(autoCastOn ? this : null);
	}

	@Override
	public boolean isAutoCastOn() {
		return autocasting ;
	}

	@Override
	public void setAutoCastOff() {
		this.autocasting = false;
	}

	@Override
	public int getAutoCastOnOrderId() {
		return OrderIds.repairon;
	}

	@Override
	public int getAutoCastOffOrderId() {
		return OrderIds.repairoff;
	}

	@Override
	public void checkCanAutoTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		this.checkCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	public void checkCanAutoTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanAutoTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
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

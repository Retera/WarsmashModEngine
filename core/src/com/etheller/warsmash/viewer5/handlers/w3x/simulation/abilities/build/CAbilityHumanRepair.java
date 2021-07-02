package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build;

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

import java.util.EnumSet;

public class CAbilityHumanRepair extends AbstractGenericSingleIconActiveAbility {
    private EnumSet<CTargetType> targetsAllowed;
    private final float navalRangeBonus;
    private final float repairCostRatio;
    private final float repairTimeRatio;
    private final float castRange;
    private CBehaviorHumanRepair behaviorRepair;

    public CAbilityHumanRepair(int handleId, War3ID alias, EnumSet<CTargetType> targetsAllowed,
                               float navalRangeBonus, float repairCostRatio, float repairTimeRatio,
                               float castRange) {
        super(handleId, alias);
        this.targetsAllowed = targetsAllowed;
        this.navalRangeBonus = navalRangeBonus;
        this.repairCostRatio = repairCostRatio;
        this.repairTimeRatio = repairTimeRatio;
        this.castRange = castRange;
    }

    @Override
    protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target, AbilityTargetCheckReceiver<CWidget> receiver) {
        if(target.canBeTargetedBy(game, unit, targetsAllowed) && target.getLife() < target.getMaxLife()) {
            receiver.targetOk(target);
        } else {
            receiver.orderIdNotAccepted();
        }
    }

    @Override
    protected void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId, CWidget target, AbilityTargetCheckReceiver<CWidget> receiver) {
        innerCheckCanTarget(game, unit, orderId, target, receiver);
    }

    @Override
    protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target, AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
        receiver.mustTargetType(AbilityTargetCheckReceiver.TargetType.UNIT);
    }

    @Override
    protected void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target, AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
        innerCheckCanTarget(game, unit, orderId, target, receiver);
    }

    @Override
    protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId, AbilityTargetCheckReceiver<Void> receiver) {
        receiver.orderIdNotAccepted();
    }

    @Override
    protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
        receiver.useOk();
    }

    @Override
    public void onAdd(CSimulation game, CUnit unit) {
        behaviorRepair = new CBehaviorHumanRepair(unit, this);
    }

    @Override
    public void onRemove(CSimulation game, CUnit unit) {

    }

    @Override
    public void onTick(CSimulation game, CUnit unit) {

    }

    @Override
    public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {

    }

    @Override
    public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
        return behaviorRepair.reset(target);
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
    public int getBaseOrderId() {
        return OrderIds.repair;
    }

    @Override
    public boolean isToggleOn() {
        return false;
    }

    public EnumSet<CTargetType> getTargetsAllowed() {
        return targetsAllowed;
    }

    public float getNavalRangeBonus() {
        return navalRangeBonus;
    }

    public float getRepairCostRatio() {
        return repairCostRatio;
    }

    public float getRepairTimeRatio() {
        return repairTimeRatio;
    }

    public float getCastRange() {
        return castRange;
    }
}

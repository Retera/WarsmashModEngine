package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityItemHeal extends AbstractGenericSingleIconNoSmartActiveAbility {
    private final int lifeToRegain;

    public CAbilityItemHeal(int handleId, War3ID alias, int lifeToRegain) {
        super(handleId, alias);
        this.lifeToRegain = lifeToRegain;
    }

    @Override
    protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target, AbilityTargetCheckReceiver<CWidget> receiver) {
        receiver.orderIdNotAccepted();
    }

    @Override
    protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target, AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
        receiver.orderIdNotAccepted();
    }

    @Override
    protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId, AbilityTargetCheckReceiver<Void> receiver) {
        if(orderId == getBaseOrderId()) {
            receiver.targetOk(null);
        } else {
            receiver.orderIdNotAccepted();
        }
    }

    @Override
    protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
        receiver.useOk();
    }

    @Override
    public int getBaseOrderId() {
        return OrderIds.heal;
    }

    @Override
    public boolean isToggleOn() {
        return false;
    }

    @Override
    public void onAdd(CSimulation game, CUnit unit) {

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
    public boolean checkBeforeQueue(CSimulation game, CUnit caster, int orderId, AbilityTarget target) {
        if(target == null && orderId == getBaseOrderId()) {
            caster.heal(game, lifeToRegain);
            game.createSpellEffectOnUnit(caster, getAlias());
            return false;
        }
        return super.checkBeforeQueue(game, caster, orderId, target);
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
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CAbilityItemChestOfGold extends CAbilitySpellBase {
    private int goldToGain;

    public CAbilityItemChestOfGold(final int handleId, final War3ID alias) {
        super(handleId, alias);
    }

    @Override
    public void populateData(final GameObject worldEditorAbility, final int level) {
        this.goldToGain = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
    }

    @Override
    public void onAdd(final CSimulation game, final CUnit unit) {
    }

    @Override
    public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId, final boolean autoCast, final AbilityTarget target) {
        if (orderId == OrderIds.itemgivegold) {
            final CPlayer player = game.getPlayer(caster.getPlayerIndex());
            game.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
            game.unitSoundEffectEvent(caster, getAlias());
            player.addGold(this.goldToGain);
            game.unitGainResourceEvent(caster, player.getId(), ResourceType.GOLD, this.goldToGain);
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
        return false; // handled in "checkBeforeQueue"
    }

    @Override
    public void onRemove(final CSimulation game, final CUnit unit) {
    }

    @Override
    public void onTick(final CSimulation game, final CUnit unit) {
    }

    @Override
    public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final boolean autoCast, final CWidget target) {
        return null;
    }

    @Override
    public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final boolean autoCast,
                           final AbilityPointTarget point) {
        return null;
    }

    @Override
    public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId, final boolean autoCast) {
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
        if (orderId == OrderIds.itemgivegold) {
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

    @Override
    public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
    }

    @Override
    public void onDeath(final CSimulation game, final CUnit cUnit) {
    }

    @Override
    public int getBaseOrderId() {
        return OrderIds.itemgivegold;
    }

    public int getgoldToGain() {
        return this.goldToGain;
    }

    public void setgoldToGain(final int goldToGain) {
        this.goldToGain = goldToGain;
    }
}

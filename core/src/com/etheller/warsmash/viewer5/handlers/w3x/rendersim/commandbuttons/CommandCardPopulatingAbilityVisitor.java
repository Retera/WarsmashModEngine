package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGeneric;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CommandCardPopulatingAbilityVisitor implements CAbilityVisitor<Void> {
    public static final CommandCardPopulatingAbilityVisitor INSTANCE = new CommandCardPopulatingAbilityVisitor();

    private CSimulation game;
    private CUnit unit;

    private CommandButtonListener commandButtonListener;
    private AbilityDataUI abilityDataUI;
    private boolean hasStop;

    public CommandCardPopulatingAbilityVisitor reset(final CSimulation game, final CUnit unit,
                                                     final CommandButtonListener commandButtonListener, final AbilityDataUI abilityDataUI) {
        this.game = game;
        this.unit = unit;
        this.commandButtonListener = commandButtonListener;
        this.abilityDataUI = abilityDataUI;
        this.hasStop = false;
        return this;
    }

    @Override
    public Void accept(final CAbilityAttack ability) {
        addCommandButton(ability, this.abilityDataUI.getAttackUI(), ability.getHandleId(), OrderIds.attack, 0, false);
        if (!this.hasStop) {
            this.hasStop = true;
            addCommandButton(null, this.abilityDataUI.getStopUI(), 0, OrderIds.stop, 0, false);
        }
        return null;
    }

    @Override
    public Void accept(final CAbilityMove ability) {
        addCommandButton(ability, this.abilityDataUI.getMoveUI(), ability.getHandleId(), OrderIds.move, 0, false);
        addCommandButton(ability, this.abilityDataUI.getHoldPosUI(), ability.getHandleId(), OrderIds.holdposition, 0,
                false);
        addCommandButton(ability, this.abilityDataUI.getPatrolUI(), ability.getHandleId(), OrderIds.patrol, 0, false);
        if (!this.hasStop) {
            this.hasStop = true;
            addCommandButton(null, this.abilityDataUI.getStopUI(), 0, OrderIds.stop, 0, false);
        }
        return null;
    }

    @Override
    public Void accept(final CAbilityGeneric ability) {
        addCommandButton(ability, this.abilityDataUI.getUI(ability.getRawcode()).getOnIconUI(), ability.getHandleId(),
                OrderIds.channel, 0, false);
        return null;
    }

    @Override
    public Void accept(final CAbilityColdArrows ability) {
        final boolean autoCastActive = ability.isAutoCastActive();
        int autoCastId;
        if (autoCastActive) {
            autoCastId = OrderIds.coldarrows;
        } else {
            autoCastId = OrderIds.uncoldarrows;
        }
        addCommandButton(ability, this.abilityDataUI.getUI(ability.getRawcode()).getOnIconUI(), ability.getHandleId(),
                OrderIds.coldarrowstarg, autoCastId, autoCastActive);
        return null;
    }

    private void addCommandButton(final CAbility ability, final IconUI iconUI, final int handleId, final int orderId,
                                  final int autoCastOrderId, final boolean autoCastActive) {
        final boolean active = (this.unit.getCurrentBehavior() != null && orderId == this.unit.getCurrentBehavior().getHighlightOrderId());
        this.commandButtonListener.commandButton(iconUI.getButtonPositionX(), iconUI.getButtonPositionY(),
                iconUI.getIcon(), handleId, orderId, autoCastOrderId, active, autoCastActive);
    }
}

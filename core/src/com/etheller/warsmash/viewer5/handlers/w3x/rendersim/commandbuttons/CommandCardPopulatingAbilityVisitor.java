package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;

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
		addCommandButton(ability, this.abilityDataUI.getAttackUI(), ability.getHandleId(), OrderIds.attack);
		if (!this.hasStop) {
			this.hasStop = true;
			addCommandButton(null, this.abilityDataUI.getStopUI(), -1, OrderIds.stop);
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityMove ability) {
		addCommandButton(ability, this.abilityDataUI.getMoveUI(), ability.getHandleId(), OrderIds.move);
		addCommandButton(ability, this.abilityDataUI.getHoldPosUI(), ability.getHandleId(), OrderIds.holdposition);
		addCommandButton(ability, this.abilityDataUI.getPatrolUI(), ability.getHandleId(), OrderIds.patrol);
		if (!this.hasStop) {
			this.hasStop = true;
			addCommandButton(null, this.abilityDataUI.getStopUI(), -1, OrderIds.stop);
		}
		return null;
	}

	private void addCommandButton(final CAbility ability, final IconUI iconUI, final int handleId, final int orderId) {
		final boolean active;
		if (this.unit.getCurrentOrder() == null) {
			active = (orderId == OrderIds.stop);
		}
		else {
			if (ability == null) {
				active = false;
			}
			else {
				ability.checkCanUse(this.game, this.unit, orderId, BooleanAbilityActivationReceiver.INSTANCE);
				active = BooleanAbilityActivationReceiver.INSTANCE.isOk();
			}
		}
		this.commandButtonListener.commandButton(iconUI.getButtonPositionX(), iconUI.getButtonPositionY(),
				iconUI.getIcon(), handleId, orderId, active);
	}
}

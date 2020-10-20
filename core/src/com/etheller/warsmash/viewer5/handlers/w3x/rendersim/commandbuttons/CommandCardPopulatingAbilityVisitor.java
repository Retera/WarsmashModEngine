package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CommandCardPopulatingAbilityVisitor implements CAbilityVisitor<Void> {
	public static final CommandCardPopulatingAbilityVisitor INSTANCE = new CommandCardPopulatingAbilityVisitor();

	private CommandButtonListener commandButtonListener;
	private AbilityDataUI abilityDataUI;
	private boolean hasStop;

	public CommandCardPopulatingAbilityVisitor reset(final CommandButtonListener commandButtonListener,
			final AbilityDataUI abilityDataUI) {
		this.commandButtonListener = commandButtonListener;
		this.abilityDataUI = abilityDataUI;
		this.hasStop = false;
		return this;
	}

	@Override
	public Void accept(final CAbilityAttack ability) {
		addCommandButton(this.abilityDataUI.getAttackUI(), ability.getHandleId(), OrderIds.attack);
		if (!this.hasStop) {
			this.hasStop = true;
			addCommandButton(this.abilityDataUI.getStopUI(), -1, OrderIds.stop);
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityMove ability) {
		addCommandButton(this.abilityDataUI.getMoveUI(), ability.getHandleId(), OrderIds.move);
		addCommandButton(this.abilityDataUI.getHoldPosUI(), ability.getHandleId(), OrderIds.holdposition);
		addCommandButton(this.abilityDataUI.getPatrolUI(), ability.getHandleId(), OrderIds.patrol);
		if (!this.hasStop) {
			this.hasStop = true;
			addCommandButton(this.abilityDataUI.getStopUI(), -1, OrderIds.stop);
		}
		return null;
	}

	private void addCommandButton(final IconUI iconUI, final int handleId, final int orderId) {
		this.commandButtonListener.commandButton(iconUI.getButtonPositionX(), iconUI.getButtonPositionY(),
				iconUI.getIcon(), orderId);
	}
}

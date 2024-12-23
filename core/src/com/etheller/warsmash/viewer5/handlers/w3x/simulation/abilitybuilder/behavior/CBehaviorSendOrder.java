package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;

public class CBehaviorSendOrder implements CBehavior {

	private final CUnit unit;
	private final CAbility ability;
	private final int playerIndex;
	private final int orderId;
	private final int visibleOrderId;
	private final CWidget target;

	public CBehaviorSendOrder(final CUnit unit, final CAbility ability, final int playerIndex, final int orderId,
			final int visibleOrderId, final CWidget target) {
		this.unit = unit;
		this.ability = ability;
		this.playerIndex = playerIndex;
		this.orderId = orderId;
		this.visibleOrderId = visibleOrderId;
		this.target = target;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		return this.ability.begin(game, this.unit, this.playerIndex, this.orderId, this.target);
	}

	@Override
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return this.visibleOrderId;
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.IDLE;
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;

public class CBehaviorSendOrder implements CBehavior {

	private CUnit unit;
	private CAbility ability;
	private int orderId;
	private int visibleOrderId;
	private CWidget target;

	public CBehaviorSendOrder(final CUnit unit, CAbility ability, final int orderId, final int visibleOrderId,
			final CWidget target) {
		this.unit = unit;
		this.ability = ability;
		this.orderId = orderId;
		this.visibleOrderId = visibleOrderId;
		this.target = target;
	}

	@Override
	public CBehavior update(CSimulation game) {
		return ability.begin(game, unit, orderId, target);
	}

	@Override
	public void begin(CSimulation game) {
	}

	@Override
	public void end(CSimulation game, boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return visibleOrderId;
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

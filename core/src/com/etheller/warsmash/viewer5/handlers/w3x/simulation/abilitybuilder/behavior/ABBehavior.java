package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public interface ABBehavior extends CBehavior {
	public ABBehavior reset(final CWidget target);
	public ABBehavior reset(final CWidget target, int orderId);

	public ABBehavior reset(final AbilityPointTarget target);
	public ABBehavior reset(final AbilityPointTarget target, int orderId);

	public ABBehavior reset();
	public ABBehavior reset(int orderId);
	
	public void setCastId(int castId);
	public void setInstant(boolean instant);
}

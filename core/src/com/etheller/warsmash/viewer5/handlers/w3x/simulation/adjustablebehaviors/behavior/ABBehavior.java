package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;

public interface ABBehavior extends CBehavior {
	public CBehavior reset(final CSimulation game, final CWidget target, final boolean autoOrder);

	public CBehavior reset(final CSimulation game, final CWidget target, int orderId, final boolean autoOrder);

	public CBehavior reset(final CSimulation game, final AbilityPointTarget target, final boolean autoOrder);

	public CBehavior reset(final CSimulation game, final AbilityPointTarget target, int orderId,
			final boolean autoOrder);

	public CBehavior reset(final boolean autoOrder);

	public CBehavior reset(int orderId, final boolean autoOrder);

	public void setCastId(int castId);

	public void setInstant(boolean instant);

	public void setBehaviorCategory(CBehaviorCategory behaviorCategory);

	public CAbility getAbility();
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

public interface CBehaviorVisitor<T> {
	T accept(CBehavior target);

	T accept(CRangedBehavior target);

}

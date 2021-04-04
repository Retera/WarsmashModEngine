package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

public interface AbilityTarget {
	float getX();

	float getY();

	<T> T visit(AbilityTargetVisitor<T> visitor);
}

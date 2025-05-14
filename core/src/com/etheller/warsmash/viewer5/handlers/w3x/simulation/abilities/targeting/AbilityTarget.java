package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

public interface AbilityTarget {
	float getX();

	float getY();

	<T> T visit(AbilityTargetVisitor<T> visitor);

	public static double angleBetween(final AbilityTarget source, final AbilityTarget target) {
		final double dx = target.getX() - source.getX();
		final double dy = target.getY() - source.getY();
		return StrictMath.atan2(dy, dx);
	}

	public static double distanceBetweenPoints(final AbilityTarget source, final AbilityTarget target) {
		final double dx = target.getX() - source.getX();
		final double dy = target.getY() - source.getY();
		return StrictMath.sqrt((dx * dx) + (dy * dy));
	}
}

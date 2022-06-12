package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

public interface CWidgetVisitor<T> {
	T accept(CUnit target);

	T accept(CDestructable target);

	T accept(CItem target);
}

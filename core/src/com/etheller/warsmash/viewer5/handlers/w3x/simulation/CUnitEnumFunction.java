package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

public interface CUnitEnumFunction {
	/**
	 * Operates on a unit, returning true if we should stop execution.
	 *
	 * @param unit
	 * @return
	 */
	boolean call(CUnit unit);
}

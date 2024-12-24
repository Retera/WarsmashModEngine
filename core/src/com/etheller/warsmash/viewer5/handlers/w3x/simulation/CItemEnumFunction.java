package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

public interface CItemEnumFunction {
	/**
	 * Operates on an item, returning true if we should stop execution.
	 *
	 * @param item
	 * @return
	 */
	boolean call(CItem destructable);
}

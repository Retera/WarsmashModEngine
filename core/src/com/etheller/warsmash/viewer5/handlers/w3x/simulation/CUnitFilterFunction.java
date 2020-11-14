package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

public interface CUnitFilterFunction {
	boolean call(CUnit unit);

	CUnitFilterFunction ACCEPT_ALL = new CUnitFilterFunction() {
		@Override
		public boolean call(final CUnit unit) {
			return true;
		}
	};

	CUnitFilterFunction ACCEPT_ALL_LIVING = new CUnitFilterFunction() {
		@Override
		public boolean call(final CUnit unit) {
			return !unit.isDead();
		}
	};
}

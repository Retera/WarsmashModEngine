package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

public interface CWidgetFilterFunction {
	boolean call(CWidget unit);

	CWidgetFilterFunction ACCEPT_ALL = new CWidgetFilterFunction() {
		@Override
		public boolean call(final CWidget unit) {
			return true;
		}
	};

	CWidgetFilterFunction ACCEPT_ALL_LIVING = new CWidgetFilterFunction() {
		@Override
		public boolean call(final CWidget unit) {
			return !unit.isDead();
		}
	};
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public interface SimulationRenderComponentModel extends SimulationRenderComponent {
	SimulationRenderComponentModel DO_NOTHING = new SimulationRenderComponentModel() {
		@Override
		public void remove() {
		}

		@Override
		public void setHeight(final float height) {
		}
	};

	void setHeight(float height);
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public interface SimulationRenderComponent {
	SimulationRenderComponent DO_NOTHING = new SimulationRenderComponent() {
		@Override
		public void remove() {
		}
	};

	void remove();
}

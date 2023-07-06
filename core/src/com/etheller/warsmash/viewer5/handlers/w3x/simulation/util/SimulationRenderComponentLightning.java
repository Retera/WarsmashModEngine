package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public interface SimulationRenderComponentLightning extends SimulationRenderComponent {
	SimulationRenderComponentLightning DO_NOTHING = new SimulationRenderComponentLightning() {
		@Override
		public void remove() {
		}

		@Override
		public void setColor(float r, float g, float b, float a) {
		}

		@Override
		public boolean isRemoved() {
			return true;
		}
	};
	void setColor(float r, float g, float b, float a);

	boolean isRemoved();
}

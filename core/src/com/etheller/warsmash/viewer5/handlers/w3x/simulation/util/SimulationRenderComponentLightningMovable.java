package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public interface SimulationRenderComponentLightningMovable extends SimulationRenderComponentLightning {
	SimulationRenderComponentLightningMovable DO_NOTHING = new SimulationRenderComponentLightningMovable() {
		@Override
		public void remove() {
		}

		@Override
		public void setColor(float r, float g, float b, float a) {
		}

		@Override
		public void move(float x1, float y1, float x2, float y2) {
		}

		@Override
		public void move(float x1, float y1, float z1, float x2, float y2, float z2) {
		}

		@Override
		public boolean isRemoved() {
			return true;
		}
	};
	void move(float x1, float y1, float x2, float y2);

	void move(float x1, float y1, float z1, float x2, float y2, float z2);
}

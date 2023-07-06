package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.lightning.LightningEffectNode;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightningMovable;

public class RenderLightningEffect implements SimulationRenderComponentLightningMovable {
	private final LightningEffectNode source;
	private final LightningEffectNode target;
	private final War3MapViewer viewer; // TODO would be nice not to store viewer, but what is "move()" supposed to do?

	public RenderLightningEffect(LightningEffectNode source, LightningEffectNode target, War3MapViewer viewer) {
		this.source = source;
		this.target = target;
		this.viewer = viewer;
	}

	@Override
	public void remove() {
		source.detach();
		target.detach();
	}

	@Override
	public void move(float x1, float y1, float x2, float y2) {
		source.setLocation(x1, y1, viewer.terrain.getGroundHeight(x1, y1));
		target.setLocation(x2, y2, viewer.terrain.getGroundHeight(x2, y2));
	}

	@Override
	public void move(float x1, float y1, float z1, float x2, float y2, float z2) {
		source.setLocation(x1, y1, z1);
		source.setLocation(x2, y2, z2);
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		source.setColor(r, g, b, a);
		target.setColor(r, g, b, a);
	}

	@Override
	public boolean isRemoved() {
		return source.scene == null;
	}
}

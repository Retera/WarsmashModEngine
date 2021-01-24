package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public interface RenderWidget {
	MdxComplexInstance getInstance();

	CWidget getSimulationWidget();

	void updateAnimations(War3MapViewer war3MapViewer);

	boolean isIntersectedOnMeshAlways();

	float getSelectionScale();

	float getX();

	float getY();

	void unassignSelectionCircle();

	void assignSelectionCircle(SplatMover t);
}

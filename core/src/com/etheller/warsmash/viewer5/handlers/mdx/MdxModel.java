package com.etheller.warsmash.viewer5.handlers.mdx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.parsers.mdlx.MdlxModel;
import com.etheller.warsmash.parsers.mdlx.Sequence;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.Texture;

public class MdxModel extends com.etheller.warsmash.viewer5.Model<MdxHandler> {
	private MdlxModel model;

	public int arrayBuffer;
	public int elementBuffer;

	public List<Batch> batches = new ArrayList<>(); // TODO??

	public List<Integer> replaceables = new ArrayList<>();

	public boolean reforged = false;

	public MdxModel(final MdxHandler handler, final ModelViewer viewer, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(handler, viewer, extension, pathSolver, fetchUrl);
	}

	@Override
	protected void lateLoad() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void load(final InputStream src, final Object options) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void error(final Exception e) {
		// TODO Auto-generated method stub

	}

	// TODO typing
	public List<Long> getGlobalSequences() {
		return this.model.getGlobalSequences();
	}

	public List<Sequence> getSequences() {
		return this.model.getSequences();
	}

	public List<float[]> getPivotPoints() {
		return this.model.getPivotPoints();
	}

	public List<GeosetAnimation> getGeosetAnimations() {
		throw new UnsupportedOperationException("NYI");
	}

	public List<Texture> getTextures() {
		throw new UnsupportedOperationException("NYI");
	}

	public List<Material> getMaterials() {
		throw new UnsupportedOperationException("NYI");
	}

}

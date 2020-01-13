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
	public boolean reforged = false;
	public boolean hd = false;
	public SolverParams solverParams = new SolverParams();
	public String name = "";
	public List<Sequence> sequences = new ArrayList<>();
	public List<Integer> globalSequences = new ArrayList<>();
	public List<Material> materials = new ArrayList<>();
	public List<Layer> layers = new ArrayList<>();
	public List<Integer> replaceables = new ArrayList<>();
	public List<Texture> textures = new ArrayList<>();
	public List<TextureAnimation> textureAnimations = new ArrayList<>();
	public List<Geoset> geosets = new ArrayList<>();
	public List<GeosetAnimation> geosetAnimations = new ArrayList<>();
	public List<Bone> bones = new ArrayList<>();
	public List<Light> lights = new ArrayList<>();
	public List<Helper> helpers = new ArrayList<>();
	public List<Attachment> attachments = new ArrayList<>();
	public List<float[]> pivotPoints = new ArrayList<>();
	public List<ParticleEmitterObject> particleEmitters = new ArrayList<>();
	public List<ParticleEmitter2Object> particleEmitters2 = new ArrayList<>();
	public List<RibbonEmitterObject> ribbonEmitters = new ArrayList<>();
	public List<Camera> cameras = new ArrayList<>();
	public List<EventObjectEmitterObject> eventObjects = new ArrayList<>();
	public

	private MdlxModel model;

	public int arrayBuffer;
	public int elementBuffer;

	public List<Batch> batches = new ArrayList<>(); // TODO??

	public List<Object> opaqueGroups;
	public List<Object> translucentGroups;

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

	public List<TextureAnimation> getTextureAnimations() {
		throw new UnsupportedOperationException("NYI");
	}

	public List<Geoset> getGeosets() {
		throw new UnsupportedOperationException("NYI");
	}

	private static final class SolverParams {
		public boolean reforged;
		public boolean hd;

	}

}

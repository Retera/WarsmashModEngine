package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.SkeletalNode;
import com.etheller.warsmash.viewer5.Texture;

public class MdxComplexInstance extends ModelInstance {

	public MdxNode[] nodes;
	public SkeletalNode[] sortedNodes;

	public int frame;
	public int counter;
	public int sequence;
	public int sequenceLoopMode;
	public boolean sequenceEnded;
	public int teamColor;
	public Texture boneTexture;
	// TODO more fields, these few are to make related classes compile
	public float[] vertexColor;
	public float[][] geosetColors;
	public float[] layerAlphas;
	public int[] layerTextures;
	public float[][] uvAnims;
	public boolean allowParticleSpawn;

	public MdxComplexInstance(final MdxModel model) {
		super(model);
	}

	@Override
	public void updateAnimations(final float dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearEmittedObjects() {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderOpaque() {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderTranslucent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	public MdxComplexInstance setSequenceLoopMode(final int mode) {
		this.sequenceLoopMode = mode;
		return this;
	}

	public void setSequence(final int sequence) {
		this.sequence = sequence;
		throw new UnsupportedOperationException("Not yet implemented");
	}
}

package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.parsers.mdlx.AnimationMap;
import com.etheller.warsmash.viewer5.handlers.EmitterObject;

public class RibbonEmitterObject extends GenericObject implements EmitterObject {
	public Layer layer;
	public float heightAbove;
	public float heightBelow;
	public float alpha;
	public float[] color;
	public float lifeSpan;
	public long textureSlot;
	public long emissionRate;
	public float gravity;
	public long columns;
	public long rows;
	/**
	 * Even if the internal texture isn't loaded, it's fine to run emitters based on
	 * this emitter object.
	 *
	 * The ribbons will simply be black.
	 */
	public boolean ok = true;

	public RibbonEmitterObject(final MdxModel model, final com.etheller.warsmash.parsers.mdlx.RibbonEmitter emitter,
			final int index) {
		super(model, emitter, index);

		this.layer = model.getMaterials().get(emitter.getMaterialId()).layers.get(0);
		this.heightAbove = emitter.getHeightAbove();
		this.heightBelow = emitter.getHeightBelow();
		this.alpha = emitter.getAlpha();
		this.color = emitter.getColor();
		this.lifeSpan = emitter.getLifeSpan();
		this.textureSlot = emitter.getTextureSlot();
		this.emissionRate = emitter.getEmissionRate();
		this.gravity = emitter.getGravity();
		this.columns = emitter.getColumns();
		this.rows = emitter.getRows();
	}

	public int getHeightBelow(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KRHB.getWar3id(), instance, this.heightBelow);
	}

	public int getHeightAbove(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KRHA.getWar3id(), instance, this.heightAbove);
	}

	public int getTextureSlot(final long[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KRTX.getWar3id(), instance, 0);
	}

	public int getColor(final float[] out, final MdxComplexInstance instance) {
		return this.getVectorValue(out, AnimationMap.KRCO.getWar3id(), instance, this.color);
	}

	public int getAlpha(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KRAL.getWar3id(), instance, this.alpha);
	}

	@Override
	public int getVisibility(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KRVS.getWar3id(), instance, 1f);
	}

	@Override
	public int getGeometryEmitterType() {
		return GeometryEmitterFuncs.EMITTER_RIBBON;
	}

	@Override
	public boolean ok() {
		return this.ok;
	}
}

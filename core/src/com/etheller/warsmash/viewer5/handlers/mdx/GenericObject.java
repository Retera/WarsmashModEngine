package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.util.RenderMathUtils;
import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGenericObject;

public class GenericObject extends AnimatedObject implements GenericIndexed {

	public final int index;
	public final String name;
	public final int objectId;
	public final int parentId;
	public final float[] pivot;
	public final int dontInheritTranslation;
	public final int dontInheritRotation;
	public final int dontInheritScaling;
	public final int billboarded;
	public final int billboardedX;
	public final int billboardedY;
	public final int billboardedZ;
	public final int cameraAnchored;
	public final int bone;
	public final int light;
	public final int eventObject;
	public final int attachment;
	public final int particleEmitter;
	public final int collisionShape;
	public final int ribbonEmitter;
	public final int emitterUsesMdlOrUnshaded;
	public final int emitterUsesTgaOrSortPrimitivesFarZ;
	public final int lineEmitter;
	public final int unfogged;
	public final int modelSpace;
	public final int xYQuad;
	public final boolean anyBillboarding;
	public final Variants variants;
	public final boolean hasTranslationAnim;
	public final boolean hasRotationAnim;
	public final boolean hasScaleAnim;
	public final boolean hasGenericAnim;

	public GenericObject(final MdxModel model, final MdlxGenericObject object, final int index) {
		super(model, object);

		this.index = index;
		this.name = object.getName();
		int objectId = object.getObjectId();
		if (objectId == -1) {
			objectId = index;
		}
		this.objectId = objectId;
		int parentId = object.getParentId();
		this.pivot = ((this.objectId < model.getPivotPoints().size())) ? model.getPivotPoints().get(this.objectId)
				: new float[] { 0, 0, 0 };

		final int flags = object.getFlags();

		this.dontInheritTranslation = flags & 0x1;
		this.dontInheritScaling = flags & 0x2;
		this.dontInheritRotation = flags & 0x4;
		this.billboarded = flags & 0x8;
		this.billboardedX = flags & 0x10;
		this.billboardedY = flags & 0x20;
		this.billboardedZ = flags & 0x40;
		this.cameraAnchored = flags & 0x80;
		this.bone = flags & 0x100;
		this.light = flags & 0x200;
		this.eventObject = flags & 0x400;
		this.attachment = flags & 0x800;
		this.particleEmitter = flags & 0x1000;
		this.collisionShape = flags & 0x2000;
		this.ribbonEmitter = flags & 0x4000;
		this.emitterUsesMdlOrUnshaded = flags & 0x8000;
		this.emitterUsesTgaOrSortPrimitivesFarZ = flags & 0x10000;
		this.lineEmitter = flags & 0x20000;
		this.unfogged = flags & 0x40000;
		this.modelSpace = flags & 0x80000;
		this.xYQuad = flags & 0x100000;

		this.anyBillboarding = (this.billboarded != 0) || (this.billboardedX != 0) || (this.billboardedY != 0)
				|| (this.billboardedZ != 0);

		if (object.getObjectId() == object.getParentId()) {
			parentId = -1; //
		}
		this.parentId = parentId;

		final Variants variants = new Variants(model.getSequences().size());

		boolean hasTranslationAnim = false;
		boolean hasRotationAnim = false;
		boolean hasScaleAnim = false;

		for (int i = 0; i < model.getSequences().size(); i++) {
			final boolean translation = this.isTranslationVariant(i);
			final boolean rotation = this.isRotationVariant(i);
			final boolean scale = this.isScaleVariant(i);

			variants.translation[i] = translation;
			variants.rotation[i] = rotation;
			variants.scale[i] = scale;
			variants.generic[i] = translation || rotation || scale;

			hasTranslationAnim = hasTranslationAnim || translation;
			hasRotationAnim = hasRotationAnim || rotation;
			hasScaleAnim = hasScaleAnim || scale;
		}

		this.variants = variants;
		this.hasTranslationAnim = hasTranslationAnim;
		this.hasRotationAnim = hasRotationAnim;
		this.hasScaleAnim = hasScaleAnim;
		this.hasGenericAnim = hasTranslationAnim || hasRotationAnim || hasScaleAnim;
	}

	/**
	 * Many of the generic objects have animated visibilities. This is a generic
	 * getter to allow the code to be consistent.
	 */
	public int getVisibility(final float[] out, final int sequence, final int frame, final int counter) {
		out[0] = 1;
		return -1;
	}

	public int getTranslation(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getVectorValue(out, AnimationMap.KGTR.getWar3id(), sequence, frame, counter,
				RenderMathUtils.FLOAT_VEC3_ZERO);
	}

	public int getRotation(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getQuatValue(out, AnimationMap.KGRT.getWar3id(), sequence, frame, counter,
				RenderMathUtils.FLOAT_QUAT_DEFAULT);
	}

	public int getScale(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getVectorValue(out, AnimationMap.KGSC.getWar3id(), sequence, frame, counter,
				RenderMathUtils.FLOAT_VEC3_ONE);
	}

	public boolean isTranslationVariant(final int sequence) {
		return this.isVariant(AnimationMap.KGTR.getWar3id(), sequence);
	}

	public boolean isRotationVariant(final int sequence) {
		return this.isVariant(AnimationMap.KGRT.getWar3id(), sequence);
	}

	public boolean isScaleVariant(final int sequence) {
		return this.isVariant(AnimationMap.KGSC.getWar3id(), sequence);
	}

	public static final class Variants {
		boolean[] translation;
		boolean[] rotation;
		boolean[] scale;
		boolean[] generic;

		public Variants(final int sequencesCount) {
			this.translation = new boolean[sequencesCount];
			this.rotation = new boolean[sequencesCount];
			this.scale = new boolean[sequencesCount];
			this.generic = new boolean[sequencesCount];
		}
	}

	@Override
	public int getIndex() {
		return this.index;
	}

}

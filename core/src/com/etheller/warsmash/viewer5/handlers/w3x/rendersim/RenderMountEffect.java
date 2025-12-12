package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.IndexedSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class RenderMountEffect implements RenderEffect {
	public static final PrimaryTag[] DEFAULT_ANIMATION_QUEUE = { PrimaryTag.SPELLCASTOMNI, PrimaryTag.MOUNT };
	private final SequenceLoopMode sequenceLoopMode;
	private final MdxComplexInstance mountModelInstance;
	private final MdxComplexInstance unitModelInstance;
	private final RenderUnit renderUnit;
	private PrimaryTag[] animationQueue;
	private final EnumSet<SecondaryTag> requiredAnimationNames;
	private int animationQueueIndex;
	private final List<Sequence> sequences;
	private boolean dismounted;
	private final float originalMaxPitch;
	private final float originalMaxRoll;

	public RenderMountEffect(final MdxComplexInstance mountModelInstance, final MdxComplexInstance unitModelInstance,
			final RenderUnit renderUnit, final War3MapViewer war3MapViewer, final PrimaryTag[] animationQueue,
			final EnumSet<SecondaryTag> requiredAnimationNames) {
		this.mountModelInstance = mountModelInstance;
		this.unitModelInstance = unitModelInstance;
		this.renderUnit = renderUnit;
		this.animationQueue = animationQueue;
		this.requiredAnimationNames = requiredAnimationNames;
		final MdxModel model = (MdxModel) this.unitModelInstance.model;
		this.sequences = model.getSequences();
		this.sequenceLoopMode = SequenceLoopMode.MODEL_LOOP;
		this.mountModelInstance.setSequenceLoopMode(SequenceLoopMode.MODEL_LOOP);
		this.unitModelInstance.sequenceEnded = true;
		playNextAnimation();
		if ((this.unitModelInstance.sequence == -1) && (model.getSequences().size() > 0)) {
			this.unitModelInstance.setSequence(0);
			this.animationQueueIndex = 0;
		}

		this.originalMaxPitch = renderUnit.getMaxPitch();
		this.originalMaxRoll = renderUnit.getMaxRoll();
		renderUnit.setMaxPitch((float) (Math.PI / 4));
		renderUnit.setMaxRoll((float) (Math.PI / 4));
	}

	@Override
	public boolean updateAnimations(final War3MapViewer war3MapViewer, final float deltaTime) {
		final boolean everythingDone = this.dismounted
				|| (this.unitModelInstance.sequenceEnded && (this.animationQueueIndex >= this.animationQueue.length));
		if (everythingDone) {
			this.animationQueueIndex = 0;
			return false;
		}
		playNextAnimation();
		this.unitModelInstance.localRotation.setFromAxisRad(0, 0, 1,
				(float) Math.toRadians(this.renderUnit.getFacing()));
		return everythingDone;
	}

	private void playNextAnimation() {
		while (this.unitModelInstance.sequenceEnded && (this.animationQueueIndex < this.animationQueue.length)) {
			applySequence();
			this.animationQueueIndex++;
		}
	}

	public void applySequence() {
		final PrimaryTag tag = this.animationQueue[this.animationQueueIndex];
		final IndexedSequence sequence = SequenceUtils.selectSequence(tag, this.requiredAnimationNames, this.sequences,
				true);
		if ((sequence != null) && (sequence.index != -1)) {
			if ((tag == PrimaryTag.STAND) && (this.sequenceLoopMode != SequenceLoopMode.NEVER_LOOP)) {
				this.unitModelInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
			}
			else {
				this.unitModelInstance.setSequenceLoopMode(this.sequenceLoopMode);
			}
			this.unitModelInstance.setSequence(sequence.index);
		}
	}

	public void setAnimations(final PrimaryTag[] animations) {
		this.animationQueue = animations;
		this.animationQueueIndex = 0;
		applySequence();
	}

	public void setHeight(final float height) {
		this.unitModelInstance.setLocation(this.unitModelInstance.localLocation.x,
				this.unitModelInstance.localLocation.y, height);
	}

	public void dismount(final War3MapViewer war3MapViewer) {
		if (this.unitModelInstance.parent != null) {
			this.unitModelInstance.setParent(null);
		}
		this.unitModelInstance.setLocation(this.mountModelInstance.localLocation);
		this.unitModelInstance.setRotation(this.mountModelInstance.localRotation);
		this.unitModelInstance.setScale(this.mountModelInstance.localScale);
		this.renderUnit.setModelInstance(this.unitModelInstance);
		war3MapViewer.worldScene.removeInstance(this.mountModelInstance);
		this.dismounted = true;
		this.renderUnit.setMaxPitch(this.originalMaxPitch);
		this.renderUnit.setMaxRoll(this.originalMaxRoll);
	}
}

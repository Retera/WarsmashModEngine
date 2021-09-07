package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.IndexedSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class RenderSpellEffect implements RenderEffect {
	public static final PrimaryTag[] DEFAULT_ANIMATION_QUEUE = { PrimaryTag.BIRTH, PrimaryTag.STAND, PrimaryTag.DEATH };
	private final MdxComplexInstance modelInstance;
	private final PrimaryTag[] animationQueue;
	private int animationQueueIndex;
	private final List<Sequence> sequences;

	public RenderSpellEffect(final MdxComplexInstance modelInstance, final War3MapViewer war3MapViewer, final float yaw,
			final PrimaryTag[] animationQueue) {
		this.modelInstance = modelInstance;
		this.animationQueue = animationQueue;
		final MdxModel model = (MdxModel) this.modelInstance.model;
		this.sequences = model.getSequences();
		this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
		this.modelInstance.localRotation.setFromAxisRad(0, 0, 1, yaw);
		this.modelInstance.sequenceEnded = true;
		playNextAnimation();
	}

	@Override
	public boolean updateAnimations(final War3MapViewer war3MapViewer, final float deltaTime) {
		playNextAnimation();
		final boolean everythingDone = this.animationQueueIndex >= this.animationQueue.length;
		if (everythingDone) {
			war3MapViewer.worldScene.removeInstance(this.modelInstance);
		}
		return everythingDone;
	}

	private void playNextAnimation() {
		while (this.modelInstance.sequenceEnded && (this.animationQueueIndex < this.animationQueue.length)) {
			final IndexedSequence sequence = SequenceUtils.selectSequence(this.animationQueue[this.animationQueueIndex],
					SequenceUtils.EMPTY, this.sequences, true);
			if ((sequence != null) && (sequence.index != -1)) {
				this.modelInstance.setSequence(sequence.index);
			}
			this.animationQueueIndex++;
		}
	}
}

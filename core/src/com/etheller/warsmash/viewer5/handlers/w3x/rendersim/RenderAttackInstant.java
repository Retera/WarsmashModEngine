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

public class RenderAttackInstant implements RenderEffect {
	private final MdxComplexInstance modelInstance;

	public RenderAttackInstant(final MdxComplexInstance modelInstance, final War3MapViewer war3MapViewer,
			final float yaw) {
		this.modelInstance = modelInstance;
		final MdxModel model = (MdxModel) this.modelInstance.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = SequenceUtils.selectSequence(PrimaryTag.DEATH, SequenceUtils.EMPTY, sequences,
				true);
		if ((sequence != null) && (sequence.index != -1)) {
			this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
			this.modelInstance.setSequence(sequence.index);
		}
		this.modelInstance.localRotation.setFromAxisRad(0, 0, 1, yaw);
	}

	@Override
	public boolean updateAnimations(final War3MapViewer war3MapViewer, final float deltaTime) {

		final boolean everythingDone = this.modelInstance.sequenceEnded;
		if (everythingDone) {
			war3MapViewer.worldScene.removeInstance(this.modelInstance);
		}
		return everythingDone;
	}
}

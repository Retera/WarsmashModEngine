package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.viewer5.SkeletalNode;

public class MdxCharacterNode extends MdxNode implements SequencedCharacterNode {
	public int nodeSequence;
	public int nodeFrame;
	public SubSequencer subSequencer;

	public void createSubSequencer(final MdxCharacterInstance modelInstance) {
		this.subSequencer = new SubSequencer(modelInstance);
	}

	/**
	 * Sub sequencer used to let one part of model play different animation than
	 * another part
	 */
	public final class SubSequencer {
		public float floatingFrame = 0;
		public int frame;
		public int sequence = -1;
		public boolean sequenceEnded = false;
		protected float animationSpeed = 1.0f;
		protected float blendTimeRemaining;
		final Set<SkeletalNode> children = new LinkedHashSet<>();

		private SubSequencer(final MdxCharacterInstance modelInstance) {
			this.children.add(MdxCharacterNode.this);
			for (int i = 0, l = modelInstance.sortedNodes.length; i < l; i++) {
				final SkeletalNode node = modelInstance.sortedNodes[i];
				if (this.children.contains(node.parent) || (MdxCharacterNode.this == node)) {
					this.children.add(node);
				}
			}
		}

		public void updateAnimations(final float dt, final MdxModel model, final MdxCharacterInstance modelInstance) {
			final int sequenceId = this.sequence;

			if ((sequenceId != -1) && (model.sequences.size() != 0)) {
				final Sequence sequence = model.sequences.get(sequenceId);
				final long[] interval = sequence.getInterval();
				final float frameTime = (dt * 1000 * this.animationSpeed);

				this.floatingFrame += frameTime;
				this.blendTimeRemaining -= frameTime;
				this.frame = (int) this.floatingFrame;

				final long animEnd = interval[1] - 1;
				if (this.floatingFrame >= animEnd) {
					boolean sequenceRestarted = false;
					if (sequence.getFlags() == 0) {
						this.floatingFrame = this.frame = (int) interval[0]; // TODO not cast

						sequenceRestarted = true;
					}

					this.sequenceEnded = !sequenceRestarted;
				}
				else {
					this.sequenceEnded = false;
				}
				if (this.sequenceEnded) {
					// blend at the end of subsequencer, since it'll probably jump back to
					// non-sub-sequence
					for (final SkeletalNode node : this.children) {
						node.beginBlending();
					}
				}
			}
		}

		public void setSequence(final int id, final MdxModel model, final MdxCharacterInstance modelInstance) {

			if (model.ok) {

				final int lastSequence = this.sequence;
				this.sequence = id;

				final List<Sequence> sequences = model.sequences;

				if ((id < 0) || (id > (sequences.size() - 1))) {
					this.sequence = -1;
					this.floatingFrame = 0;
				}
				else {
					// TODO blend
					if ((modelInstance.blendTime > 0) && (lastSequence != -1)) {
						if ((this.blendTimeRemaining <= 0)) {
							this.blendTimeRemaining = modelInstance.blendTime;
							for (final SkeletalNode node : this.children) {
								node.beginBlending();
							}
						}
					}

					// TODO not cast
					this.floatingFrame = (int) sequences.get(id).getInterval()[0];
					this.sequenceEnded = false;
				}
			}
		}
	}

	@Override
	public int getCharacterNodeSequence() {
		return this.nodeSequence;
	}

	@Override
	public int getCharacterNodeFrame() {
		return this.nodeFrame;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public boolean wasDirty() {
		return this.wasDirty;
	}
}

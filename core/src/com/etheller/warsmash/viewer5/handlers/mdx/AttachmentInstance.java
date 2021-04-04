package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.UpdatableObject;

public class AttachmentInstance implements UpdatableObject {
	private static final float[] visbilityHeap = new float[1];

	private final MdxComplexInstance instance;
	private final Attachment attachment;
	public final MdxComplexInstance internalInstance;

	public AttachmentInstance(final MdxComplexInstance instance, final Attachment attachment) {
		final MdxModel internalModel = attachment.internalModel;
		final MdxComplexInstance internalInstance = (MdxComplexInstance) internalModel.addInstance();

		internalInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		internalInstance.dontInheritScaling = false;
		internalInstance.hide();
		internalInstance.setParent(instance.nodes[attachment.objectId]);
		internalInstance.setAnimationSpeed(instance.getAnimationSpeed());

		this.instance = instance;
		this.attachment = attachment;
		this.internalInstance = internalInstance;
	}

	@Override
	public void update(final float dt, final boolean objectVisible) {
		final MdxComplexInstance internalInstance = this.internalInstance;
		if (internalInstance.model.ok) {
			if (!objectVisible) {
				internalInstance.hide();
			}
			else {
				this.attachment.getVisibility(visbilityHeap, this.instance.sequence, this.instance.frame,
						this.instance.counter);

				if (visbilityHeap[0] > 0.1) {
					// The parent instance might not actually be in a scene.
					// This happens if loading a local model, where loading is instant and adding to
					// a scene always comes afterwards.
					// Therefore, do it here dynamically.
					this.instance.scene.addInstance(internalInstance);

					if (internalInstance.hidden()) {
						internalInstance.show();

						// Every time the attachment becomes visible again, restart its first sequence.
						internalInstance.setSequence(0);
					}
				}
				else {
					internalInstance.hide();
				}
			}
		}
	}
}

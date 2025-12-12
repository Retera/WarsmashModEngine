package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.List;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.Descriptor;
import com.etheller.warsmash.viewer5.GenericNode;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SkeletalNode;
import com.etheller.warsmash.viewer5.UpdatableObject;

public class MdxCharacterInstance extends MdxComplexInstance implements SequencedCharacterNode {
	public static final Descriptor<MdxNode> NODE_DESCRIPTOR = new Descriptor<MdxNode>() {
		@Override
		public MdxNode create() {
			return new MdxCharacterNode();
		}
	};

	public MdxCharacterInstance(final MdxModel model) {
		super(model, NODE_DESCRIPTOR);
	}

	/**
	 * Updates all of this instance internal nodes and objects. Nodes that are
	 * determined to not be visible will not be updated, nor will any of their
	 * children down the hierarchy.
	 */
	@Override
	public boolean updateNodes(final float dt, final boolean forcedArg) {
		if (!this.model.ok) {
			return false;
		}
		final int counter = this.counter;
		final SkeletalNode[] sortedNodes = this.sortedNodes;
		final MdxModel model = (MdxModel) this.model;
		final List<GenericObject> sortedGenericObjects = model.sortedGenericObjects;
		final Scene scene = this.scene;
		boolean updated = false;

		// Update the nodes
		for (int i = 0, l = sortedNodes.length; i < l; i++) {
			final GenericObject genericObject = sortedGenericObjects.get(i);
			final MdxCharacterNode node = (MdxCharacterNode) sortedNodes[i];
			final GenericNode parent = node.parent;
			final SequencedCharacterNode parentSequenced = (SequencedCharacterNode) parent;
			int sequence = parentSequenced.getCharacterNodeSequence();
			int frame = parentSequenced.getCharacterNodeFrame();
			if ((node.subSequencer != null) && !node.subSequencer.sequenceEnded) {
				node.subSequencer.updateAnimations(dt, model, this);
				sequence = node.subSequencer.sequence;
				frame = node.subSequencer.frame;
			}
			final boolean forced = forcedArg || (sequence == -1);
			node.nodeSequence = sequence;
			node.nodeFrame = frame;

			genericObject.getVisibility(visibilityHeap, sequence, frame, counter);

			final boolean objectVisible = visibilityHeap[0] > 0;
			final boolean nodeVisible = forced || (parent.visible && objectVisible);

			node.visible = nodeVisible;

			// Every node only needs to be updated if this is a forced update, or if both
			// the parent node and the generic object corresponding to this node are
			// visible.
			// Incoming messy code for optimizations!
			if (nodeVisible) {
				boolean wasDirty = false;
				final GenericObject.Variants variants = genericObject.variants;
				final Vector3 localLocation = node.localLocation;
				final Quaternion localRotation = node.localRotation;
				final Vector3 localScale = node.localScale;
				final Quaternion overrideWorldRotation = node.overrideWorldRotation;

				// Only update the local node data if there is a need to
				if (forced || variants.generic[sequence]) {
					wasDirty = true;

					// Translation
					if (forced || variants.translation[sequence]) {
						genericObject.getTranslation(translationHeap, sequence, frame, counter);

						localLocation.x = translationHeap[0];
						localLocation.y = translationHeap[1];
						localLocation.z = translationHeap[2];
					}

					// Rotation
					if (forced || variants.rotation[sequence]) {
						genericObject.getRotation(rotationHeap, sequence, frame, counter);

						localRotation.x = rotationHeap[0];
						localRotation.y = rotationHeap[1];
						localRotation.z = rotationHeap[2];
						localRotation.w = rotationHeap[3];
					}

					// Scale
					if (forced || variants.scale[sequence]) {
						genericObject.getScale(scaleHeap, sequence, frame, counter);

						localScale.x = scaleHeap[0];
						localScale.y = scaleHeap[1];
						localScale.z = scaleHeap[2];
					}
				}

				final boolean wasReallyDirty = forced || wasDirty || parent.wasDirty || genericObject.anyBillboarding
						|| (overrideWorldRotation != null);

				node.wasDirty = wasReallyDirty;

				// If this is a forced update, or this node's local data was updated, or the
				// parent node was updated, do a full world update.
				if (wasReallyDirty) {
					node.recalculateTransformation(scene, this.blendTimeRemaining / this.blendTime);
					updated = true;
				}

				// If there is an instance object associated with this node, and the node is
				// visible (which might not be the case for a forced update!), update the
				// object.
				// This includes attachments and emitters.
				final UpdatableObject object = node.object;

				if (object != null) {
					object.update(dt, objectVisible);
				}

				// Update all of the node's non-skeletal children, which will update their
				// children, and so on.
				node.updateChildren(dt, scene);
			}
		}
		return updated;
	}

	@Override
	public void updateAnimations(final float dt) {
		final MdxModel model = (MdxModel) this.model;
		final int sequenceId = this.sequence;

		if ((sequenceId != -1) && (model.sequences.size() != 0)) {
			final Sequence sequence = model.sequences.get(sequenceId);
			final long[] interval = sequence.getInterval();
			final float frameTime = (dt * 1000 * this.animationSpeed);

			final int lastIntegerFrame = this.frame;
			this.floatingFrame += frameTime;
			this.blendTimeRemaining -= frameTime;
			this.frame = (int) this.floatingFrame;
			final int integerFrameTime = this.frame - lastIntegerFrame;
			this.counter += integerFrameTime;
			this.allowParticleSpawn = true;
			if (this.additiveOverrideMeshMode) {
				this.vertexColor[3] = Math.max(0,
						this.vertexColor[3] - (integerFrameTime / (float) (interval[1] - interval[0])));
			}

			final long animEnd = interval[1] - 1;
			if (this.floatingFrame >= animEnd) {
				boolean sequenceRestarted = false;
				if (sequence.getFlags() == 0) {
					this.floatingFrame = this.frame = (int) interval[0]; // TODO not cast

					resetEventEmitters();
					sequenceRestarted = true;
				}

				this.sequenceEnded = !sequenceRestarted;
			}
			else {
				this.sequenceEnded = false;
			}
		}

		final boolean forced = this.forced;

		updateNodes(dt, forced);

		updateBoneTexture();

		updateBatches(forced);
		int i = 0;
		for (final Geoset geoset : ((MdxModel) this.model).getGeosets()) {
			boolean show = true;
			if (geoset.mdlxGeoset.selectionGroup > 0) {
				final int slotKey = (int) (geoset.mdlxGeoset.selectionGroup / 100);
				final int slotValue = (int) (geoset.mdlxGeoset.selectionGroup % 100);
				switch (slotKey) {
				case 0:
					show = (slotValue == 3);
					break;
				case 5:
					show = (slotValue == 2);
					break;
				case 10:
					show = (slotValue == 2);
					break;
				default:
					show = (slotValue == 1);
					break;
				}
			}
			if (!show) {
				this.geosetColors[i][3] = 0.0f;
			}
			i++;
		}

		this.forced = false;

	}

	@Override
	public MdxComplexInstance setSequence(final int id) {
		return super.setSequence(id);
	}

	@Override
	public int getCharacterNodeSequence() {
		return this.sequence;
	}

	@Override
	public int getCharacterNodeFrame() {
		return this.frame;
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

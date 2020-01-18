package com.etheller.warsmash.viewer5.handlers.mdx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.parsers.mdlx.Sequence;
import com.etheller.warsmash.viewer5.GenericNode;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.Node;
import com.etheller.warsmash.viewer5.RenderBatch;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SkeletalNode;
import com.etheller.warsmash.viewer5.TextureMapper;
import com.etheller.warsmash.viewer5.UpdatableObject;
import com.etheller.warsmash.viewer5.gl.DataTexture;

public class MdxComplexInstance extends ModelInstance {
	private static final float[] visibilityHeap = new float[1];
	private static final float[] translationHeap = new float[3];
	private static final float[] rotationHeap = new float[4];
	private static final float[] scaleHeap = new float[3];
	private static final float[] colorHeap = new float[3];
	private static final float[] alphaHeap = new float[1];
	private static final long[] textureIdHeap = new long[1];

	public List<AttachmentInstance> attachments = new ArrayList<>();
	public List<ParticleEmitter> particleEmitters = new ArrayList<>();
	public List<ParticleEmitter2> particleEmitters2 = new ArrayList<>();
	public List<RibbonEmitter> ribbonEmitters = new ArrayList<>();
	public List<EventObjectEmitter<?, ?>> eventObjectEmitters = new ArrayList<>();
	public MdxNode[] nodes;
	public SkeletalNode[] sortedNodes;
	public int frame = 0;
	// Global sequences
	public int counter = 0;
	public int sequence = -1;
	public int sequenceLoopMode = 0;
	public boolean sequenceEnded = false;
	public int teamColor = 0;
	public float[] vertexColor = { 1, 1, 1, 1 };
	// Particles do not spawn when the sequence is -1, or when the sequence finished
	// and it's not repeating
	public boolean allowParticleSpawn = false;
	// If forced is true, everything will update regardless of variancy.
	// Any later non-forced update can then use variancy to skip updating things.
	// It is set to true every time the sequence is set with setSequence().
	public boolean forced = true;
	public float[][] geosetColors;
	public float[] layerAlphas;
	public int[] layerTextures;
	public float[][] uvAnims;
	public Matrix4[] worldMatrices;
	public FloatBuffer worldMatricesCopyHeap;
	public DataTexture boneTexture;

	public MdxComplexInstance(final MdxModel model) {
		super(model);
	}

	@Override
	public void load() {
		final MdxModel model = (MdxModel) this.model;

		this.geosetColors = new float[model.geosets.size()][];
		for (int i = 0, l = model.geosets.size(); i < l; i++) {
			this.geosetColors[i] = new float[4];
		}

		this.layerAlphas = new float[model.layers.size()];
		this.layerTextures = new int[model.layers.size()];
		this.uvAnims = new float[model.layers.size()][];
		for (int i = 0, l = model.layers.size(); i < l; i++) {
			this.layerAlphas[i] = 0;
			this.layerTextures[i] = 0;
			this.uvAnims[i] = new float[5];
		}

		// Create the needed amount of shared nodes.
		final Object[] sharedNodeData = Node.createSkeletalNodes(model.genericObjects.size(),
				MdxNodeDescriptor.INSTANCE);
		final List<MdxNode> nodes = (List<MdxNode>) sharedNodeData[0];
		int nodeIndex = 0;
		this.nodes = nodes.toArray(new MdxNode[nodes.size()]);

		// A shared typed array for all world matrices of the internal nodes.
		this.worldMatrices = ((List<Matrix4>) sharedNodeData[1]).toArray(new Matrix4[0]);
		this.worldMatricesCopyHeap = ByteBuffer.allocateDirect(16 * this.worldMatrices.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

		// And now initialize all of the nodes and objects
		for (final Bone bone : model.bones) {
			this.initNode(this.nodes, this.nodes[nodeIndex++], bone);
		}

		for (final Light light : model.lights) {
			this.initNode(this.nodes, this.nodes[nodeIndex++], light);
		}

		for (final Helper helper : model.helpers) {
			this.initNode(this.nodes, this.nodes[nodeIndex++], helper);
		}

		for (final Attachment attachment : model.attachments) {
			AttachmentInstance attachmentInstance = null;

			// Attachments may have game models attached to them, such as Undead and
			// Nightelf building animations.
			if (attachment.internalModel != null) {
				attachmentInstance = new AttachmentInstance(this, attachment);

				this.attachments.add(attachmentInstance);
			}

			this.initNode(this.nodes, this.nodes[nodeIndex++], attachment, attachmentInstance);
		}

		for (final ParticleEmitterObject emitterObject : model.particleEmitters) {
			final ParticleEmitter emitter = new ParticleEmitter(this, emitterObject);

			this.particleEmitters.add(emitter);

			this.initNode(this.nodes, this.nodes[nodeIndex++], emitterObject, emitter);
		}

		for (final ParticleEmitter2Object emitterObject : model.particleEmitters2) {
			final ParticleEmitter2 emitter = new ParticleEmitter2(this, emitterObject);

			this.particleEmitters2.add(emitter);

			this.initNode(this.nodes, this.nodes[nodeIndex++], emitterObject, emitter);
		}

		for (final RibbonEmitterObject emitterObject : model.ribbonEmitters) {
			final RibbonEmitter emitter = new RibbonEmitter(this, emitterObject);

			this.ribbonEmitters.add(emitter);

			this.initNode(this.nodes, this.nodes[nodeIndex++], emitterObject, emitter);
		}

		for (final EventObjectEmitterObject emitterObject : model.eventObjects) {
			final String type = emitterObject.type;
			EventObjectEmitter<?, ?> emitter;

			if ("SPN".equals(type)) {
				emitter = new EventObjectSpnEmitter(this, emitterObject);
			}
			else if ("SPL".equals(type)) {
				emitter = new EventObjectSplEmitter(this, emitterObject);
			}
			else if ("UBR".equals(type)) {
				emitter = new EventObjectUbrEmitter(this, emitterObject);
			}
			else {
				emitter = new EventObjectSndEmitter(this, emitterObject);
			}

			this.eventObjectEmitters.add(emitter);

			this.initNode(this.nodes, this.nodes[nodeIndex++], emitterObject, emitter);
		}

		for (final CollisionShape collisionShape : model.collisionShapes) {
			this.initNode(this.nodes, this.nodes[nodeIndex++], collisionShape);
		}

		// Save a sorted array of all of the nodes, such that every child node comes
		// after its parent.
		// This allows for flat iteration when updating.
		final List<Integer> hierarchy = model.hierarchy;

		this.sortedNodes = new SkeletalNode[nodes.size()];
		for (int i = 0, l = nodes.size(); i < l; i++) {
			this.sortedNodes[i] = this.nodes[hierarchy.get(i)];
		}

		// If the sequence was changed before the model was loaded, reset it now that
		// the model loaded.
		this.setSequence(this.sequence);

		if (model.bones.size() != 0) {
			this.boneTexture = new DataTexture(model.viewer.gl, 4, model.bones.size() * 4, 1);
		}
	}

	/*
	 * Clear all of the emitted objects that belong to this instance.
	 */
	@Override
	public void clearEmittedObjects() {
		for (final ParticleEmitter emitter : this.particleEmitters) {
			emitter.clear();
		}

		for (final ParticleEmitter2 emitter : this.particleEmitters2) {
			emitter.clear();
		}

		for (final RibbonEmitter emitter : this.ribbonEmitters) {
			emitter.clear();
		}

		for (final EventObjectEmitter<?, ?> emitter : this.eventObjectEmitters) {
			emitter.clear();
		}
	}

	private void initNode(final MdxNode[] nodes, final SkeletalNode node, final GenericObject genericObject) {
		initNode(nodes, node, genericObject, null);
	}

	/**
	 * Initialize a skeletal node.
	 */
	private void initNode(final MdxNode[] nodes, final SkeletalNode node, final GenericObject genericObject,
			final UpdatableObject object) {
		node.pivot.set(genericObject.pivot);

		if (genericObject.parentId == -1) {
			node.parent = this;
		}
		else {
			node.parent = nodes[genericObject.parentId];
		}

		/// TODO: single-axis billboarding
		if (genericObject.billboarded != 0) {
			node.billboarded = true;
		} // else if (genericObject.billboardedX) {
			// node.billboardedX = true;
			// } else if (genericObject.billboardedY) {
			// node.billboardedY = true;
			// } else if (genericObject.billboardedZ) {
			// node.billboardedZ = true;
			// }

		if (object != null) {
			node.object = object;
		}

	}

	/*
	 * Overriden to hide also attachment models.
	 */
	@Override
	public void hide() {
		super.hide();

		for (final AttachmentInstance attachment : this.attachments) {
			attachment.internalInstance.hide();
		}
	}

	/**
	 * Updates all of this instance internal nodes and objects. Nodes that are
	 * determined to not be visible will not be updated, nor will any of their
	 * children down the hierarchy.
	 */
	public void updateNodes(final float dt, final boolean forced) {
		final int sequence = this.sequence;
		final int frame = this.frame;
		final int counter = this.counter;
		final SkeletalNode[] sortedNodes = this.sortedNodes;
		final MdxModel model = (MdxModel) this.model;
		final List<GenericObject> sortedGenericObjects = model.sortedGenericObjects;
		final Scene scene = this.scene;

		// Update the nodes
		for (int i = 0, l = sortedNodes.length; i < l; i++) {
			final GenericObject genericObject = sortedGenericObjects.get(i);
			final SkeletalNode node = sortedNodes[i];
			final GenericNode parent = node.parent;

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

				final boolean wasReallyDirty = forced || wasDirty || parent.wasDirty || genericObject.anyBillboarding;

				node.wasDirty = wasReallyDirty;

				// If this is a forced update, or this node's local data was updated, or the
				// parent node was updated, do a full world update.
				if (wasReallyDirty) {
					node.recalculateTransformation(scene);
				}

				// If there is an instance object associated with this node, and the node is
				// visible (which might not be the case for a forced update!), update the
				// object.
				// This includes attachments and emitters.
				final UpdatableObject object = node.object;

				if ((object != null) && objectVisible) {
					object.update(dt);
				}

				// Update all of the node's non-skeletal children, which will update their
				// children, and so on.
				node.updateChildren(dt, scene);
			}
		}
	}

	/**
	 * Update the batch data.
	 */
	public void updateBatches(final boolean forced) {
		final int sequence = this.sequence;
		final int frame = this.frame;
		final int counter = this.counter;
		final MdxModel model = (MdxModel) this.model;
		final List<Geoset> geosets = model.geosets;
		final List<Layer> layers = model.layers;
		final float[][] geosetColors = this.geosetColors;
		final float[] layerAlphas = this.layerAlphas;
		final int[] layerTextures = this.layerTextures;
		final float[][] uvAnims = this.uvAnims;

		// Geoset
		for (int i = 0, l = geosets.size(); i < l; i++) {
			final Geoset geoset = geosets.get(i);
			final GeosetAnimation geosetAnimation = geoset.geosetAnimation;
			final float[] geosetColor = geosetColors[i];

			if (geosetAnimation != null) {
				// Color
				if (forced || (geosetAnimation.variants.get("color")[sequence] != 0)) {
					geosetAnimation.getColor(colorHeap, sequence, frame, counter);

					geosetColor[0] = colorHeap[0];
					geosetColor[1] = colorHeap[1];
					geosetColor[2] = colorHeap[2];
				}

				// Alpha
				if (forced || (geosetAnimation.variants.get("alpha")[sequence] != 0)) {
					geosetAnimation.getAlpha(alphaHeap, sequence, frame, counter);

					geosetColor[3] = alphaHeap[0];
				}
			}
			else if (forced) {
				geosetColor[0] = 1;
				geosetColor[1] = 1;
				geosetColor[2] = 1;
				geosetColor[3] = 1;
			}
		}

		// Layers
		for (int i = 0, l = layers.size(); i < l; i++) {
			final Layer layer = layers.get(i);
			final TextureAnimation textureAnimation = layer.textureAnimation;
			final float[] uvAnim = uvAnims[i];

			// Alpha
			if (forced || (layer.variants.get("alpha")[sequence] != 0)) {
				layer.getAlpha(alphaHeap, sequence, frame, counter);

				layerAlphas[i] = alphaHeap[0];
			}

			// Sprite animation
			if (forced || (layer.variants.get("textureId")[sequence] != 0)) {
				layer.getTextureId(textureIdHeap, sequence, frame, counter);

				layerTextures[i] = (int) textureIdHeap[0];
			}

			if (textureAnimation != null) {
				// UV translation animation
				if (forced || (textureAnimation.variants.get("translation")[sequence] != 0)) {
					textureAnimation.getTranslation(translationHeap, sequence, frame, counter);

					uvAnim[0] = translationHeap[0];
					uvAnim[1] = translationHeap[1];
				}

				// UV rotation animation
				if (forced || (textureAnimation.variants.get("rotation")[sequence] != 0)) {
					textureAnimation.getRotation(rotationHeap, sequence, frame, counter);

					uvAnim[2] = rotationHeap[2];
					uvAnim[3] = rotationHeap[3];
				}

				// UV scale animation
				if (forced || (textureAnimation.variants.get("scale")[sequence] != 0)) {
					textureAnimation.getScale(scaleHeap, sequence, frame, counter);

					uvAnim[4] = scaleHeap[0];
				}
			}
			else if (forced) {
				uvAnim[0] = 0;
				uvAnim[1] = 0;
				uvAnim[2] = 0;
				uvAnim[3] = 1;
				uvAnim[4] = 1;
			}
		}
	}

	public void updateBoneTexture() {
		if (this.boneTexture != null) {
			this.worldMatricesCopyHeap.clear();
			for (int i = 0, l = this.worldMatrices.length; i < l; i++) {
				final Matrix4 worldMatrix = this.worldMatrices[i];
				this.worldMatricesCopyHeap.put((i * 16) + 0, worldMatrix.val[Matrix4.M00]);
				this.worldMatricesCopyHeap.put((i * 16) + 1, worldMatrix.val[Matrix4.M01]);
				this.worldMatricesCopyHeap.put((i * 16) + 2, worldMatrix.val[Matrix4.M02]);
				this.worldMatricesCopyHeap.put((i * 16) + 3, worldMatrix.val[Matrix4.M03]);
				this.worldMatricesCopyHeap.put((i * 16) + 4, worldMatrix.val[Matrix4.M10]);
				this.worldMatricesCopyHeap.put((i * 16) + 5, worldMatrix.val[Matrix4.M11]);
				this.worldMatricesCopyHeap.put((i * 16) + 6, worldMatrix.val[Matrix4.M12]);
				this.worldMatricesCopyHeap.put((i * 16) + 7, worldMatrix.val[Matrix4.M13]);
				this.worldMatricesCopyHeap.put((i * 16) + 8, worldMatrix.val[Matrix4.M20]);
				this.worldMatricesCopyHeap.put((i * 16) + 9, worldMatrix.val[Matrix4.M21]);
				this.worldMatricesCopyHeap.put((i * 16) + 10, worldMatrix.val[Matrix4.M22]);
				this.worldMatricesCopyHeap.put((i * 16) + 11, worldMatrix.val[Matrix4.M23]);
				this.worldMatricesCopyHeap.put((i * 16) + 12, worldMatrix.val[Matrix4.M30]);
				this.worldMatricesCopyHeap.put((i * 16) + 13, worldMatrix.val[Matrix4.M31]);
				this.worldMatricesCopyHeap.put((i * 16) + 14, worldMatrix.val[Matrix4.M32]);
				this.worldMatricesCopyHeap.put((i * 16) + 15, worldMatrix.val[Matrix4.M33]);
			}
			this.boneTexture.bindAndUpdate(this.worldMatricesCopyHeap);
		}
	}

	@Override
	public void renderOpaque() {
		final MdxModel model = (MdxModel) this.model;

		for (final GenericGroup group : model.opaqueGroups) {
			group.render(this);
		}
	}

	@Override
	public void renderTranslucent() {
		final MdxModel model = (MdxModel) this.model;

		for (final GenericGroup group : model.translucentGroups) {
			group.render(this);
		}
	}

	@Override
	public void updateAnimations(final float dt) {
		final MdxModel model = (MdxModel) this.model;
		final int sequenceId = this.sequence;

		if (sequenceId != -1) {
			final Sequence sequence = model.sequences.get(sequenceId);
			final long[] interval = sequence.getInterval();
			final int frameTime = model.viewer.frameTime;

			this.frame += frameTime;
			this.counter += frameTime;
			this.allowParticleSpawn = true;

			if (this.frame >= interval[1]) {
				if ((this.sequenceLoopMode == 2) || ((this.sequenceLoopMode == 0) && (sequence.getFlags() == 0))) {
					this.frame = (int) interval[0]; // TODO not cast

					this.resetEventEmitters();
				}
				else {
					this.frame = (int) interval[1]; // TODO not cast
					this.counter -= frameTime;
					this.allowParticleSpawn = false;
				}

				this.sequenceEnded = true;
			}
			else {
				this.sequenceEnded = false;
			}
		}

		final boolean forced = this.forced;

		if (sequenceId == -1) {
			if (forced) {
				// Update the nodes
				this.updateNodes(dt, forced);

				this.updateBoneTexture();

				// Update the batches
				this.updateBatches(forced);
			}
		}
		else {
			// let variants = model.variants;

			// if (forced || variants.nodes[sequenceId]) {
			// Update the nodes
			this.updateNodes(dt, forced);

			this.updateBoneTexture();
			// }

			// if (forced || variants.batches[sequenceId]) {
			// Update the batches
			this.updateBatches(forced);
			// }
		}

		this.forced = false;

	}

	/**
	 * Set the team color of this instance.
	 */
	public MdxComplexInstance setTeamColor(final int id) {
		this.teamColor = id;

		return this;
	}

	/**
	 * Set the vertex color of this instance.
	 */
	public MdxComplexInstance setVertexColor(final float[] color) {
		System.arraycopy(color, 0, this.vertexColor, 0, color.length);

		return this;
	}

	/**
	 * Set the sequence of this instance.
	 */
	public MdxComplexInstance setSequence(final int id) {
		final MdxModel model = (MdxModel) this.model;

		this.sequence = id;

		if (model.ok) {
			final List<Sequence> sequences = model.sequences;

			if ((id < 0) || (id > (sequences.size() - 1))) {
				this.sequence = -1;
				this.frame = 0;
				this.allowParticleSpawn = false;
			}
			else {
				this.frame = (int) sequences.get(id).getInterval()[0]; // TODO not cast
			}

			this.resetEventEmitters();

			this.forced = true;
		}

		return this;
	}

	/**
	 * Set the seuqnece loop mode. 0 to never loop, 1 to loop based on the model,
	 * and 2 to always loop.
	 */
	public MdxComplexInstance setSequenceLoopMode(final int mode) {
		this.sequenceLoopMode = mode;

		return this;
	}

	/**
	 * Get an attachment node.
	 */
	public MdxNode getAttachment(final int id) {
		final MdxModel model = (MdxModel) this.model;
		final Attachment attachment = model.attachments.get(id);

		if (attachment != null) {
			return this.nodes[attachment.index];
		}

		return null;
	}

	/**
	 * Event emitters depend on keyframe index changes to emit, rather than only
	 * values. To work, they need to check what the last keyframe was, and only if
	 * it's a different one, do something. When changing sequences, these states
	 * need to be reset, so they can immediately emit things if needed.
	 */
	private void resetEventEmitters() {
		/// TODO: Update this. Said Ghostwolf.
		for (final EventObjectEmitter<?, ?> eventObjectEmitter : this.eventObjectEmitters) {
			eventObjectEmitter.reset();
		}
	}

	@Override
	protected RenderBatch getBatch(final TextureMapper textureMapper2) {
		throw new UnsupportedOperationException("NOT API");
	}
}

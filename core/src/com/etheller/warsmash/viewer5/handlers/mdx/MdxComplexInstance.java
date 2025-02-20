package com.etheller.warsmash.viewer5.handlers.mdx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Bounds;
import com.etheller.warsmash.viewer5.GenericNode;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.Node;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.RenderBatch;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SkeletalNode;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.TextureMapper;
import com.etheller.warsmash.viewer5.UpdatableObject;
import com.etheller.warsmash.viewer5.gl.DataTexture;
import com.etheller.warsmash.viewer5.handlers.w3x.DynamicShadowManager;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;

public class MdxComplexInstance extends ModelInstance {
	private static final float[] visibilityHeap = new float[1];
	private static final float[] translationHeap = new float[3];
	private static final float[] rotationHeap = new float[4];
	private static final float[] scaleHeap = new float[3];
	private static final float[] colorHeap = new float[3];
	private static final float[] alphaHeap = new float[1];
	private static final long[] textureIdHeap = new long[1];
	private static final Vector3 intersectionHeap = new Vector3();

	public List<LightInstance> lights = new ArrayList<>();
	public List<AttachmentInstance> attachments = new ArrayList<>();
	public List<ParticleEmitter> particleEmitters = new ArrayList<>();
	public List<ParticleEmitter2> particleEmitters2 = new ArrayList<>();
	public List<RibbonEmitter> ribbonEmitters = new ArrayList<>();
	public List<EventObjectEmitter<?, ?>> eventObjectEmitters = new ArrayList<>();
	public MdxNode[] nodes;
	public SkeletalNode[] sortedNodes;
	public int frame = 0;
	public float floatingFrame = 0;
	// Global sequences
	public int counter = 0;
	public int sequence = -1;
	public SequenceLoopMode sequenceLoopMode = SequenceLoopMode.NEVER_LOOP;
	public boolean sequenceEnded = false;
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
	public Texture[] replaceableTextures = new Texture[WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT];
	public Texture[] replaceableTextures_diffuse = new Texture[WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT];
	public Texture[] replaceableTextures_normal = new Texture[WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT];
	public Texture[] replaceableTextures_orm = new Texture[WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT];
	private float animationSpeed = 1.0f;
	private float blendTime;
	private float blendTimeRemaining;
	public boolean additiveOverrideMeshMode = false;
	private boolean hasAnyUnselectableMesh = false;

	public MdxComplexInstance(final MdxModel model) {
		super(model);
	}

	@Override
	public void load() {
		final MdxModel model = (MdxModel) this.model;

		this.geosetColors = new float[model.geosets.size()][];
		for (int i = 0, l = model.geosets.size(); i < l; i++) {
			this.geosetColors[i] = new float[4];
			if (model.geosets.get(i).unselectable) {
				this.hasAnyUnselectableMesh = true;
			}
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
			final LightInstance lightInstance = new LightInstance(this, light);
			this.lights.add(lightInstance);
			this.initNode(this.nodes, this.nodes[nodeIndex++], light, lightInstance);
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
			else if ("SPL".equals(type) || "FPT".equals(type)) {
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

		// Below, we allow attachments to initialize after the node tree
		// is finished building. This allows "internalInstance.setParent(...)"
		// to link the child model instance to the parent
		for (final AttachmentInstance attachment : this.attachments) {
			attachment.initialize();
		}

		// If the sequence was changed before the model was loaded, reset it now that
		// the model loaded.
		setSequence(this.sequence);

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

	private void initNode(final MdxNode[] nodes, final MdxNode node, final GenericObject genericObject) {
		this.initNode(nodes, node, genericObject, null);
	}

	/**
	 * Initialize a skeletal node.
	 */
	private void initNode(final MdxNode[] nodes, final MdxNode node, final GenericObject genericObject,
			final UpdatableObject object) {
		node.name = genericObject.name;
		node.pivot.set(genericObject.pivot);

		if (genericObject.parentId == -1) {
			node.parent = this;
		}
		else {
			node.parent = nodes[genericObject.parentId];
		}

		node.dontInheritTranslation = genericObject.dontInheritTranslation > 0;
		node.dontInheritScaling = genericObject.dontInheritScaling > 0;
		node.dontInheritRotation = genericObject.dontInheritRotation > 0;

		if (genericObject.billboarded != 0) {
			node.billboarded = true;
		}
		else if (genericObject.billboardedX != 0) {
			node.billboardedX = true;
		}
		else if (genericObject.billboardedY != 0) {
			node.billboardedY = true;
		}
		else if (genericObject.billboardedZ != 0) {
			node.billboardedZ = true;
		}

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
		if (!this.model.ok) {
			return;
		}
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
				final Quaternion overrideWorldRotation = node.overrideWorldRotation;
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
				if (overrideWorldRotation != null) {
					if (node.preOverrideLocalRotation != null) {
						localRotation.set(node.preOverrideLocalRotation);
					}
					else {
						localRotation.set(overrideWorldRotation);
						localRotation.mulLeft(parent.inverseWorldRotation);
					}
				}

				final boolean wasReallyDirty = forced || wasDirty || parent.wasDirty || genericObject.anyBillboarding
						|| (overrideWorldRotation != null);

				node.wasDirty = wasReallyDirty;

				// If this is a forced update, or this node's local data was updated, or the
				// parent node was updated, do a full world update.
				if (wasReallyDirty) {
					node.recalculateTransformation(scene, this.blendTimeRemaining / this.blendTime);
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
	}

	/**
	 * Update the batch data.
	 */
	public void updateBatches(final boolean forced) {
		final int sequence = this.sequence;
		final int frame = this.frame;
		final int counter = this.counter;
		final MdxModel model = (MdxModel) this.model;
		if (!model.ok) {
			return;
		}
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
				this.worldMatricesCopyHeap.put((i * 16) + 1, worldMatrix.val[Matrix4.M10]);
				this.worldMatricesCopyHeap.put((i * 16) + 2, worldMatrix.val[Matrix4.M20]);
				this.worldMatricesCopyHeap.put((i * 16) + 3, worldMatrix.val[Matrix4.M30]);
				this.worldMatricesCopyHeap.put((i * 16) + 4, worldMatrix.val[Matrix4.M01]);
				this.worldMatricesCopyHeap.put((i * 16) + 5, worldMatrix.val[Matrix4.M11]);
				this.worldMatricesCopyHeap.put((i * 16) + 6, worldMatrix.val[Matrix4.M21]);
				this.worldMatricesCopyHeap.put((i * 16) + 7, worldMatrix.val[Matrix4.M31]);
				this.worldMatricesCopyHeap.put((i * 16) + 8, worldMatrix.val[Matrix4.M02]);
				this.worldMatricesCopyHeap.put((i * 16) + 9, worldMatrix.val[Matrix4.M12]);
				this.worldMatricesCopyHeap.put((i * 16) + 10, worldMatrix.val[Matrix4.M22]);
				this.worldMatricesCopyHeap.put((i * 16) + 11, worldMatrix.val[Matrix4.M32]);
				this.worldMatricesCopyHeap.put((i * 16) + 12, worldMatrix.val[Matrix4.M03]);
				this.worldMatricesCopyHeap.put((i * 16) + 13, worldMatrix.val[Matrix4.M13]);
				this.worldMatricesCopyHeap.put((i * 16) + 14, worldMatrix.val[Matrix4.M23]);
				this.worldMatricesCopyHeap.put((i * 16) + 15, worldMatrix.val[Matrix4.M33]);
			}
			this.boneTexture.bindAndUpdate(this.worldMatricesCopyHeap);
		}
	}

	@Override
	public void renderOpaque(final Matrix4 mvp) {
		final MdxModel model = (MdxModel) this.model;

		if (!this.additiveOverrideMeshMode) {
			for (final GenericGroup group : model.opaqueGroups) {
				group.render(this, mvp);
			}
		}

		final int glGetError = Gdx.gl.glGetError();
		if ((glGetError != GL20.GL_NO_ERROR) && WarsmashConstants.ENABLE_DEBUG) {
			throw new IllegalStateException("GL ERROR: " + glGetError + " ON " + model.name + " (Opaque)");
		}
	}

	@Override
	public void renderTranslucent() {
		if (DynamicShadowManager.IS_SHADOW_MAPPING) {
			return;
		}
		final MdxModel model = (MdxModel) this.model;

		if (this.additiveOverrideMeshMode) {
			for (final GenericGroup group : model.opaqueGroups) {
				group.render(this, this.scene.camera.viewProjectionMatrix);
			}
		}
		for (final GenericGroup group : model.translucentGroups) {
			group.render(this, this.scene.camera.viewProjectionMatrix);

			final int glGetError = Gdx.gl.glGetError();
			if ((glGetError != GL20.GL_NO_ERROR) && WarsmashConstants.ENABLE_DEBUG) {
				throw new IllegalStateException("GL ERROR: " + glGetError + " ON " + model.name + " (Translucent)");
			}
		}
	}

	@Override
	public void updateAnimations(final float dt) {
		final MdxModel model = (MdxModel) this.model;
		final int sequenceId = this.sequence;

		if ((sequenceId != -1) && (model.sequences.size() != 0)) {
			final Sequence sequence = model.sequences.get(sequenceId);
			final long[] interval = sequence.getInterval();
			final float frameTime = dt * 1000 * this.animationSpeed;

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
				if ((this.sequenceLoopMode == SequenceLoopMode.ALWAYS_LOOP)
						|| ((this.sequenceLoopMode == SequenceLoopMode.MODEL_LOOP) && (sequence.getFlags() == 0))) {
					this.floatingFrame = this.frame = (int) interval[0]; // TODO not cast

					resetEventEmitters();
					sequenceRestarted = true;
				}
				else if (this.sequenceLoopMode == SequenceLoopMode.LOOP_TO_NEXT_ANIMATION) { // faux queued animation
					// mode
					final float framesPast = this.floatingFrame - animEnd;

					final List<Sequence> sequences = model.sequences;
					this.sequence = (this.sequence + 1) % sequences.size();
					this.floatingFrame = sequences.get(this.sequence).getInterval()[0] + framesPast; // TODO not cast
					this.frame = (int) this.floatingFrame;
					this.sequenceEnded = false;
					resetEventEmitters();
					this.forced = true;
					sequenceRestarted = true;
				}
				else {
					this.floatingFrame = this.frame = (int) animEnd; // TODO not cast
					this.counter -= integerFrameTime;
					this.allowParticleSpawn = false;
				}
				if (this.sequenceLoopMode == SequenceLoopMode.NEVER_LOOP_AND_HIDE_WHEN_DONE) {
					hide();
				}

				this.sequenceEnded = !sequenceRestarted;
			}
			else {
				this.sequenceEnded = false;
			}
		}

		final boolean forced = this.forced;

		if (sequenceId == -1) {
			if (forced) {
				// Update the nodes
				updateNodes(dt, forced);

				updateBoneTexture();

				// Update the batches
				updateBatches(forced);
			}
		}
		else {
			// let variants = model.variants;

			// if (forced || variants.nodes[sequenceId]) {
			// Update the nodes
			updateNodes(dt, forced);

			updateBoneTexture();
			// }

			// if (forced || variants.batches[sequenceId]) {
			// Update the batches
			updateBatches(forced);
			// }
		}

		this.forced = false;

	}

	@Override
	protected void updateLights(final Scene scene) {
		for (final LightInstance light : this.lights) {
			light.update(scene);
		}
	}

	@Override
	protected void removeLights(final Scene scene2) {
		for (final LightInstance light : this.lights) {
			light.remove(this.scene);
		}
		for (final ParticleEmitter particleEmitter : this.particleEmitters) {
			particleEmitter.onRemove();
		}
	}

	/**
	 * Set the team color of this instance.
	 */
	public MdxComplexInstance setTeamColor(final int id) {
		this.replaceableTextures[1] = (Texture) this.model.viewer.load(
				"ReplaceableTextures\\" + ReplaceableIds.getPathString(1) + ReplaceableIds.getIdString(id) + ".blp",
				PathSolver.DEFAULT, null);
		this.replaceableTextures[2] = (Texture) this.model.viewer.load(
				"ReplaceableTextures\\" + ReplaceableIds.getPathString(2) + ReplaceableIds.getIdString(id) + ".blp",
				PathSolver.DEFAULT, null);
		for (final AttachmentInstance attachmentInstance : this.attachments) {
			if (attachmentInstance.internalInstance != null) {
				attachmentInstance.internalInstance.setTeamColor(id);
			}
		}
		return this;
	}

	@Override
	public void setReplaceableTexture(final int replaceableTextureId, final String replaceableTextureFile) {
		this.replaceableTextures[replaceableTextureId] = (Texture) this.model.viewer.load(replaceableTextureFile,
				PathSolver.DEFAULT, null);
	}

	@Override
	public void setReplaceableTextureHD(final int replaceableTextureId, final String replaceableTextureFile) {
		this.replaceableTextures_diffuse[replaceableTextureId] = (Texture) this.model.viewer
				.load(replaceableTextureFile + "_diffuse.dds", PathSolver.DEFAULT, null);
		this.replaceableTextures_normal[replaceableTextureId] = (Texture) this.model.viewer
				.load(replaceableTextureFile + "_normal.dds", PathSolver.DEFAULT, null);
		this.replaceableTextures_orm[replaceableTextureId] = (Texture) this.model.viewer
				.load(replaceableTextureFile + "_orm.dds", PathSolver.DEFAULT, null);
	}

	/**
	 * Set the vertex color of this instance.
	 */
	public MdxComplexInstance setVertexColor(final float[] color) {
		System.arraycopy(color, 0, this.vertexColor, 0, color.length);

		return this;
	}

	/**
	 * Set the vertex color of this instance.
	 */
	public MdxComplexInstance setVertexColor(final Color color) {
		this.vertexColor[0] = color.r;
		this.vertexColor[1] = color.g;
		this.vertexColor[2] = color.b;
		this.vertexColor[3] = color.a;
		return this;
	}

	public MdxComplexInstance setVertexColor(final float r, final float g, final float b, final float a) {
		this.vertexColor[0] = r;
		this.vertexColor[1] = g;
		this.vertexColor[2] = b;
		this.vertexColor[3] = a;
		return this;
	}

	public MdxComplexInstance setVertexAlpha(final float alpha) {
		this.vertexColor[3] = alpha;

		return this;
	}

	/**
	 * Set the sequence of this instance.
	 */
	public MdxComplexInstance setSequence(final int id) {
		final MdxModel model = (MdxModel) this.model;

		if (model.ok) {

			final int lastSequence = this.sequence;
			this.sequence = id;

			final List<Sequence> sequences = model.sequences;

			if ((id < 0) || (id > (sequences.size() - 1))) {
				this.sequence = -1;
				this.frame = 0;
				this.floatingFrame = 0;
				this.allowParticleSpawn = false;
			}
			else {
				if ((this.blendTime > 0) && (lastSequence != -1)) {
					if ((this.blendTimeRemaining <= 0) && (this.counter > 0)) {
						this.blendTimeRemaining = this.blendTime;
						for (int i = 0, l = this.sortedNodes.length; i < l; i++) {
							final SkeletalNode node = this.sortedNodes[i];
							node.beginBlending();
						}
					}
				}

				this.frame = (int) sequences.get(id).getInterval()[0]; // TODO not cast
				this.floatingFrame = this.frame;
				this.sequenceEnded = false;
			}

			resetEventEmitters();

			this.forced = true;
		}

		return this;
	}

	/**
	 * Set the seuqnece loop mode. 0 to never loop, 1 to loop based on the model,
	 * and 2 to always loop. 3 was added by Retera as "hide after done" for gameplay
	 * spawned effects
	 */
	public MdxComplexInstance setSequenceLoopMode(final SequenceLoopMode mode) {
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

	public Bounds getBounds() {
		if (this.sequence == -1) {
			return this.model.bounds;
		}
		else {
			if (((MdxModel) this.model).sequences.isEmpty()) {
				System.err.println("Printing diagnostics for corrupted state MdxComplexInstance (about to crash)");
				System.err.println("Model name: " + ((MdxModel) this.model).name);
			}
			final Bounds sequenceBounds = ((MdxModel) this.model).sequences.get(this.sequence).getBounds();
			if (sequenceBounds.r == 0) {
				return this.model.bounds;
			}
			else {
				return sequenceBounds;
			}
		}
	}

	public boolean intersectRayBounds(final Ray ray, final Vector3 intersection) {
		return CollisionShape.intersectRayBounds(getBounds(), this.worldMatrix, ray, intersection);
	}

	/**
	 * Intersects a world ray with the model's CollisionShapes. Only ever call this
	 * function on the Gdx thread because it uses static variables to hold state
	 * while processing.
	 *
	 * @param ray
	 */
	public boolean intersectRayWithCollisionSimple(final Ray ray, final Vector3 intersection) {
		final MdxModel mdxModel = (MdxModel) this.model;
		final List<CollisionShape> collisionShapes = mdxModel.collisionShapes;
		boolean intersected = false;
		ray.getEndPoint(intersection, 99999);
		for (final CollisionShape collisionShape : collisionShapes) {
			final MdxNode mdxNode = this.nodes[collisionShape.index];
			if (collisionShape.checkIntersect(ray, mdxNode, intersectionHeap)) {
				if (intersectionHeap.dst2(ray.origin) < intersection.dst2(ray.origin)) {
					intersection.set(intersectionHeap);
				}
				intersected = true;
			}
		}
		return intersected || (collisionShapes.isEmpty() && intersectRayBounds(ray, intersection));
	}

	/**
	 * Intersects a world ray with the model's geosets. Only ever call this function
	 * on the Gdx thread because it uses static variables to hold state while
	 * processing.
	 *
	 * @param ray
	 */
	public boolean intersectRayWithMeshSlow(final Ray ray, final Vector3 intersection) {
		final MdxModel mdxModel = (MdxModel) this.model;
		boolean intersected = false;
		ray.getEndPoint(intersection, 99999);
		for (final Geoset geoset : mdxModel.geosets) {
			if (!geoset.unselectable) {
				geoset.getAlpha(alphaHeap, this.sequence, this.frame, this.counter);
				if (alphaHeap[0] > 0) {
					final MdlxGeoset mdlxGeoset = geoset.mdlxGeoset;
					if (CollisionShape.intersectRayTriangles(ray, this, mdlxGeoset.getVertices(), mdlxGeoset.getFaces(),
							3, intersectionHeap)) {
						if (intersectionHeap.dst2(ray.origin) < intersection.dst2(ray.origin)) {
							intersection.set(intersectionHeap);
						}
						intersected = true;
					}
				}
			}
		}
		return intersected;
	}

	/**
	 * Intersects a world ray with the model's CollisionShapes. Only ever call this
	 * function on the Gdx thread because it uses static variables to hold state
	 * while processing.
	 *
	 * @param ray
	 */
	public boolean intersectRayWithCollision(final Ray ray, final Vector3 intersection, final boolean alwaysUseMesh,
			final boolean onlyUseMesh) {
		final MdxModel mdxModel = (MdxModel) this.model;
		final List<CollisionShape> collisionShapes = mdxModel.collisionShapes;
		if (!onlyUseMesh) {
			for (final CollisionShape collisionShape : collisionShapes) {
				final MdxNode mdxNode = this.nodes[collisionShape.index];
				if (collisionShape.checkIntersect(ray, mdxNode, intersection)) {
					return true;
				}
			}
		}
		if (collisionShapes.isEmpty() || alwaysUseMesh) {
			for (final Geoset geoset : mdxModel.geosets) {
				if (!geoset.unselectable) {
					geoset.getAlpha(alphaHeap, this.sequence, this.frame, this.counter);
					if (alphaHeap[0] > 0) {
						final MdlxGeoset mdlxGeoset = geoset.mdlxGeoset;
						if (CollisionShape.intersectRayTriangles(ray, this, mdlxGeoset.getVertices(),
								mdlxGeoset.getFaces(), 3, intersection)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public void setAnimationSpeed(final float speedRatio) {
		this.animationSpeed = speedRatio;
		for (final AttachmentInstance attachmentInstance : this.attachments) {
			if (attachmentInstance.internalInstance != null) {
				attachmentInstance.internalInstance.setAnimationSpeed(speedRatio);
			}
		}
	}

	public float getAnimationSpeed() {
		return this.animationSpeed;
	}

	public void setBlendTime(final float blendTime) {
		this.blendTime = blendTime;
	}

	public void setFrame(final int frame) {
		this.frame = frame;
		this.floatingFrame = frame;
	}

	public void setFrameByRatio(final float ratioOfAnimationCompleted) {
		if (this.sequence != -1) {
			final Sequence currentlyPlayingSequence = ((MdxModel) this.model).sequences.get(this.sequence);
			final long start = currentlyPlayingSequence.getInterval()[0];
			final int lastIntegerFrame = this.frame;
			final float lastFloatingFrame = this.floatingFrame;
			final long sequenceLength = currentlyPlayingSequence.getInterval()[1] - start;
			final float newFloatingFrame = start + (sequenceLength * ratioOfAnimationCompleted);
			float frameTime = newFloatingFrame - lastFloatingFrame;
			if (frameTime < 0) {
				frameTime += sequenceLength;
			}
			this.floatingFrame = newFloatingFrame;
			this.frame = (int) this.floatingFrame;
			this.blendTimeRemaining -= frameTime;
			int integerFrameTime = this.frame - lastIntegerFrame;
			if (integerFrameTime < 0) {
				integerFrameTime += sequenceLength;
			}
			this.counter += integerFrameTime;
			for (final AttachmentInstance attachmentInstance : this.attachments) {
				if (attachmentInstance.internalInstance != null) {
					attachmentInstance.internalInstance.setFrameByRatio(ratioOfAnimationCompleted);
				}
			}
		}
	}

	public int clampFrame(final int frameToClamp) {
		final MdxModel model = (MdxModel) this.model;
		final int sequenceId = this.sequence;
		if ((sequenceId >= 0) && (sequenceId < model.sequences.size())) {
			final Sequence sequence = model.sequences.get(sequenceId);
			final long[] interval = sequence.getInterval();
			return (int) Math.max(interval[0], Math.min(interval[1], frameToClamp));
		}
		return frameToClamp;
	}

	public MdxNode inefficientlyGetNodeByNameSearch(final String name) {
		if (this.model.ok) {
			for (final MdxNode node : this.nodes) {
				if ((node.name != null) && node.name.equalsIgnoreCase(name)) {
					return node;
				}
			}
		}
		return null;
	}
}

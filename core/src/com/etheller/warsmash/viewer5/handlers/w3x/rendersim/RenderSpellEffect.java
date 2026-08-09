package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.EnumSet;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.CollidableDoodadComponent;
import com.etheller.warsmash.viewer5.handlers.w3x.IndexedSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class RenderSpellEffect implements RenderEffect {
	public static final PrimaryTag[] DEFAULT_ANIMATION_QUEUE = { PrimaryTag.BIRTH, PrimaryTag.STAND, PrimaryTag.DEATH };
	public static final PrimaryTag[] STAND_ONLY = { PrimaryTag.STAND };
	public static final PrimaryTag[] DEATH_ONLY = { PrimaryTag.DEATH };
	private SequenceLoopMode sequenceLoopMode;
	private final MdxComplexInstance modelInstance;
	private PrimaryTag[] animationQueue;
	private final EnumSet<SecondaryTag> requiredAnimationNames;
	private int animationQueueIndex;
	private final List<Sequence> sequences;
	private boolean killWhenDone = true;

	public RenderSpellEffect(final MdxComplexInstance modelInstance, final War3MapViewer war3MapViewer, final float yaw,
			final PrimaryTag[] animationQueue, final EnumSet<SecondaryTag> requiredAnimationNames) {
		this.modelInstance = modelInstance;
		this.animationQueue = animationQueue;
		this.requiredAnimationNames = requiredAnimationNames;
		final MdxModel model = (MdxModel) this.modelInstance.model;
		this.sequences = model.getSequences();
		this.sequenceLoopMode = SequenceLoopMode.MODEL_LOOP;
		this.modelInstance.setSequenceLoopMode(this.sequenceLoopMode);
		this.modelInstance.localRotation.setFromAxisRad(0, 0, 1, yaw);
		this.modelInstance.sequenceEnded = true;
		playNextAnimation();
		if ((this.modelInstance.sequence == -1) && (model.getSequences().size() > 0)) {
			this.modelInstance.setSequence(0);
			this.animationQueueIndex = 0;
		}
	}

	@Override
	public boolean updateAnimations(final War3MapViewer war3MapViewer, final float deltaTime) {
		final boolean everythingDone = this.modelInstance.sequenceEnded
				&& (this.animationQueueIndex >= this.animationQueue.length);
		if (everythingDone) {
			if (this.killWhenDone) {
				if (this.modelInstance.parent != null) {
					this.modelInstance.setParent(null);
				}
				war3MapViewer.worldScene.removeInstance(this.modelInstance);
			}
			else {
				this.animationQueueIndex = 0;
				return false;
			}
		}
		playNextAnimation();
		return everythingDone;
	}

	private void playNextAnimation() {
		while (this.modelInstance.sequenceEnded && (this.animationQueueIndex < this.animationQueue.length)) {
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
				this.modelInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
			}
			else {
				this.modelInstance.setSequenceLoopMode(this.sequenceLoopMode);
			}
			this.modelInstance.setSequence(sequence.index);
		}
	}

	public void setAnimations(final PrimaryTag[] animations, final boolean killWhenDone) {
		this.animationQueue = animations;
		this.animationQueueIndex = 0;
		setKillWhenDone(killWhenDone);
		applySequence();
		this.animationQueueIndex++;
	}

	public void setKillWhenDone(final boolean killWhenDone) {
		this.killWhenDone = killWhenDone;
		if (killWhenDone) {
			this.sequenceLoopMode = SequenceLoopMode.NEVER_LOOP;
			this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
		}
		else {
			this.modelInstance.setSequenceLoopMode(this.sequenceLoopMode);
		}
	}

	public void setHeight(final float height) {
		this.modelInstance.setLocation(this.modelInstance.localLocation.x, this.modelInstance.localLocation.y, height);
	}

	public void setReplaceableId(final int index, final String path) {
		this.modelInstance.setReplaceableTexture(index, path);
	}

	public void setScale(final float scalingValue) {
		this.modelInstance.uniformScale(scalingValue);
	}

	/**
	 * Applies lighting that incorporates Z and tunnels, for third person mode.
	 *
	 * @param war3MapViewer
	 */
	public void applyLightingThirdPerson(final War3MapViewer war3MapViewer) {
		final Vector3 location = this.modelInstance.localLocation;
		final float heightval = 66f;
		final float downwardRayTestHeight = location.z + heightval;
		CollidableDoodadComponent currentWalkableUnder = war3MapViewer.getHighestWalkableUnder(location.x, location.y,
				downwardRayTestHeight);

		War3MapViewer.gdxRayHeap.set(location.x, location.y, downwardRayTestHeight, 0, 0, -81920);
		float groundHeightTerrain = groundHeightTerrain = war3MapViewer.terrain.getGroundHeight(location.x, location.y);
		;
		final float groundHeight;
		if ((currentWalkableUnder != null)
				&& currentWalkableUnder.intersectRayWithGeosetSlow(War3MapViewer.gdxRayHeap,
						War3MapViewer.intersectionHeap)
				&& !((War3MapViewer.intersectionHeap.z < groundHeightTerrain)
						&& (groundHeightTerrain <= (location.z + heightval)))) {
			groundHeight = War3MapViewer.intersectionHeap.z;
		}
		else {
			groundHeight = groundHeightTerrain; // groundHeightTerrainAndWater;
			currentWalkableUnder = null;
		}
		if (currentWalkableUnder != null) {
			this.modelInstance.setModelOnlyLightManager(currentWalkableUnder.getModelOnlyLightManager());
			// always do 1 omit offset on unit, because WMO always has skylight in list
//			this.instance.setLightOmitOffsetOverride(currentWalkableUnder.isInterior() ? 1 : 0);
			// Inside a WMO interior the unit gets only sparse MOLT lamps, so without help
			// it is
			// near-black. Light it from the surface: a flat ambient floor (the WMO MOHD
			// ambient) plus a
			// directional 'extra' from the baked colour of the ground it stands on, shaded
			// along the
			// world sun direction so it matches outdoor shading. The lamps still add on top
			// via the
			// light manager. The ground colour is re-sampled only after the unit moves a
			// bit (perf).
			final float[] interiorAmbient = currentWalkableUnder.getServedInteriorAmbient();
			final float[] sampledGroundColor = new float[3];
			final float[] exteriorColorHeap = RenderUnit.exteriorColorHeap;
			if (interiorAmbient != null) {
				this.modelInstance.setLightOmitOffsetOverride(1);

				final boolean haveExterior = war3MapViewer.getExteriorLightColor(exteriorColorHeap);
				final boolean hasSampledGround = currentWalkableUnder.sampleNearestFloorColor(location.x, location.y,
						groundHeight, haveExterior ? RenderUnit.exteriorColorHeap : null, sampledGroundColor);

				final Vector3 sunDirHeap = RenderUnit.sunDirHeap;
				final boolean haveSun = war3MapViewer.getSunWorldDirection(sunDirHeap);
				final float sunX = haveSun ? sunDirHeap.x : 0f;
				final float sunY = haveSun ? sunDirHeap.y : 0f;
				final float sunZ = haveSun ? sunDirHeap.z : 1f;
				final float[] currentGroundColor = new float[3];
				if (hasSampledGround) {
					currentGroundColor[0] = sampledGroundColor[0];
					currentGroundColor[1] = sampledGroundColor[1];
					currentGroundColor[2] = sampledGroundColor[2];
					// Split the eased ground colour: part as flat ambient (lights ALL sides, so the
					// side
					// facing away from the sun is still lit by the local ground colour, not just
					// the dim
					// MOHD floor), the rest as the directional 'extra'. The lit side total is
					// unchanged
					// (MOHD + ground); only the dark side is lifted.
					final float f = RenderUnit.INTERIOR_GROUND_AMBIENT_FRACTION;
					this.modelInstance.setInteriorLightingDynamic(interiorAmbient[0] + (f * currentGroundColor[0]),
							interiorAmbient[1] + (f * currentGroundColor[1]),
							interiorAmbient[2] + (f * currentGroundColor[2]), (1f - f) * currentGroundColor[0],
							(1f - f) * currentGroundColor[1], (1f - f) * currentGroundColor[2], sunX, sunY, sunZ);
				}
				else {
					this.modelInstance.setInteriorLightingDynamic(interiorAmbient[0], interiorAmbient[1],
							interiorAmbient[2], 0, 0, 0, sunX, sunY, sunZ);
				}
			}
			else {
				this.modelInstance.setLightOmitOffsetOverride(currentWalkableUnder.getLightOmitOffsetOverride());
				this.modelInstance.setInteriorAmbientFlat(0, 0, 0);
			}
		}
		else {
//			this.instance.setLightOmitOffsetOverride(0);
			this.modelInstance.setModelOnlyLightManager(null);
			this.modelInstance.setLightOmitOffsetOverride(0);
			// Back outdoors: clear any interior ambient so the unit returns to normal sky
			// lighting.
			this.modelInstance.setInteriorAmbientFlat(0, 0, 0);
		}
	}
}

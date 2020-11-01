package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.parsers.mdlx.Sequence;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSoundset;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandButtonListener;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandCardPopulatingAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitAnimationListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;

public class RenderUnit {
	private static final Quaternion tempQuat = new Quaternion();
	private static final War3ID RED = War3ID.fromString("uclr");
	private static final War3ID GREEN = War3ID.fromString("uclg");
	private static final War3ID BLUE = War3ID.fromString("uclb");
	private static final War3ID MODEL_SCALE = War3ID.fromString("usca");
	private static final War3ID MOVE_HEIGHT = War3ID.fromString("umvh");
	private static final War3ID ORIENTATION_INTERPOLATION = War3ID.fromString("uori");
	private static final War3ID ANIM_PROPS = War3ID.fromString("uani");
	private static final War3ID BLEND_TIME = War3ID.fromString("uble");
	private static final float[] heapZ = new float[3];
	public final MdxComplexInstance instance;
	public final MutableGameObject row;
	public final float[] location = new float[3];
	public float selectionScale;
	public UnitSoundset soundset;
	public final MdxModel portraitModel;
	public int playerIndex;
	private final CUnit simulationUnit;
	public SplatMover shadow;
	public SplatMover selectionCircle;

	private float x;
	private float y;
	private float facing;

	private boolean swimming;

	private boolean dead = false;

	private final UnitAnimationListenerImpl unitAnimationListenerImpl;
	private OrientationInterpolation orientationInterpolation;
	private float currentTurnVelocity = 0;
	public long lastUnitResponseEndTimeMillis;
	private boolean corpse;
	private boolean boneCorpse;
	private final RenderUnitTypeData typeData;

	public RenderUnit(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final com.etheller.warsmash.parsers.w3x.unitsdoo.Unit unit, final UnitSoundset soundset,
			final MdxModel portraitModel, final CUnit simulationUnit, final RenderUnitTypeData typeData) {
		this.portraitModel = portraitModel;
		this.simulationUnit = simulationUnit;
		this.typeData = typeData;
		final MdxComplexInstance instance = (MdxComplexInstance) model.addInstance();

		final float[] location = unit.getLocation();
		System.arraycopy(location, 0, this.location, 0, 3);
		instance.move(location);
		this.facing = simulationUnit.getFacing();
		final float angle = (float) Math.toRadians(this.facing);
//		instance.localRotation.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle);
		this.x = simulationUnit.getX();
		this.y = simulationUnit.getY();
		instance.rotate(tempQuat.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle));
		instance.scale(unit.getScale());
		this.playerIndex = unit.getPlayer() & 0xFFFF;
		instance.setTeamColor(this.playerIndex);
		instance.setScene(map.worldScene);
		this.unitAnimationListenerImpl = new UnitAnimationListenerImpl(instance);
		simulationUnit.setUnitAnimationListener(this.unitAnimationListenerImpl);
		final String requiredAnimationNames = row.getFieldAsString(ANIM_PROPS, 0);
		TokenLoop: for (final String animationName : requiredAnimationNames.split(",")) {
			final String upperCaseToken = animationName.toUpperCase();
			for (final SecondaryTag secondaryTag : SecondaryTag.values()) {
				if (upperCaseToken.equals(secondaryTag.name())) {
					this.unitAnimationListenerImpl.addSecondaryTag(secondaryTag);
					continue TokenLoop;
				}
			}
		}

		if (row != null) {
			heapZ[2] = simulationUnit.getFlyHeight();
			this.location[2] += heapZ[2];

			instance.move(heapZ);
			War3ID red;
			War3ID green;
			War3ID blue;
			War3ID scale;
			scale = MODEL_SCALE;
			red = RED;
			green = GREEN;
			blue = BLUE;
			instance.setVertexColor(new float[] { (row.getFieldAsInteger(red, 0)) / 255f,
					(row.getFieldAsInteger(green, 0)) / 255f, (row.getFieldAsInteger(blue, 0)) / 255f });
			instance.uniformScale(row.getFieldAsFloat(scale, 0));

			this.selectionScale = row.getFieldAsFloat(War3MapViewer.UNIT_SELECT_SCALE, 0);
			int orientationInterpolationOrdinal = row.getFieldAsInteger(ORIENTATION_INTERPOLATION, 0);
			if ((orientationInterpolationOrdinal < 0)
					|| (orientationInterpolationOrdinal >= OrientationInterpolation.VALUES.length)) {
				orientationInterpolationOrdinal = 0;
			}
			this.orientationInterpolation = OrientationInterpolation.VALUES[orientationInterpolationOrdinal];

			final float blendTime = row.getFieldAsFloat(BLEND_TIME, 0);
			instance.setBlendTime(blendTime * 1000.0f);
		}

		this.instance = instance;
		this.row = row;
		this.soundset = soundset;

	}

	public void populateCommandCard(final CSimulation game, final CommandButtonListener commandButtonListener,
			final AbilityDataUI abilityDataUI, final int subMenuOrderId) {
		for (final CAbility ability : this.simulationUnit.getAbilities()) {
			ability.visit(CommandCardPopulatingAbilityVisitor.INSTANCE.reset(game, this.simulationUnit,
					commandButtonListener, abilityDataUI, subMenuOrderId));
		}
	}

	public void updateAnimations(final War3MapViewer map) {
		final float deltaTime = Gdx.graphics.getDeltaTime();
		final float simulationX = this.simulationUnit.getX();
		final float simulationY = this.simulationUnit.getY();
		final float simDx = simulationX - this.x;
		final float simDy = simulationY - this.y;
		final float distanceToSimulation = (float) Math.sqrt((simDx * simDx) + (simDy * simDy));
		final int speed = this.simulationUnit.getSpeed();
		final float speedDelta = speed * deltaTime;
		if ((distanceToSimulation > speedDelta) && (deltaTime < 1.0)) {
			// The 1.0 here says that after 1 second of lag, units just teleport to show
			// where they actually are
			this.x += (speedDelta * simDx) / distanceToSimulation;
			this.y += (speedDelta * simDy) / distanceToSimulation;
		}
		else {
			this.x = simulationX;
			this.y = simulationY;
		}
		final float x = this.x;
		final float dx = x - this.location[0];
		this.location[0] = x;
		final float y = this.y;
		final float dy = y - this.location[1];
		this.location[1] = y;
		final float groundHeight;
		final MovementType movementType = this.simulationUnit.getUnitType().getMovementType();
		final short terrainPathing = map.terrain.pathingGrid.getPathing(x, y);
		boolean swimming = (movementType == MovementType.AMPHIBIOUS)
				&& PathingGrid.isPathingFlag(terrainPathing, PathingGrid.PathingType.SWIMMABLE)
				&& !PathingGrid.isPathingFlag(terrainPathing, PathingGrid.PathingType.WALKABLE);
		final float groundHeightTerrain = map.terrain.getGroundHeight(x, y);
		float groundHeightTerrainAndWater;
		MdxComplexInstance currentWalkableUnder;
		final boolean standingOnWater = (swimming) || (movementType == MovementType.FLOAT)
				|| (movementType == MovementType.FLY) || (movementType == MovementType.HOVER);
		if (standingOnWater) {
			groundHeightTerrainAndWater = Math.max(groundHeightTerrain, map.terrain.getWaterHeight(x, y));
		}
		else {
			// land units will have their feet pass under the surface of the water
			groundHeightTerrainAndWater = groundHeightTerrain;
		}
		if (movementType == MovementType.FLOAT) {
			// boats cant go on bridges
			groundHeight = groundHeightTerrainAndWater;
			currentWalkableUnder = null;
		}
		else {
			currentWalkableUnder = map.getHighestWalkableUnder(x, y);
			War3MapViewer.gdxRayHeap.set(x, y, 4096, 0, 0, -8192);
			if ((currentWalkableUnder != null)
					&& currentWalkableUnder.intersectRayWithCollision(War3MapViewer.gdxRayHeap,
							War3MapViewer.intersectionHeap, true, true)
					&& (War3MapViewer.intersectionHeap.z > groundHeightTerrainAndWater)) {
				groundHeight = War3MapViewer.intersectionHeap.z;
				swimming = false; // Naga Royal Guard should slither across a bridge, not swim in rock
			}
			else {
				groundHeight = groundHeightTerrainAndWater;
				currentWalkableUnder = null;
			}
		}
		if (swimming && !this.swimming) {
			this.unitAnimationListenerImpl.addSecondaryTag(AnimationTokens.SecondaryTag.SWIM);
		}
		else if (!swimming && this.swimming) {
			this.unitAnimationListenerImpl.removeSecondaryTag(AnimationTokens.SecondaryTag.SWIM);
		}
		this.swimming = swimming;
		final boolean dead = this.simulationUnit.isDead();
		final boolean corpse = this.simulationUnit.isCorpse();
		final boolean boneCorpse = this.simulationUnit.isBoneCorpse();
		if (dead && !this.dead) {
			this.unitAnimationListenerImpl.playAnimation(true, PrimaryTag.DEATH, SequenceUtils.EMPTY, 1.0f, true);
			if (this.shadow != null) {
				this.shadow.destroy(Gdx.gl30, map.terrain.centerOffset);
				this.shadow = null;
			}
			if (this.selectionCircle != null) {
				this.selectionCircle.destroy(Gdx.gl30, map.terrain.centerOffset);
				this.selectionCircle = null;
			}
		}
		if (boneCorpse && !this.boneCorpse) {
			this.unitAnimationListenerImpl.playAnimationWithDuration(true, PrimaryTag.DECAY, SequenceUtils.BONE,
					this.simulationUnit.getEndingDecayTime(map.simulation), true);
		}
		else if (corpse && !this.corpse) {
			this.unitAnimationListenerImpl.playAnimationWithDuration(true, PrimaryTag.DECAY, SequenceUtils.FLESH,
					map.simulation.getGameplayConstants().getDecayTime(), true);
		}
		this.dead = dead;
		this.corpse = corpse;
		this.boneCorpse = boneCorpse;
		this.location[2] = this.simulationUnit.getFlyHeight() + groundHeight;
		this.instance.moveTo(this.location);
		float simulationFacing = this.simulationUnit.getFacing();
		if (simulationFacing < 0) {
			simulationFacing += 360;
		}
		float renderFacing = this.facing;
		if (renderFacing < 0) {
			renderFacing += 360;
		}
		float facingDelta = simulationFacing - renderFacing;
		if (facingDelta < -180) {
			facingDelta = 360 + facingDelta;
		}
		if (facingDelta > 180) {
			facingDelta = -360 + facingDelta;
		}
		final float absoluteFacingDelta = Math.abs(facingDelta);
		final float turningSign = Math.signum(facingDelta);

		final float absoluteFacingDeltaRadians = (float) Math.toRadians(absoluteFacingDelta);
		float acceleration;
		final boolean endPhase = (absoluteFacingDeltaRadians <= this.orientationInterpolation.getEndingAccelCutoff())
				&& ((this.currentTurnVelocity * turningSign) > 0);
		if (endPhase) {
			this.currentTurnVelocity = (1
					- ((this.orientationInterpolation.getEndingAccelCutoff() - absoluteFacingDeltaRadians)
							/ this.orientationInterpolation.getEndingAccelCutoff()))
					* (this.orientationInterpolation.getMaxVelocity()) * turningSign;
		}
		else {
			acceleration = this.orientationInterpolation.getStartingAcceleration() * turningSign;
			this.currentTurnVelocity = this.currentTurnVelocity + acceleration;
		}
		if ((this.currentTurnVelocity * turningSign) > this.orientationInterpolation.getMaxVelocity()) {
			this.currentTurnVelocity = this.orientationInterpolation.getMaxVelocity() * turningSign;
		}
		float angleToAdd = (float) ((Math.toDegrees(this.currentTurnVelocity) * deltaTime) / 0.03f);

		if (absoluteFacingDelta < Math.abs(angleToAdd)) {
			angleToAdd = facingDelta;
			this.currentTurnVelocity = 0.0f;
		}
		this.facing = (((this.facing + angleToAdd) % 360) + 360) % 360;
		this.instance.setLocalRotation(tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z, this.facing));

		final float facingRadians = (float) Math.toRadians(this.facing);
		final float maxPitch = this.typeData.getMaxPitch();
		final float maxRoll = this.typeData.getMaxRoll();
		final float sampleRadius = this.typeData.getElevationSampleRadius();
		float pitch, roll;
		final float pitchSampleForwardX = x + (sampleRadius * (float) Math.cos(facingRadians));
		final float pitchSampleForwardY = y + (sampleRadius * (float) Math.sin(facingRadians));
		final float pitchSampleBackwardX = x - (sampleRadius * (float) Math.cos(facingRadians));
		final float pitchSampleBackwardY = y - (sampleRadius * (float) Math.sin(facingRadians));
		final double leftOfFacingAngle = facingRadians + (Math.PI / 2);
		final float rollSampleForwardX = x + (sampleRadius * (float) Math.cos(leftOfFacingAngle));
		final float rollSampleForwardY = y + (sampleRadius * (float) Math.sin(leftOfFacingAngle));
		final float rollSampleBackwardX = x - (sampleRadius * (float) Math.cos(leftOfFacingAngle));
		final float rollSampleBackwardY = y - (sampleRadius * (float) Math.sin(leftOfFacingAngle));
		final float pitchSampleGroundHeight1;
		final float pitchSampleGroundHeight2;
		final float rollSampleGroundHeight1;
		final float rollSampleGroundHeight2;
		if (currentWalkableUnder != null) {
			pitchSampleGroundHeight1 = getGroundHeightSample(groundHeight, currentWalkableUnder, pitchSampleBackwardX,
					pitchSampleBackwardY);
			pitchSampleGroundHeight2 = getGroundHeightSample(groundHeight, currentWalkableUnder, pitchSampleForwardX,
					pitchSampleForwardY);
			rollSampleGroundHeight1 = getGroundHeightSample(groundHeight, currentWalkableUnder, rollSampleBackwardX,
					rollSampleBackwardY);
			rollSampleGroundHeight2 = getGroundHeightSample(groundHeight, currentWalkableUnder, rollSampleForwardX,
					rollSampleForwardY);
		}
		else {
			final float pitchGroundHeight1 = map.terrain.getGroundHeight(pitchSampleBackwardX, pitchSampleBackwardY);
			final float pitchGroundHeight2 = map.terrain.getGroundHeight(pitchSampleForwardX, pitchSampleForwardY);
			final float rollGroundHeight1 = map.terrain.getGroundHeight(rollSampleBackwardX, rollSampleBackwardY);
			final float rollGroundHeight2 = map.terrain.getGroundHeight(rollSampleForwardX, rollSampleForwardY);
			if (standingOnWater) {
				pitchSampleGroundHeight1 = Math.max(pitchGroundHeight1,
						map.terrain.getWaterHeight(pitchSampleBackwardX, pitchSampleBackwardY));
				pitchSampleGroundHeight2 = Math.max(pitchGroundHeight2,
						map.terrain.getWaterHeight(pitchSampleForwardX, pitchSampleForwardY));
				rollSampleGroundHeight1 = Math.max(rollGroundHeight1,
						map.terrain.getWaterHeight(rollSampleBackwardX, rollSampleBackwardY));
				rollSampleGroundHeight2 = Math.max(rollGroundHeight2,
						map.terrain.getWaterHeight(rollSampleForwardX, rollSampleForwardY));
			}
			else {
				pitchSampleGroundHeight1 = pitchGroundHeight1;
				pitchSampleGroundHeight2 = pitchGroundHeight2;
				rollSampleGroundHeight1 = rollGroundHeight1;
				rollSampleGroundHeight2 = rollGroundHeight2;
			}
		}
		pitch = Math.max(-maxPitch, Math.min(maxPitch,
				(float) Math.atan2(pitchSampleGroundHeight2 - pitchSampleGroundHeight1, sampleRadius * 2)));
		roll = Math.max(-maxRoll, Math.min(maxRoll,
				(float) Math.atan2(rollSampleGroundHeight2 - rollSampleGroundHeight1, sampleRadius * 2)));
		this.instance.rotate(tempQuat.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Y, -pitch));
		this.instance.rotate(tempQuat.setFromAxisRad(RenderMathUtils.VEC3_UNIT_X, roll));

		map.worldScene.instanceMoved(this.instance, this.location[0], this.location[1]);
		if (this.shadow != null) {
			this.shadow.move(dx, dy, map.terrain.centerOffset);
		}
		if (this.selectionCircle != null) {
			this.selectionCircle.move(dx, dy, map.terrain.centerOffset);
		}
		this.unitAnimationListenerImpl.update();
	}

	private float getGroundHeightSample(final float groundHeight, final MdxComplexInstance currentWalkableUnder,
			final float sampleX, final float sampleY) {
		final float sampleGroundHeight;
		War3MapViewer.gdxRayHeap.origin.x = sampleX;
		War3MapViewer.gdxRayHeap.origin.y = sampleY;
		if (currentWalkableUnder.intersectRayWithCollision(War3MapViewer.gdxRayHeap, War3MapViewer.intersectionHeap,
				true, true)) {
			sampleGroundHeight = War3MapViewer.intersectionHeap.z;
		}
		else {
			sampleGroundHeight = groundHeight;
		}
		return sampleGroundHeight;
	}

	public CUnit getSimulationUnit() {
		return this.simulationUnit;
	}

	private static final class UnitAnimationListenerImpl implements CUnitAnimationListener {
		private final MdxComplexInstance instance;
		private final EnumSet<AnimationTokens.SecondaryTag> secondaryAnimationTags = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private final EnumSet<AnimationTokens.SecondaryTag> recycleSet = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private PrimaryTag currentAnimation;
		private EnumSet<SecondaryTag> currentAnimationSecondaryTags;
		private float currentSpeedRatio;
		private boolean currentlyAllowingRarityVariations;
		private final Queue<QueuedAnimation> animationQueue = new LinkedList<>();

		public UnitAnimationListenerImpl(final MdxComplexInstance instance) {
			this.instance = instance;
		}

		public void addSecondaryTag(final AnimationTokens.SecondaryTag tag) {
			this.secondaryAnimationTags.add(tag);
			playAnimation(true, this.currentAnimation, this.currentAnimationSecondaryTags, this.currentSpeedRatio,
					this.currentlyAllowingRarityVariations);
		}

		public void removeSecondaryTag(final AnimationTokens.SecondaryTag tag) {
			this.secondaryAnimationTags.remove(tag);
			playAnimation(true, this.currentAnimation, this.currentAnimationSecondaryTags, this.currentSpeedRatio,
					this.currentlyAllowingRarityVariations);
		}

		@Override
		public void playAnimation(final boolean force, final PrimaryTag animationName,
				final EnumSet<SecondaryTag> secondaryAnimationTags, final float speedRatio,
				final boolean allowRarityVariations) {
			this.animationQueue.clear();
			if (force || (animationName != this.currentAnimation)) {
				this.currentSpeedRatio = speedRatio;
				this.recycleSet.clear();
				this.recycleSet.addAll(this.secondaryAnimationTags);
				this.recycleSet.addAll(secondaryAnimationTags);
				this.instance.setAnimationSpeed(speedRatio);
				if (SequenceUtils.randomSequence(this.instance, animationName, this.recycleSet,
						allowRarityVariations) != null) {
					this.currentAnimation = animationName;
					this.currentAnimationSecondaryTags = secondaryAnimationTags;
					this.currentlyAllowingRarityVariations = allowRarityVariations;
				}
			}
		}

		public void playAnimationWithDuration(final boolean force, final PrimaryTag animationName,
				final EnumSet<SecondaryTag> secondaryAnimationTags, final float duration,
				final boolean allowRarityVariations) {
			this.animationQueue.clear();
			if (force || (animationName != this.currentAnimation)) {
				this.recycleSet.clear();
				this.recycleSet.addAll(this.secondaryAnimationTags);
				this.recycleSet.addAll(secondaryAnimationTags);
				final Sequence sequence = SequenceUtils.randomSequence(this.instance, animationName, this.recycleSet,
						allowRarityVariations);
				if (sequence != null) {
					this.currentAnimation = animationName;
					this.currentAnimationSecondaryTags = secondaryAnimationTags;
					this.currentlyAllowingRarityVariations = allowRarityVariations;
					this.currentSpeedRatio = ((sequence.getInterval()[1] - sequence.getInterval()[0]) / 1000.0f)
							/ duration;
					this.instance.setAnimationSpeed(this.currentSpeedRatio);
				}
			}
		}

		@Override
		public void queueAnimation(final PrimaryTag animationName, final EnumSet<SecondaryTag> secondaryAnimationTags,
				final boolean allowRarityVariations) {
			this.animationQueue.add(new QueuedAnimation(animationName, secondaryAnimationTags, allowRarityVariations));
		}

		public void update() {
			if (this.instance.sequenceEnded || (this.instance.sequence == -1)) {
				// animation done
				if ((this.instance.sequence != -1) && (((MdxModel) this.instance.model).getSequences()
						.get(this.instance.sequence).getFlags() == 0)) {
					// animation is a looping animation
					playAnimation(true, this.currentAnimation, this.currentAnimationSecondaryTags,
							this.currentSpeedRatio, this.currentlyAllowingRarityVariations);
				}
				else {
					final QueuedAnimation nextAnimation = this.animationQueue.poll();
					if (nextAnimation != null) {
						playAnimation(true, nextAnimation.animationName, nextAnimation.secondaryAnimationTags, 1.0f,
								nextAnimation.allowRarityVariations);
					}
				}
			}
		}

	}

	private static final class QueuedAnimation {
		private final PrimaryTag animationName;
		private final EnumSet<SecondaryTag> secondaryAnimationTags;
		private final boolean allowRarityVariations;

		public QueuedAnimation(final PrimaryTag animationName, final EnumSet<SecondaryTag> secondaryAnimationTags,
				final boolean allowRarityVariations) {
			this.animationName = animationName;
			this.secondaryAnimationTags = secondaryAnimationTags;
			this.allowRarityVariations = allowRarityVariations;
		}
	}
}

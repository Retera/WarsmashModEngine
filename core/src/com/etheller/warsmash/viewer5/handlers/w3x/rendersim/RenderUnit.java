package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.EnumSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.CollidableDoodadComponent;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSoundset;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.BuildingShadow;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandButtonListener;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandCardPopulatingAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson.CAbilityPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CRarityControl;

public class RenderUnit implements RenderWidget {
	public static final War3ID PLAYER_PAWN_ID = War3ID.fromString("plyr");
	public static final Color ETHEREAL = new Color(0.75f, 1, 0.5f, 0.5f);
	public static final Color DEFAULT = new Color(1, 1, 1, 1);
	public static final Quaternion tempQuat = new Quaternion();
	private static final float[] heapZ = new float[3];
	public MdxComplexInstance instance;
	public final float[] location = new float[3];
	public float selectionScale;
	public UnitSoundset soundset;
	public MdxModel portraitModel;
	public int playerIndex;
	private final CUnit simulationUnit;
	public SplatMover shadow;
	public BuildingShadow buildingShadowInstance;
	public SplatMover selectionCircle;
	public SplatMover selectionPreviewHighlight;

	private float facing;

	private boolean swimming;
	private boolean working;
	private boolean invisible;

	private boolean dead = false;

	private UnitAnimationListenerImpl unitAnimationListenerImpl;
	private OrientationInterpolation orientationInterpolation;
	private float currentTurnVelocity = 0;
	public long lastUnitResponseEndTimeMillis;
	private boolean corpse;
	private boolean boneCorpse;
	private boolean building;
	private RenderUnitType typeData;
	public MdxModel specialArtModel;
	public SplatMover uberSplat;
	private float selectionHeight;
	private RenderUnit preferredSelectionReplacement;
	private CAbilityPlayerPawn playerPawn;
	private float[] currentColor = { 1, 1, 1, 1 };

	public RenderUnit(final War3MapViewer map, final float x, final float y, final float z, final int playerIndex,
			final CUnit simulationUnit, final RenderUnitType typeData, final BuildingShadow buildingShadow,
			final float selectionCircleScaleFactor, final float animationWalkSpeed, final float animationRunSpeed,
			final float scalingValue) {
		this.simulationUnit = simulationUnit;
		resetRenderUnit(map, x, y, z, playerIndex, simulationUnit, typeData, buildingShadow, selectionCircleScaleFactor,
				animationWalkSpeed, animationRunSpeed, scalingValue);

	}

	public void resetRenderUnit(final War3MapViewer map, final float x, final float y, final float z,
			final int playerIndex, final CUnit simulationUnit, final RenderUnitType typeData,
			final BuildingShadow buildingShadow, final float selectionCircleScaleFactor, final float animationWalkSpeed,
			final float animationRunSpeed, final float scalingValue) {
		this.portraitModel = typeData.getPortraitModel();
		this.typeData = typeData;
		this.specialArtModel = typeData.getSpecialArtModel();
		if (this.buildingShadowInstance != null) {
			this.buildingShadowInstance.remove();
		}
		this.buildingShadowInstance = buildingShadow;
		if (this.instance != null) {
			this.instance.detach();
		}
		this.playerPawn = simulationUnit.getFirstAbilityOfType(CAbilityPlayerPawn.class);
		MdxModel model = typeData.getModel();
		boolean playerPawnFlag = simulationUnit.getTypeId().equals(PLAYER_PAWN_ID);
		final MdxComplexInstance instance = (MdxComplexInstance) (playerPawnFlag ? model.addInstance(2)
				: model.addInstance());

		this.location[0] = x;
		this.location[1] = y;
		this.location[2] = z;
		instance.move(this.location);
		this.facing = simulationUnit.getFacing();
		final float angle = (float) Math.toRadians(this.facing);
//		instance.localRotation.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle);
		instance.rotate(tempQuat.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle));
		this.playerIndex = playerIndex & 0xFFFF;
		instance.setTeamColor(this.playerIndex);
		for (final RenderUnitReplaceableTex replaceableTex : typeData.getReplaceableTextures()) {
			instance.setReplaceableTexture(replaceableTex.getReplaceableId(), replaceableTex.getPath());
		}
		instance.setScene(map.worldScene);
		this.unitAnimationListenerImpl = new UnitAnimationListenerImpl(instance, animationWalkSpeed, animationRunSpeed);
		simulationUnit.setUnitAnimationListener(this.unitAnimationListenerImpl);

		if (typeData != null) {
			boolean changedAnimProps = false;
			for (final SecondaryTag secondaryTag : typeData.getRequiredAnimationNames()) {
				if (this.unitAnimationListenerImpl.addSecondaryTag(secondaryTag)) {
					changedAnimProps = true;
				}
			}
			if (changedAnimProps) {
				this.unitAnimationListenerImpl.forceResetCurrentAnimation();
			}

			heapZ[2] = simulationUnit.getFlyHeight();
			this.location[2] += heapZ[2];

			instance.move(heapZ);
			final Vector3 tintingColor = typeData.getTintingColor();
			this.currentColor = new float[] { tintingColor.x, tintingColor.y, tintingColor.z, 1.0f };
			instance.uniformScale(scalingValue);

			this.selectionScale = typeData.getSelectScale() * selectionCircleScaleFactor;
			this.selectionHeight = typeData.getSelectHeight();
			int orientationInterpolationOrdinal = typeData.getOrientationInterpolation();
			if ((orientationInterpolationOrdinal < 0)
					|| (orientationInterpolationOrdinal >= OrientationInterpolation.VALUES.length)) {
				orientationInterpolationOrdinal = 0;
			}
			this.orientationInterpolation = OrientationInterpolation.VALUES[orientationInterpolationOrdinal];

			final float blendTime = typeData.getBlendTime();
			instance.setBlendTime(blendTime * 1000.0f);
		}

		this.instance = instance;
		if (typeData != null) {
			updateColor(map);
		}
		this.soundset = typeData.getSoundset();
		this.building = simulationUnit.isBuilding();
	}

	public void populateCommandCard(final CSimulation game, final GameUI gameUI,
			final CommandButtonListener commandButtonListener, final AbilityDataUI abilityDataUI,
			final int subMenuOrderId, final boolean multiSelect, final int localPlayerIndex) {
		final CommandCardPopulatingAbilityVisitor commandCardPopulatingVisitor = CommandCardPopulatingAbilityVisitor.INSTANCE
				.reset(game, gameUI, this.simulationUnit, commandButtonListener, abilityDataUI, subMenuOrderId,
						multiSelect, localPlayerIndex);
		for (final CAbility ability : this.simulationUnit.getAbilities()) {
			if (!this.simulationUnit.isPaused() || (ability instanceof CBuff)
					|| (ability instanceof AbilityGenericSingleIconPassiveAbility)) {
				ability.visit(commandCardPopulatingVisitor);
			}
		}
	}

	@Override
	public void updateAnimations(final War3MapViewer map) {
		final boolean wasHidden = this.instance.hidden();
		if (this.simulationUnit.isHidden()
				|| !this.simulationUnit.isVisible(map.simulation, map.getLocalPlayerIndex())) {
			if (!wasHidden) {
				if (this.selectionCircle != null) {
					this.selectionCircle.hide();
				}
				if (this.selectionPreviewHighlight != null) {
					this.selectionPreviewHighlight.hide();
				}
				if (this.shadow != null) {
					this.shadow.hide();
				}
			}
			this.instance.hide();
			return;
		}
		else {
			this.instance.show();
			if (wasHidden) {
				if (this.selectionCircle != null) {
					this.selectionCircle.show(map.terrain.centerOffset);
				}
				if (this.selectionPreviewHighlight != null) {
					this.selectionPreviewHighlight.show(map.terrain.centerOffset);
				}
				if (this.shadow != null) {
					this.shadow.show(map.terrain.centerOffset);
				}
				repositioned(map);
			}
		}
		final float prevX = this.location[0];
		final float prevY = this.location[1];
		final float simulationX = this.simulationUnit.getX();
		final float simulationY = this.simulationUnit.getY();
		final float deltaTime = Gdx.graphics.getDeltaTime();
		final float simDx = simulationX - this.location[0];
		final float simDy = simulationY - this.location[1];
		final float distanceToSimulation = (float) Math.sqrt((simDx * simDx) + (simDy * simDy));
		final float speed = isPlayerPawn() ? this.playerPawn.getRenderMoveSpeed() : this.simulationUnit.getSpeed();
		final float speedDelta = speed * deltaTime;
		if ((distanceToSimulation > speedDelta) && (deltaTime < 1.0)) {
			// The 1.0 here says that after 1 second of lag, units just teleport to show
			// where they actually are
			this.location[0] += (speedDelta * simDx) / distanceToSimulation;
			this.location[1] += (speedDelta * simDy) / distanceToSimulation;
		}
		else {
			this.location[0] = simulationX;
			this.location[1] = simulationY;
		}
		final float groundHeightTerrain = map.terrain.getGroundHeight(this.location[0], this.location[1]);
		if (isPlayerPawn()) {
			final float prevZ = this.location[2];
			final float simDz = this.playerPawn.getZ() - this.location[2];
			final float absSimDz = Math.abs(simDz);
			final float verticalSign = Math.signum(simDz);
			if (verticalSign != 0) {
				final float elapsedSteps = deltaTime / WarsmashConstants.SIMULATION_STEP_TIME;
				final float verticalSpeed = (elapsedSteps
						* Math.abs(this.playerPawn.getBehaviorPlayerPawn().getVelocity().z
								- (float) Math.pow(1.5, elapsedSteps - 2f)));
				float min = Math.min(verticalSpeed, absSimDz);
				if (min <= 0) {
					min = 0;
				}
				this.location[2] += min * verticalSign;
				if (this.location[2] < groundHeightTerrain) {
					this.location[2] = groundHeightTerrain;
				}
			}
		}
		final float dx = this.location[0] - prevX;
		final float dy = this.location[1] - prevY;
		final float groundHeight;
		final MovementType movementType = this.simulationUnit.getMovementType();
		final short terrainPathing = map.terrain.pathingGrid.getPathing(this.location[0], this.location[1]);
		boolean swimming = (movementType == MovementType.AMPHIBIOUS)
				&& PathingGrid.isPathingFlag(terrainPathing, PathingGrid.PathingType.SWIMMABLE)
				&& !PathingGrid.isPathingFlag(terrainPathing, PathingGrid.PathingType.WALKABLE);
		final boolean invisible = this.simulationUnit.getInvisLevels() > 0;
		final boolean working = this.simulationUnit.getBuildQueueTypes()[0] != null;
		float groundHeightTerrainAndWater;
		CollidableDoodadComponent currentWalkableUnder;
		final boolean standingOnWater = (swimming) || (movementType == MovementType.FLOAT)
				|| (movementType == MovementType.FLY) || (movementType == MovementType.HOVER);
		if (standingOnWater) {
			groundHeightTerrainAndWater = Math.max(groundHeightTerrain,
					map.terrain.getWaterHeight(this.location[0], this.location[1]));
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
			currentWalkableUnder = map.getHighestWalkableUnder(this.location[0], this.location[1]);
			War3MapViewer.gdxRayHeap.set(this.location[0], this.location[1], 40960, 0, 0, -81920);
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
		boolean changedAnimationProperties = false;
		if (swimming && !this.swimming) {
			if (this.unitAnimationListenerImpl.addSecondaryTag(AnimationTokens.SecondaryTag.SWIM)) {
				changedAnimationProperties = true;
			}
		}
		else if (!swimming && this.swimming) {
			if (this.unitAnimationListenerImpl.removeSecondaryTag(AnimationTokens.SecondaryTag.SWIM)) {
				changedAnimationProperties = true;
			}
		}
		if (working && !this.working) {
			if (this.unitAnimationListenerImpl.addSecondaryTag(AnimationTokens.SecondaryTag.WORK)) {
				changedAnimationProperties = true;
			}
		}
		else if (!working && this.working) {
			if (this.unitAnimationListenerImpl.removeSecondaryTag(AnimationTokens.SecondaryTag.WORK)) {
				changedAnimationProperties = true;
			}
		}
		if (changedAnimationProperties) {
			this.unitAnimationListenerImpl.forceResetCurrentAnimation();
		}
		boolean colorNeedsUpdate = false;
		if (invisible && !this.invisible) {
			// turn invisible instantly
			colorNeedsUpdate = true;
		}
		else if (!invisible && this.invisible) {
			// show instantly
			colorNeedsUpdate = true;
		}
		else if ((this.simulationUnit.getFadeTimer() != null) && this.simulationUnit.getFadeTimer().isRunning()) {
			colorNeedsUpdate = true;
		}
		this.invisible = invisible;
		this.swimming = swimming;
		this.working = working;
		if (colorNeedsUpdate) {
			updateColor(map);
		}
		final boolean dead = this.simulationUnit.isDead();
		final boolean corpse = this.simulationUnit.isCorpse();
		final boolean boneCorpse = this.simulationUnit.isBoneCorpse();
		final boolean building = this.simulationUnit.isBuilding();
		if (dead) {
			if (!this.dead) {
				this.unitAnimationListenerImpl.playAnimation(true, PrimaryTag.DEATH, SequenceUtils.EMPTY, 1.0f, true);
				removeSplats(map);
			}
		}
		else if (building != this.building) {
			if (building) {
				if (this.shadow != null) {
					this.shadow.hide();
				}
				createBuildingDecalSplats(map);
			}
			else {
				if (this.shadow != null) {
					this.shadow.show(map.terrain.centerOffset);
				}
				removeBuildingDecalSplats(map);
			}
			this.building = building;
		}
		if (boneCorpse && !this.boneCorpse) {
			if (this.simulationUnit.getUnitType().isHero()) {
				this.unitAnimationListenerImpl.playAnimationWithDuration(true, PrimaryTag.DISSIPATE,
						SequenceUtils.EMPTY, this.simulationUnit.getEndingDecayTime(map.simulation), true);
			}
			else {
				this.unitAnimationListenerImpl.playAnimationWithDuration(true, PrimaryTag.DECAY, SequenceUtils.BONE,
						this.simulationUnit.getEndingDecayTime(map.simulation), true);
			}
		}
		else if (corpse && !this.corpse) {
			this.unitAnimationListenerImpl.playAnimationWithDuration(true, PrimaryTag.DECAY, SequenceUtils.FLESH,
					map.simulation.getGameplayConstants().getDecayTime(), true);
		}
		this.dead = dead;
		this.corpse = corpse;
		this.boneCorpse = boneCorpse;
		if (!isPlayerPawn()) {
			this.location[2] = this.simulationUnit.getFlyHeight() + groundHeight;
		}
		final float selectionCircleHeight = this.selectionHeight + groundHeight;
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
		final float maxPitch = (float) Math.toRadians(this.typeData.getMaxPitch());
		final float maxRoll = (float) Math.toRadians(this.typeData.getMaxRoll());
		final float sampleRadius = this.typeData.getElevationSampleRadius();
		float pitch, roll;
		final float pitchSampleForwardX = this.location[0] + (sampleRadius * (float) Math.cos(facingRadians));
		final float pitchSampleForwardY = this.location[1] + (sampleRadius * (float) Math.sin(facingRadians));
		final float pitchSampleBackwardX = this.location[0] - (sampleRadius * (float) Math.cos(facingRadians));
		final float pitchSampleBackwardY = this.location[1] - (sampleRadius * (float) Math.sin(facingRadians));
		final double leftOfFacingAngle = facingRadians + (Math.PI / 2);
		final float rollSampleForwardX = this.location[0] + (sampleRadius * (float) Math.cos(leftOfFacingAngle));
		final float rollSampleForwardY = this.location[1] + (sampleRadius * (float) Math.sin(leftOfFacingAngle));
		final float rollSampleBackwardX = this.location[0] - (sampleRadius * (float) Math.cos(leftOfFacingAngle));
		final float rollSampleBackwardY = this.location[1] - (sampleRadius * (float) Math.sin(leftOfFacingAngle));
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
			if (isPlayerPawn()) {
				this.shadow.setHeightAbsolute(this.playerPawn.getZ() > (groundHeightTerrain + 9),
						groundHeight + map.imageWalkableZOffset);
			}
			else {
				this.shadow.setHeightAbsolute(currentWalkableUnder != null, groundHeight + map.imageWalkableZOffset);
			}
		}
		if (this.selectionCircle != null) {
			this.selectionCircle.move(dx, dy, map.terrain.centerOffset);
			this.selectionCircle.setHeightAbsolute(
					(currentWalkableUnder != null)
							|| ((movementType == MovementType.FLY) || (movementType == MovementType.HOVER)),
					selectionCircleHeight + map.imageWalkableZOffset);
		}
		if (this.selectionPreviewHighlight != null) {
			this.selectionPreviewHighlight.move(dx, dy, map.terrain.centerOffset);
			this.selectionPreviewHighlight.setHeightAbsolute(
					(currentWalkableUnder != null)
							|| ((movementType == MovementType.FLY) || (movementType == MovementType.HOVER)),
					selectionCircleHeight + map.imageWalkableZOffset);
		}
		this.unitAnimationListenerImpl.update();
		if (!dead && this.simulationUnit.isConstructingOrUpgrading()) {
			this.instance.setFrameByRatio(
					this.simulationUnit.getConstructionProgress() / this.simulationUnit.getUnitType().getBuildTime());
		}
	}

	private void removeSplats(final War3MapViewer map) {
		if (this.shadow != null) {
			this.shadow.destroy(Gdx.gl30, map.terrain.centerOffset);
			this.shadow = null;
		}
		removeBuildingDecalSplats(map);
		if (this.selectionCircle != null) {
			this.selectionCircle.destroy(Gdx.gl30, map.terrain.centerOffset);
			this.selectionCircle = null;
		}
		if (this.selectionPreviewHighlight != null) {
			this.selectionPreviewHighlight.destroy(Gdx.gl30, map.terrain.centerOffset);
			this.selectionPreviewHighlight = null;
		}
	}

	public void removeBuildingDecalSplats(final War3MapViewer map) {
		if (this.buildingShadowInstance != null) {
			this.buildingShadowInstance.remove();
			this.buildingShadowInstance = null;
		}
		if (this.uberSplat != null) {
			this.uberSplat.destroy(Gdx.gl30, map.terrain.centerOffset);
			this.uberSplat = null;
		}
	}

	public void createBuildingDecalSplats(final War3MapViewer map) {
		final float unitX = this.simulationUnit.getX();
		final float unitY = this.simulationUnit.getY();
		if (this.buildingShadowInstance == null) {
			final String buildingShadow = this.typeData.getBuildingShadow();
			if (buildingShadow != null) {
				this.buildingShadowInstance = map.terrain.addShadow(buildingShadow, unitX, unitY);
			}
		}
		if (this.uberSplat == null) {
			final String uberSplatTexturePath = this.typeData.getUberSplat();
			if (uberSplatTexturePath != null) {
				this.uberSplat = map.addUberSplatIngame(unitX, unitY, uberSplatTexturePath,
						this.typeData.getUberSplatScaleValue());
			}
		}
	}

	private float getGroundHeightSample(final float groundHeight, final CollidableDoodadComponent currentWalkableUnder,
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

	public EnumSet<AnimationTokens.SecondaryTag> getSecondaryAnimationTags() {
		return this.unitAnimationListenerImpl.secondaryAnimationTags;
	}

	public void repositioned(final War3MapViewer map) {
		final float prevX = this.location[0];
		final float prevY = this.location[1];
		final float simulationX = this.simulationUnit.getX();
		final float simulationY = this.simulationUnit.getY();
		final float dx = simulationX - prevX;
		final float dy = simulationY - prevY;
		if (this.shadow != null) {
			this.shadow.move(dx, dy, map.terrain.centerOffset);
		}
		if (this.selectionCircle != null) {
			this.selectionCircle.move(dx, dy, map.terrain.centerOffset);
		}
		if (this.selectionPreviewHighlight != null) {
			this.selectionPreviewHighlight.move(dx, dy, map.terrain.centerOffset);
		}
		this.location[0] = this.simulationUnit.getX();
		this.location[1] = this.simulationUnit.getY();
	}

	@Override
	public MdxComplexInstance getInstance() {
		return this.instance;
	}

	@Override
	public CWidget getSimulationWidget() {
		return this.simulationUnit;
	}

	@Override
	public boolean isIntersectedOnMeshAlways() {
		return this.simulationUnit.isBuilding();
	}

	@Override
	public float getSelectionScale() {
		return this.selectionScale;
	}

	@Override
	public float getX() {
		return this.location[0];
	}

	@Override
	public float getY() {
		return this.location[1];
	}

	@Override
	public float getZ() {
		return this.location[2];
	}

	@Override
	public void unassignSelectionCircle() {
		this.selectionCircle = null;
	}

	@Override
	public void assignSelectionCircle(final SplatMover t) {
		this.selectionCircle = t;
	}

	@Override
	public void unassignSelectionPreviewHighlight() {
		this.selectionPreviewHighlight = null;
	}

	@Override
	public void assignSelectionPreviewHighlight(final SplatMover t) {
		this.selectionPreviewHighlight = t;
	}

	@Override
	public boolean isSelectable(final CSimulation simulation, final int byPlayer) {
		return this.simulationUnit.isVisible(simulation, byPlayer); // later needs locust
	}

	@Override
	public SplatMover getSelectionPreviewHighlight() {
		return this.selectionPreviewHighlight;
	}

	public void onRemove(final War3MapViewer map) {
		removeSplats(map);
	}

	public void setPreferredSelectionReplacement(final RenderUnit preferredSelectionReplacement) {
		this.preferredSelectionReplacement = preferredSelectionReplacement;
	}

	public RenderUnit getPreferredSelectionReplacement() {
		return this.preferredSelectionReplacement;
	}

	@Override
	public SplatMover getSelectionCircle() {
		return this.selectionCircle;
	}

	public boolean groupsWith(final RenderUnit selectedUnit) {
		return this.simulationUnit.getUnitType() == selectedUnit.getSimulationUnit().getUnitType();
	}

	public void setPlayerColor(final int ordinal) {
		this.playerIndex = ordinal;
		getInstance().setTeamColor(ordinal);
	}

	public float getFacing() {
		return this.facing;
	}

	public void setFacing(final float facing) {
		this.facing = facing;
	}

	@Override
	public boolean isShowSelectionCircleAboveWater() {
		return this.simulationUnit.isMovementOnWaterAllowed()
				|| (this.simulationUnit.getMovementType() == MovementType.HOVER)
				|| (this.simulationUnit.getMovementType() == MovementType.FLY);
	}

	public boolean isPlayerPawn() {
		return this.playerPawn != null;
	}

	public RenderUnitType getTypeData() {
		return this.typeData;
	}

	public void setVertexColoring(final War3MapViewer map, final Color color) {
		this.currentColor = new float[] { color.r, color.g, color.b, color.a };
		updateColor(map);
	}

	public void setVertexColoring(final War3MapViewer map, final float r, final float g, final float b) {
		this.currentColor = new float[] { r, g, b, 1 };
		updateColor(map);
	}

	public void setVertexColoring(final War3MapViewer map, final float r, final float g, final float b, final float a) {
		final float[] color = new float[] { r, g, b, a };
		this.currentColor = color;
		updateColor(map);
	}

	private void updateColor(final War3MapViewer map) {
		float finalAlpha = this.currentColor[3];
		if (this.invisible) {
			finalAlpha *= 0.5f;
		}
		else {
			final CTimer fadeTimer = this.simulationUnit.getFadeTimer();
			if ((fadeTimer != null) && fadeTimer.isRunning()) {
				final float fadeAmount = map.getRemainingSecondsForRender(fadeTimer) / fadeTimer.getTimeoutTime();
				finalAlpha *= (fadeAmount * 0.5f) + 0.5f;
			}
		}
		this.instance.setVertexColor(this.currentColor[0], this.currentColor[1], this.currentColor[2], finalAlpha);
	}

	public float[] getVertexColoring() {
		return this.currentColor;
	}

	public void queueAnimation(final String whichAnimation) {
		final EnumSet<AnimationTokens.PrimaryTag> primaryTags = EnumSet.noneOf(AnimationTokens.PrimaryTag.class);
		final EnumSet<AnimationTokens.SecondaryTag> secondaryTag = EnumSet.noneOf(AnimationTokens.SecondaryTag.class);
		Sequence.populateTags(primaryTags, secondaryTag, whichAnimation);
		final AnimationTokens.PrimaryTag primaryTag = Sequence.any(primaryTags);
		this.unitAnimationListenerImpl.queueAnimation(primaryTag, secondaryTag, true);
	}

	public void playAnimation(final String whichAnimation) {
		final EnumSet<AnimationTokens.PrimaryTag> primaryTags = EnumSet.noneOf(AnimationTokens.PrimaryTag.class);
		final EnumSet<AnimationTokens.SecondaryTag> secondaryTag = EnumSet.noneOf(AnimationTokens.SecondaryTag.class);
		Sequence.populateTags(primaryTags, secondaryTag, whichAnimation);
		final AnimationTokens.PrimaryTag primaryTag = Sequence.any(primaryTags);
		this.unitAnimationListenerImpl.playAnimation(true, primaryTag, secondaryTag, 1.0f, true);
	}

	public void playAnimation(final int index) {
		this.unitAnimationListenerImpl.playAnimation(true, index, 1.0f, true);
	}

	public void playAnimationWithRarity(final String whichAnimation, final CRarityControl control) {
		final EnumSet<AnimationTokens.PrimaryTag> primaryTags = EnumSet.noneOf(AnimationTokens.PrimaryTag.class);
		final EnumSet<AnimationTokens.SecondaryTag> secondaryTag = EnumSet.noneOf(AnimationTokens.SecondaryTag.class);
		Sequence.populateTags(primaryTags, secondaryTag, whichAnimation);
		final AnimationTokens.PrimaryTag primaryTag = Sequence.any(primaryTags);
		// TODO: below is not doing probably what it should.... We can assume "control
		// == RARE" means to INTENTIONALLY PLAY THE RARE ONES but at the moment, this
		// flag we are passing means to ALLOW SOMETIMES playing the rare ones
		this.unitAnimationListenerImpl.playAnimation(true, primaryTag, secondaryTag, 1.0f,
				control == CRarityControl.RARE);
	}

	public void addAnimationProperties(final String properties, final boolean add) {
		final EnumSet<AnimationTokens.PrimaryTag> primaryTags = EnumSet.noneOf(AnimationTokens.PrimaryTag.class);
		final EnumSet<AnimationTokens.SecondaryTag> secondaryTags = EnumSet.noneOf(AnimationTokens.SecondaryTag.class);
		Sequence.populateTags(primaryTags, secondaryTags, properties);
		boolean changedAnimProps = false;
		if (add) {
			for (final AnimationTokens.SecondaryTag tag : secondaryTags) {
				if (this.unitAnimationListenerImpl.addSecondaryTag(tag)) {
					changedAnimProps = true;
				}
			}
		}
		else {
			for (final AnimationTokens.SecondaryTag tag : secondaryTags) {
				if (this.unitAnimationListenerImpl.removeSecondaryTag(tag)) {
					changedAnimProps = true;
				}
			}
		}
		if (changedAnimProps) {
			this.unitAnimationListenerImpl.forceResetCurrentAnimation();
		}
	}

	public void lockTargetFacing(final String boneNameString, final RenderUnit renderPeerTarget, final float offsetX,
			final float offsetY, final float offsetZ) {
		if (boneNameString != null) {
			if ("head".equals(boneNameString.toLowerCase())) {
				this.unitAnimationListenerImpl.lockHeadFacing(renderPeerTarget.instance,
						new Vector3(offsetX, offsetY, offsetZ));
			}
			else if ("turret".equals(boneNameString.toLowerCase())) {
				this.unitAnimationListenerImpl.lockTurretFacing(renderPeerTarget.instance,
						new Vector3(offsetX, offsetY, offsetZ));
			}
		}
	}

	public void resetLookAt() {
		this.unitAnimationListenerImpl.clearHeadFacing();
		this.unitAnimationListenerImpl.clearTurretFacing();
	}

	public void setupPlayerPawn(CAbilityPlayerPawn abilityPlayerPawn) {
		this.playerPawn = abilityPlayerPawn;
	}
}

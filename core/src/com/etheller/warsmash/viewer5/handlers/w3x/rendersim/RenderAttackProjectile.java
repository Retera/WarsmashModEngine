package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.List;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.IndexedSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetWidgetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;

public class RenderAttackProjectile implements RenderEffect {
	private static final Quaternion pitchHeap = new Quaternion();

	private final CAttackProjectile simulationProjectile;
	private final MdxComplexInstance modelInstance;
	private float x;
	private float y;
	private float z;
	private final float startingHeight;
	private final float arcPeakHeight;
	private float totalTravelDistance;

	private final float targetHeight;

	private float yaw;

	private float pitch;
	private boolean done = false;
	private float deathTimeElapsed;

	public RenderAttackProjectile(final CAttackProjectile simulationProjectile, final MdxComplexInstance modelInstance,
			final float z, final float arc, final War3MapViewer war3MapViewer) {
		this.simulationProjectile = simulationProjectile;
		this.modelInstance = modelInstance;
		this.x = simulationProjectile.getX();
		this.y = simulationProjectile.getY();
		this.z = z;
		this.startingHeight = z;
		final float targetX = this.simulationProjectile.getTarget().getX();
		final float targetY = this.simulationProjectile.getTarget().getY();
		final float dxToTarget = targetX - this.x;
		final float dyToTarget = targetY - this.y;
		final float d2DToTarget = (float) StrictMath.sqrt((dxToTarget * dxToTarget) + (dyToTarget * dyToTarget));
		final float startingDistance = d2DToTarget + this.totalTravelDistance;
		final CWidget widgetTarget = this.simulationProjectile.getTarget().visit(AbilityTargetWidgetVisitor.INSTANCE);
		float impactZ;
		float flyHeight;
		if ((simulationProjectile.getUnitAttack().getWeaponType() == CWeaponType.ARTILLERY) || (widgetTarget == null)) {
			impactZ = 0;
			flyHeight = 0;
		}
		else {
			impactZ = widgetTarget.getImpactZ();
			flyHeight = widgetTarget.getFlyHeight();
		}
		this.targetHeight = (war3MapViewer.terrain.getGroundHeight(targetX, targetY) + flyHeight + impactZ);
		this.arcPeakHeight = arc * startingDistance;
		this.yaw = (float) StrictMath.atan2(dyToTarget, dxToTarget);
	}

	@Override
	public boolean updateAnimations(final War3MapViewer war3MapViewer, final float deltaTime) {
		final boolean wasDone = this.done;
		if (this.done = this.simulationProjectile.isDone()) {
			final MdxModel model = (MdxModel) this.modelInstance.model;
			final List<Sequence> sequences = model.getSequences();
			final IndexedSequence sequence = SequenceUtils.selectSequence(PrimaryTag.DEATH, SequenceUtils.EMPTY,
					sequences, true);
			if ((sequence != null) && this.done && !wasDone) {
				this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
				this.modelInstance.setSequence(sequence.index);
			}
		}
		else {
			if (this.modelInstance.sequenceEnded || (this.modelInstance.sequence == -1)) {
				SequenceUtils.randomStandSequence(this.modelInstance);
			}
		}
		final float simX = this.simulationProjectile.getX();
		final float simY = this.simulationProjectile.getY();
		final float simDx = simX - this.x;
		final float simDy = simY - this.y;
		final float simD = (float) StrictMath.sqrt((simDx * simDx) + (simDy * simDy));
		final float speed = StrictMath.min(simD, this.simulationProjectile.getSpeed() * deltaTime);
		if (simD > 0) {
			this.x = this.x + ((speed * simDx) / simD);
			this.y = this.y + ((speed * simDy) / simD);
			final float targetX = this.simulationProjectile.getTargetX();
			final float targetY = this.simulationProjectile.getTargetY();
			final float dxToTarget = targetX - this.x;
			final float dyToTarget = targetY - this.y;
			final float d2DToTarget = (float) StrictMath.sqrt((dxToTarget * dxToTarget) + (dyToTarget * dyToTarget));
			final float startingDistance = d2DToTarget + this.totalTravelDistance;
			final float halfStartingDistance = startingDistance / 2f;

			final float dtsz = this.targetHeight - this.startingHeight;
			final float d1z = dtsz / (halfStartingDistance * 2);
			this.totalTravelDistance += speed;
			final float dz = d1z * this.totalTravelDistance;

			final float distanceToPeak = this.totalTravelDistance - halfStartingDistance;
			final float normPeakDist = distanceToPeak / halfStartingDistance;
			final float currentHeightPercentage = 1 - (normPeakDist * normPeakDist);
			final float arcCurrentHeight = currentHeightPercentage * this.arcPeakHeight;
			this.z = this.startingHeight + dz + arcCurrentHeight;

			if (!this.done) {
				this.yaw = (float) StrictMath.atan2(dyToTarget, dxToTarget);

				final float slope = (-2 * (normPeakDist) * this.arcPeakHeight) / halfStartingDistance;
				this.pitch = (float) StrictMath.atan2(slope + d1z, 1);
			}
		}
		if (this.done) {
			this.pitch = 0;
			this.deathTimeElapsed += deltaTime;
		}

		this.modelInstance.setLocation(this.x, this.y, this.z);
		this.modelInstance.localRotation.setFromAxisRad(0, 0, 1, this.yaw);
		this.modelInstance.rotate(pitchHeap.setFromAxisRad(0, -1, 0, this.pitch));
		war3MapViewer.worldScene.instanceMoved(this.modelInstance, this.x, this.y);

		final boolean everythingDone = this.simulationProjectile.isDone() && (this.modelInstance.sequenceEnded
				|| (this.deathTimeElapsed >= war3MapViewer.simulation.getGameplayConstants().getBulletDeathTime()));
		if (everythingDone) {
			war3MapViewer.worldScene.removeInstance(this.modelInstance);
		}
		return everythingDone;
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.parsers.mdlx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.IndexedSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.StandSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.projectile.CAttackProjectile;

public class RenderAttackProjectile {
	private static final Quaternion pitchHeap = new Quaternion();
	private static final Vector3 skewVector = new Vector3();

	private final CAttackProjectile simulationProjectile;
	private final MdxComplexInstance modelInstance;
	private float x;
	private float y;
	private float z;

	public RenderAttackProjectile(final CAttackProjectile simulationProjectile,
			final MdxComplexInstance modelInstance) {
		this.simulationProjectile = simulationProjectile;
		this.modelInstance = modelInstance;
		this.x = simulationProjectile.getX();
		this.y = simulationProjectile.getY();
		this.z = simulationProjectile.getZ();
	}

	public boolean updateAnimations(final War3MapViewer war3MapViewer) {
		if (this.simulationProjectile.isDone()) {
			final MdxModel model = (MdxModel) this.modelInstance.model;
			final List<Sequence> sequences = model.getSequences();
			final IndexedSequence sequence = StandSequence.selectSequence("death", sequences);
			if (this.modelInstance.sequence != sequence.index) {
				this.modelInstance.setSequence(sequence.index);
			}
		}
		else {
			if (this.modelInstance.sequenceEnded || (this.modelInstance.sequence == -1)) {
				StandSequence.randomStandSequence(this.modelInstance);
			}
		}
		final float simX = this.simulationProjectile.getX();
		final float simY = this.simulationProjectile.getY();
		final float simZ = this.simulationProjectile.getZ();
		final float simDx = simX - this.x;
		final float simDy = simY - this.y;
		final float simDz = simZ - this.z;
		final float simD = (float) Math.sqrt((simDx * simDx) + (simDy * simDy));
		final float deltaTime = Gdx.graphics.getDeltaTime();
		final float speed = Math.min(simD, this.simulationProjectile.getSpeed() * deltaTime);
		if (simD > 0) {
			this.x = this.x + ((speed * simDx) / simD);
			this.y = this.y + ((speed * simDy) / simD);
			this.z = this.z + ((speed * simDz) / simD);
		}

		final float dxToTarget = this.simulationProjectile.getTarget().getX() - this.x;
		final float dyToTarget = this.simulationProjectile.getTarget().getY() - this.y;
		final float dzToTarget = this.simulationProjectile.getTarget().getFlyHeight() - this.z;
		final float d2DToTarget = (float) Math.sqrt((dxToTarget * dxToTarget) + (dyToTarget * dyToTarget));
		final float yaw = (float) Math.atan2(dxToTarget, dyToTarget);

		final float pitch = (float) Math.atan2(dzToTarget, d2DToTarget);

		final float arcCurrentHeight = this.simulationProjectile.getArcCurrentHeight();
		final float oppositeYaw = yaw + (float) Math.PI;
		skewVector.set((float) (Math.cos(oppositeYaw) * Math.cos(Math.PI / 4)),
				(float) (Math.sin(oppositeYaw) * Math.cos(Math.PI / 4)), (float) (Math.sin(Math.PI / 4)));

		final float skewX = arcCurrentHeight * skewVector.x;
		final float skewY = arcCurrentHeight * skewVector.y;
		final float skewZ = arcCurrentHeight * skewVector.z;

		this.modelInstance.setLocation(this.x + skewX, this.y + skewY, this.z + skewZ);
//		this.modelInstance.localRotation.setFromAxisRad(0, 0, 1, yaw);
		this.modelInstance.localRotation.setFromAxisRad(0, -1, 0, pitch);
//		this.modelInstance.rotateLocal(pitchHeap.setFromAxisRad(0, -1, 0, pitch));
		war3MapViewer.worldScene.grid.moved(this.modelInstance);

		final boolean everythingDone = this.simulationProjectile.isDone() && this.modelInstance.sequenceEnded;
		if (everythingDone) {
			war3MapViewer.worldScene.removeInstance(this.modelInstance);
		}
		return everythingDone;
	}
}

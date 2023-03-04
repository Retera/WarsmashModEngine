package com.etheller.warsmash.viewer5.handlers.w3x.ui.thirdperson;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraPanControls;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget.UnitAnimationListenerImpl;

public class PlayerPawn {
	private static final RenderUnit[] intersectingUnit = new RenderUnit[1];
	private static final float ROOT_TWO = (float) Math.sqrt(2);
	private static final Quaternion tempQuat = new Quaternion();
	private static final Vector3 tempVec = new Vector3();
	public final CameraPanControls cameraPanControls;
	private final Vector3 location;
	private final Vector3 velocity;
	private float facingDegrees;
	private final ModelInstance pawnModelInstance;
	private final UnitAnimationListenerImpl animationProcessor;
	private final SplatMover unitShadowSplatDynamicIngame;
	private final MdxComplexInstance pawnComplexInstance;
	boolean wasAirborn = false;
	private boolean wasFalling;
	private RenderUnit lastIntersectedUnit;

	private final Vector3 lastIntersectedUnitLocation = new Vector3();
	private float lastIntersectedUnitFacing;

	public PlayerPawn(final ModelInstance pawnModelInstance, final UnitAnimationListenerImpl animationProcessor,
			final SplatMover unitShadowSplatDynamicIngame, final MdxComplexInstance pawnComplexInstance) {
		this.cameraPanControls = new CameraPanControls();
		this.location = new Vector3();
		this.velocity = new Vector3();
		this.pawnModelInstance = pawnModelInstance;
		this.animationProcessor = animationProcessor;
		this.unitShadowSplatDynamicIngame = unitShadowSplatDynamicIngame;
		this.pawnComplexInstance = pawnComplexInstance;
	}

	public CameraPanControls getCameraPanControls() {
		return this.cameraPanControls;
	}

	public void update(final War3MapViewer war3MapViewer) {
		float forwardSpeed;
		if (this.cameraPanControls.left) {
			setFacingDegrees(getFacingDegrees() + 4);
		}
		else if (this.cameraPanControls.right) {
			setFacingDegrees(getFacingDegrees() - 4);
		}
		int walkingAnimSpeed = 0;
		if (this.cameraPanControls.up) {
			forwardSpeed = (4);
			walkingAnimSpeed = 4;
		}
		else if (this.cameraPanControls.down) {
			forwardSpeed = (-4);
			walkingAnimSpeed = 2;
		}
		else {
			forwardSpeed = (0);
		}
		final float prevX = this.location.x;
		final float prevY = this.location.y;
		if (this.lastIntersectedUnit != null) {
			tempVec.set(this.location).sub(this.lastIntersectedUnitLocation);
			final float newFacing = this.lastIntersectedUnit.getFacing();
			final float deltaFacing = newFacing - this.lastIntersectedUnitFacing;
			this.facingDegrees += deltaFacing;
			tempQuat.setFromAxis(0, 0, 1, deltaFacing);
			tempQuat.transform(tempVec);
			this.location.set(this.lastIntersectedUnit.location).add(tempVec);
		}
		final float prevZBeneath = war3MapViewer.getNearestIntersectingZBeneath(this.location.x, this.location.y,
				this.location.z + 9, intersectingUnit);
		this.lastIntersectedUnit = intersectingUnit[0];
		if (this.lastIntersectedUnit != null) {
			this.lastIntersectedUnitLocation.set(intersectingUnit[0].location);
			this.lastIntersectedUnitFacing = this.lastIntersectedUnit.getFacing();
		}
		final boolean falling = prevZBeneath < this.location.z;
		if (falling) {
			// falling
			this.velocity.z -= 0.5;
			if ((this.velocity.x == 0) && (this.velocity.y == 0) && (forwardSpeed > 0)) {
				final double facingRad = Math.toRadians(this.facingDegrees);
				final float slowSpeed = forwardSpeed / 2;
				this.velocity.x = (float) (Math.cos(facingRad) * slowSpeed);
				this.velocity.y = (float) (Math.sin(facingRad) * slowSpeed);
			}
		}
		else {
			final double facingRad = Math.toRadians(this.facingDegrees);
			this.velocity.x = (float) (Math.cos(facingRad) * forwardSpeed);
			this.velocity.y = (float) (Math.sin(facingRad) * forwardSpeed);
			this.velocity.z = 0;
		}
		this.wasFalling = falling;
		final float speed = this.velocity.len();
		tempVec.set(this.location).add(this.velocity);
		if ((speed > 0) && !war3MapViewer.is3DTravelBlocked(this.location, tempVec, 9, getHeight())) {
			final float nextZBeneath = war3MapViewer.getNearestIntersectingZBeneath(tempVec.x, tempVec.y, tempVec.z + 9,
					intersectingUnit);
			this.lastIntersectedUnit = intersectingUnit[0];
			if (this.lastIntersectedUnit != null) {
				this.lastIntersectedUnitLocation.set(intersectingUnit[0].location);
				this.lastIntersectedUnitFacing = this.lastIntersectedUnit.getFacing();
			}
			this.location.set(tempVec.x, tempVec.y, Math.max(tempVec.z, nextZBeneath));
		}
		else {
			tempVec.set(this.location.x, this.location.y, Math.max(prevZBeneath, this.location.z + this.velocity.z));
			if (!war3MapViewer.is3DTravelBlocked(this.location, tempVec, 9, getHeight())) {
				this.location.z = tempVec.z;
			}
			else {
				this.wasFalling = false;
			}
		}
		final float zBeneath = war3MapViewer.getNearestIntersectingZBeneath(this.location.x, this.location.y,
				this.location.z + 9, intersectingUnit);
		if ((zBeneath + 9) < this.location.z) {
			this.wasAirborn = true;
			if (this.pawnComplexInstance.sequenceEnded) {
				this.animationProcessor.playAnimation(false, PrimaryTag.STAND, SequenceUtils.ALTERNATE, 1.0f, true);
			}
		}
		else {
			if (this.wasAirborn) {
				if (this.pawnComplexInstance.sequenceEnded) {
					this.wasAirborn = false;
				}
				this.animationProcessor.playAnimation(false, PrimaryTag.MORPH, SequenceUtils.ALTERNATE, 1.0f, true);
			}
			else {
				if (walkingAnimSpeed > 0) {
					this.animationProcessor.playWalkAnimation(false, walkingAnimSpeed, true);
				}
				else {
					this.animationProcessor.playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
				}
			}
		}
		this.unitShadowSplatDynamicIngame.move(this.location.x - prevX, this.location.y - prevY,
				war3MapViewer.terrain.centerOffset);
		this.pawnModelInstance.setLocation(this.location);
		tempQuat.setFromAxis(0, 0, 1, getFacingDegrees());
		this.pawnModelInstance.setLocalRotation(this.tempQuat);
	}

	public UnitAnimationListenerImpl getAnimationProcessor() {
		return this.animationProcessor;
	}

	public Vector3 getLocation() {
		return this.location;
	}

	public float getFacingDegrees() {
		return this.facingDegrees;
	}

	public void setFacingDegrees(float facingDegrees) {
		if (facingDegrees < -180) {
			facingDegrees += 360;
		}
		if (facingDegrees > 180) {
			facingDegrees -= 360;
		}
		this.facingDegrees = facingDegrees;
	}

	public float getHeight() {
		return 100;
	}

	public void setVelocityZ(final float zGravity) {
		this.velocity.z = zGravity;
	}

	public void jump() {
		if (!this.wasFalling) {
			setVelocityZ(9);
			this.location.z += 1;
			this.animationProcessor.playAnimation(true, PrimaryTag.MORPH, SequenceUtils.EMPTY, 1.0f, true);
		}
	}
}

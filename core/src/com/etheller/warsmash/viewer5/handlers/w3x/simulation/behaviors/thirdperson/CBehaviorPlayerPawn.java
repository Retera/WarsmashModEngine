package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.thirdperson;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxCharacterInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxCharacterNode;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraPanControls;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson.CAbilityPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorPlayerPawn implements CBehavior {
	private static final RenderUnit[] intersectingUnit = new RenderUnit[1];
	private static final float ROOT_TWO = (float) Math.sqrt(2);
	private static final Quaternion tempQuat = new Quaternion();
	private static final Vector3 tempVec = new Vector3();
	private static final Vector3 tempVec2 = new Vector3();
	private final CUnit unit;
	private final CAbilityPlayerPawn playerPawn;

	public final CameraPanControls cameraPanControls;
	private final Vector3 velocity;

	boolean wasAirborn = false;
	private boolean wasFalling;
	private RenderUnit lastIntersectedUnit;

	private final Vector3 lastIntersectedUnitLocation = new Vector3();
	private float lastIntersectedUnitFacing;
	private float forwardSpeed;
	private MdxCharacterNode spineLow;

	private War3MapViewer viewerWorldAccess;
	private MdxCharacterInstance characterModelInstance;

	public CBehaviorPlayerPawn(final CUnit unit, final CAbilityPlayerPawn playerPawn) {
		this.unit = unit;
		this.playerPawn = playerPawn;
		this.cameraPanControls = new CameraPanControls();
		this.velocity = new Vector3();
	}

	public void setViewerWorldAccess(final War3MapViewer viewerWorldAccess) {
		this.viewerWorldAccess = viewerWorldAccess;

		final RenderUnit renderPeer = viewerWorldAccess.getRenderPeer(this.unit);
		this.spineLow = (MdxCharacterNode) renderPeer.instance.inefficientlyGetNodeByNameSearch("spinelow");
		this.characterModelInstance = (MdxCharacterInstance) renderPeer.instance;
		if (this.spineLow != null) {
			this.spineLow.createSubSequencer(this.characterModelInstance);
		}
	}

	@Override
	public CBehavior update(final CSimulation game) {
		int shuffle = 0;
		if (this.cameraPanControls.left) {
			setFacingDegrees(this.unit.getFacing() + 12);
			shuffle = 1;
		}
		else if (this.cameraPanControls.right) {
			setFacingDegrees(this.unit.getFacing() - 12);
			shuffle = -1;
		}
		int walking = 0;
		if (this.cameraPanControls.up) {
			this.forwardSpeed = (18);
			walking = 1;
		}
		else if (this.cameraPanControls.down) {
			this.forwardSpeed = (-18);
			walking = -1;
		}
		else {
			this.forwardSpeed = (0);
		}
		final float prevX = this.unit.getX();
		final float prevY = this.unit.getY();
		if (this.lastIntersectedUnit != null) {
			tempVec.set(prevX, prevY, this.playerPawn.getZ()).sub(this.lastIntersectedUnitLocation);
			final float newFacing = this.lastIntersectedUnit.getFacing();
			final float deltaFacing = newFacing - this.lastIntersectedUnitFacing;
			this.unit.setFacing(this.unit.getFacing() + deltaFacing);
			tempQuat.setFromAxis(0, 0, 1, deltaFacing);
			tempQuat.transform(tempVec);
			tempVec2.set(this.lastIntersectedUnit.location).add(tempVec);
			this.unit.setPoint(tempVec2.x, tempVec2.y, game.getWorldCollision(), game.getRegionManager());
			this.playerPawn.setZ(tempVec2.z);
		}
		final float prevZBeneath = this.viewerWorldAccess.getNearestIntersectingZBeneath(this.unit.getX(),
				this.unit.getY(), this.playerPawn.getZ() + 9, intersectingUnit);
		this.lastIntersectedUnit = intersectingUnit[0];
		if (this.lastIntersectedUnit != null) {
			this.lastIntersectedUnitLocation.set(intersectingUnit[0].location);
			this.lastIntersectedUnitFacing = this.lastIntersectedUnit.getFacing();
		}
		final boolean falling = prevZBeneath < this.playerPawn.getZ();
		if (falling) {
			// falling
			this.velocity.z -= 3;
			if ((this.velocity.x == 0) && (this.velocity.y == 0) && (this.forwardSpeed > 0)) {
				final double facingRad = Math.toRadians(this.unit.getFacing());
				final float slowSpeed = this.forwardSpeed / 2;
				this.velocity.x = (float) (Math.cos(facingRad) * slowSpeed);
				this.velocity.y = (float) (Math.sin(facingRad) * slowSpeed);
			}
		}
		else {
			final double facingRad = Math.toRadians(this.unit.getFacing());
			this.velocity.x = (float) (Math.cos(facingRad));
			this.velocity.y = (float) (Math.sin(facingRad));
			this.velocity.z = 0;
			tempVec.set(this.unit.getX(), this.unit.getY(), this.playerPawn.getZ()).add(this.velocity);
			final float nextZBeneath = this.viewerWorldAccess.getNearestIntersectingZBeneath(tempVec.x, tempVec.y,
					tempVec.z + 9, intersectingUnit);
			this.velocity.z = Math.max(-this.forwardSpeed,
					Math.min(this.forwardSpeed, nextZBeneath - this.playerPawn.getZ()));
			this.velocity.nor();
			this.velocity.scl(this.forwardSpeed);
		}
		this.wasFalling = (prevZBeneath + 9) < this.playerPawn.getZ();
		final float speed = this.velocity.len();
		tempVec.set(this.unit.getX(), this.unit.getY(), this.playerPawn.getZ()).add(this.velocity);
		final float stairsHeight = 9;
		if ((speed > 0) && !this.viewerWorldAccess.is3DTravelBlocked(
				tempVec2.set(this.unit.getX(), this.unit.getY(), this.playerPawn.getZ()), tempVec, stairsHeight,
				getHeight(), tempVec)) {
			final float nextZBeneath = this.viewerWorldAccess.getNearestIntersectingZBeneath(tempVec.x, tempVec.y,
					tempVec.z + 9, intersectingUnit);
			this.lastIntersectedUnit = intersectingUnit[0];
			if (this.lastIntersectedUnit != null) {
				this.lastIntersectedUnitLocation.set(intersectingUnit[0].location);
				this.lastIntersectedUnitFacing = this.lastIntersectedUnit.getFacing();
			}
			this.unit.setPoint(tempVec.x, tempVec.y, game.getWorldCollision(), game.getRegionManager());
			this.playerPawn.setZ(falling ? Math.max(tempVec.z, nextZBeneath)
					: Math.max(tempVec.z - this.forwardSpeed, nextZBeneath));
		}
		else {
			tempVec2.set(this.unit.getX(), this.unit.getY(),
					Math.max(prevZBeneath, this.playerPawn.getZ() + this.velocity.z));
			if (!this.viewerWorldAccess.is3DTravelBlocked(
					tempVec.set(this.unit.getX(), this.unit.getY(), this.playerPawn.getZ()), tempVec2, stairsHeight,
					getHeight(), tempVec)) {
				this.playerPawn.setZ(tempVec2.z);
			}
			else {
				this.unit.setPoint(tempVec2.x, tempVec2.y, game.getWorldCollision(), game.getRegionManager());
				this.playerPawn.setZ(tempVec2.z);
				this.velocity.z = 0;
				this.wasFalling = false;
			}
		}
		final float zBeneath = this.viewerWorldAccess.getNearestIntersectingZBeneath(this.unit.getX(), this.unit.getY(),
				this.playerPawn.getZ() + 9, intersectingUnit);
		if ((zBeneath + 9) < this.playerPawn.getZ()) {
			this.wasAirborn = true;
			if (this.characterModelInstance.sequenceEnded) {
				this.unit.getUnitAnimationListener().playAnimation(false,
						this.velocity.z > 0 ? PrimaryTag.JUMP : PrimaryTag.FALL, SequenceUtils.EMPTY, 1.0f, true);
			}
		}
		else {
			if (this.wasAirborn) {
				if (this.characterModelInstance.sequenceEnded) {
					this.wasAirborn = false;
				}
				else {
					if (this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.JUMPEND,
							SequenceUtils.EMPTY, 1.0f, true)) {
						if (this.spineLow != null) {
							this.spineLow.subSequencer.setSequence(this.characterModelInstance.sequence,
									(MdxModel) this.characterModelInstance.model, this.characterModelInstance);
						}
					}
					if (walking != 0) {
						this.wasAirborn = false;
					}
				}
			}
			else {
				if (walking != 0) {
					this.unit.getUnitAnimationListener().playAnimation(false,
							walking > 0 ? PrimaryTag.WALK : PrimaryTag.WALKBACKWARDS, SequenceUtils.FAST, 1.0f, true);
				}
				else {
					switch (shuffle) {
					case 1:
						this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.SHUFFLELEFT,
								SequenceUtils.EMPTY, 1.0f, true);
						break;
					case 0:
						this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY,
								1.0f, true);
						break;
					case -1:
						this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.SHUFFLERIGHT,
								SequenceUtils.EMPTY, 1.0f, true);
						break;
					}
				}
			}
		}
		return this;
	}

	@Override
	public void begin(final CSimulation game) {

	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
		this.unit.setDefaultBehavior(this);
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.stop;
	}

	public void setFacingDegrees(float facingDegrees) {
		if (facingDegrees < -180) {
			facingDegrees += 360;
		}
		if (facingDegrees > 180) {
			facingDegrees -= 360;
		}
		this.unit.setFacing(facingDegrees);
	}

	public float getHeight() {
		return 100;
	}

	public void setVelocityZ(final float zGravity) {
		this.velocity.z = zGravity;
	}

	public void jump() {
		if (!this.wasFalling) {
			setVelocityZ(27);
			this.playerPawn.setZ(this.playerPawn.getZ() + 1);
			this.unit.getUnitAnimationListener().playAnimation(true, PrimaryTag.JUMPSTART, SequenceUtils.EMPTY, 1.0f,
					true);
		}
	}

	public float getForwardSpeed() {
		return this.forwardSpeed;
	}

	public CameraPanControls getCameraPanControls() {
		return this.cameraPanControls;
	}

	public Vector3 getVelocity() {
		return this.velocity;
	}

}

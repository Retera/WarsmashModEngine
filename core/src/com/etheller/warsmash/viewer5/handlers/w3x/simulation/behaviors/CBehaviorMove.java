package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWorldCollision;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CPathfindingProcessor;

public class CBehaviorMove implements CBehavior {
	private static boolean ALWAYS_INTERRUPT_MOVE = false;

	private static final Rectangle tempRect = new Rectangle();
	private final CUnit unit;
	private int highlightOrderId;
	private final TargetVisitingResetter targetVisitingResetter;

	public CBehaviorMove(final CUnit unit) {
		this.unit = unit;
		this.targetVisitingResetter = new TargetVisitingResetter();
	}

	private boolean wasWithinPropWindow = false;
	private List<Point2D.Float> path = null;
	private CPathfindingProcessor.GridMapping gridMapping;
	private Point2D.Float target;
	private int searchCycles = 0;
	private CUnit followUnit;
	private CRangedBehavior rangedBehavior;
	private boolean firstUpdate = true;
	private boolean disableCollision = false;
	private boolean pathfindingActive = false;
	private boolean firstPathfindJob = false;
	private boolean pathfindingFailedGiveUp;
	private int giveUpUntilTurnTick;

	public CBehaviorMove reset(final int highlightOrderId, final AbilityTarget target) {
		target.visit(this.targetVisitingResetter.reset(highlightOrderId));
		this.rangedBehavior = null;
		this.disableCollision = false;
		return this;
	}

	public CBehaviorMove reset(final AbilityTarget target, final CRangedBehavior rangedBehavior,
			final boolean disableCollision) {
		final int highlightOrderId = rangedBehavior.getHighlightOrderId();
		target.visit(this.targetVisitingResetter.reset(highlightOrderId));
		this.rangedBehavior = rangedBehavior;
		this.disableCollision = disableCollision;
		return this;
	}

	private void internalResetMove(final int highlightOrderId, final float targetX, final float targetY) {
		this.highlightOrderId = highlightOrderId;
		this.wasWithinPropWindow = false;
		this.gridMapping = CPathfindingProcessor.isCollisionSizeBetterSuitedForCorners(
				this.unit.getUnitType().getCollisionSize()) ? CPathfindingProcessor.GridMapping.CORNERS
						: CPathfindingProcessor.GridMapping.CELLS;
		this.target = new Point2D.Float(targetX, targetY);
		this.path = null;
		this.searchCycles = 0;
		this.followUnit = null;
		this.firstUpdate = true;
		this.pathfindingFailedGiveUp = false;
		this.giveUpUntilTurnTick = 0;
	}

	private void internalResetMove(final int highlightOrderId, final CUnit followUnit) {
		this.highlightOrderId = highlightOrderId;
		this.wasWithinPropWindow = false;
		this.gridMapping = CPathfindingProcessor.isCollisionSizeBetterSuitedForCorners(
				this.unit.getUnitType().getCollisionSize()) ? CPathfindingProcessor.GridMapping.CORNERS
						: CPathfindingProcessor.GridMapping.CELLS;
		this.target = new Float(followUnit.getX(), followUnit.getY());
		this.path = null;
		this.searchCycles = 0;
		this.followUnit = followUnit;
		this.firstUpdate = true;
		this.pathfindingFailedGiveUp = false;
		this.giveUpUntilTurnTick = 0;
	}

	@Override
	public int getHighlightOrderId() {
		return this.highlightOrderId;
	}

	@Override
	public CBehavior update(final CSimulation simulation) {
		if ((this.rangedBehavior != null) && this.rangedBehavior.isWithinRange(simulation)) {
			return this.rangedBehavior.update(simulation);
		}
		if (this.firstUpdate) {
			// when units start moving, if they're on top of other units, maybe push them to
			// the side
			this.unit.setPointAndCheckUnstuck(this.unit.getX(), this.unit.getY(), simulation);
			this.firstUpdate = false;
		}
		if (this.pathfindingFailedGiveUp) {
			onMoveGiveUp(simulation);
			return this.unit.pollNextOrderBehavior(simulation);
		}
		final float prevX = this.unit.getX();
		final float prevY = this.unit.getY();

		MovementType movementType = this.unit.getUnitType().getMovementType();
		if (movementType == null) {
			movementType = MovementType.DISABLED;
		}
		else if ((movementType == MovementType.FOOT) && this.disableCollision) {
			movementType = MovementType.FOOT_NO_COLLISION;
		}
		final PathingGrid pathingGrid = simulation.getPathingGrid();
		final CWorldCollision worldCollision = simulation.getWorldCollision();
		final float collisionSize = this.unit.getUnitType().getCollisionSize();
		final float startFloatingX = prevX;
		final float startFloatingY = prevY;
		if (this.path == null) {
			if (!this.pathfindingActive) {
				if (this.followUnit != null) {
					this.target.x = this.followUnit.getX();
					this.target.y = this.followUnit.getY();
				}
				simulation.findNaiveSlowPath(this.unit, this.followUnit, startFloatingX, startFloatingY, this.target,
						movementType, collisionSize, true, this);
				this.pathfindingActive = true;
				this.firstPathfindJob = true;
			}
		}
		else if ((this.followUnit != null) && (this.path.size() > 1) && (this.target.distance(this.followUnit.getX(),
				this.followUnit.getY()) > (0.1 * this.target.distance(this.unit.getX(), this.unit.getY())))) {
			this.target.x = this.followUnit.getX();
			this.target.y = this.followUnit.getY();
			if (this.pathfindingActive) {
				simulation.removeFromPathfindingQueue(this);
			}
			simulation.findNaiveSlowPath(this.unit, this.followUnit, startFloatingX, startFloatingY, this.target,
					movementType, collisionSize, this.searchCycles < 4, this);
			this.pathfindingActive = true;
		}
		float currentTargetX;
		float currentTargetY;
		if ((this.path == null) || this.path.isEmpty()) {
			if (this.followUnit != null) {
				currentTargetX = this.followUnit.getX();
				currentTargetY = this.followUnit.getY();
			}
			else {
				currentTargetX = this.target.x;
				currentTargetY = this.target.y;
			}
		}
		else {
			if ((this.followUnit != null) && (this.path.size() == 1)) {
				currentTargetX = this.followUnit.getX();
				currentTargetY = this.followUnit.getY();
			}
			else {
				final Point2D.Float nextPathElement = this.path.get(0);
				currentTargetX = nextPathElement.x;
				currentTargetY = nextPathElement.y;
			}
		}

		float deltaX = currentTargetX - prevX;
		float deltaY = currentTargetY - prevY;
		double goalAngleRad = Math.atan2(deltaY, deltaX);
		float goalAngle = (float) Math.toDegrees(goalAngleRad);
		if (goalAngle < 0) {
			goalAngle += 360;
		}
		float facing = this.unit.getFacing();
		float delta = goalAngle - facing;
		final float propulsionWindow = this.unit.getUnitType().getPropWindow();
		final float turnRate = this.unit.getUnitType().getTurnRate();
		final int speed = this.unit.getSpeed();

		if (delta < -180) {
			delta = 360 + delta;
		}
		if (delta > 180) {
			delta = -360 + delta;
		}
		float absDelta = Math.abs(delta);

		if ((absDelta <= 1.0) && (absDelta != 0)) {
			this.unit.setFacing(goalAngle);
		}
		else {
			float angleToAdd = Math.signum(delta) * (float) Math.toDegrees(turnRate);
			if (absDelta < Math.abs(angleToAdd)) {
				angleToAdd = delta;
			}
			facing += angleToAdd;
			this.unit.setFacing(facing);
		}
		final boolean blockedByGiveUpUntilTickDelay = simulation.getGameTurnTick() < this.giveUpUntilTurnTick;
		if (!blockedByGiveUpUntilTickDelay && (this.path != null) && !this.pathfindingActive
				&& (absDelta < propulsionWindow)) {
			final float speedTick = speed * WarsmashConstants.SIMULATION_STEP_TIME;
			double continueDistance = speedTick;
			do {
				boolean done;
				float nextX, nextY;
				final double travelDistance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
				if (travelDistance <= continueDistance) {
					nextX = currentTargetX;
					nextY = currentTargetY;
					continueDistance = continueDistance - travelDistance;
					done = true;
				}
				else {
					final double radianFacing = Math.toRadians(facing);
					nextX = (prevX + (float) (Math.cos(radianFacing) * continueDistance));
					nextY = (prevY + (float) (Math.sin(radianFacing) * continueDistance));
					continueDistance = 0;
//					done = (this.gridMapping.getX(pathingGrid, nextX) == this.gridMapping.getX(pathingGrid,
//							currentTargetX))
//							&& (this.gridMapping.getY(pathingGrid, nextY) == this.gridMapping.getY(pathingGrid,
//									currentTargetY));
					done = false;
				}
				tempRect.set(this.unit.getCollisionRectangle());
				tempRect.setCenter(nextX, nextY);
				if ((movementType == null) || (pathingGrid.isPathable(nextX, nextY, movementType, collisionSize)// ((int)
						// collisionSize
						// / 16)
						// * 16
						&& !worldCollision.intersectsAnythingOtherThan(tempRect, this.unit, movementType))) {
					this.unit.setPoint(nextX, nextY, worldCollision, simulation.getRegionManager());
					if (done) {
						// if we're making headway along the path then it's OK to start thinking fast
						// again
						if (travelDistance > 0) {
							this.searchCycles = 0;
						}
						if (this.path.isEmpty()) {
							onMoveGiveUp(simulation);
							return this.unit.pollNextOrderBehavior(simulation);
						}
						else {
							System.out.println(this.path);
							final Float removed = this.path.remove(0);
							System.out.println(
									"We think we reached  " + removed + " because we are at " + nextX + "," + nextY);
							final boolean emptyPath = this.path.isEmpty();
							if (emptyPath) {
								if (this.followUnit != null) {
									currentTargetX = this.followUnit.getX();
									currentTargetY = this.followUnit.getY();
								}
								else {
									currentTargetX = this.target.x;
									currentTargetY = this.target.y;
								}
							}
							else {
								if ((this.followUnit != null) && (this.path.size() == 1)) {
									currentTargetX = this.followUnit.getX();
									currentTargetY = this.followUnit.getY();
								}
								else {
									final Point2D.Float firstPathElement = this.path.get(0);
									currentTargetX = firstPathElement.x;
									currentTargetY = firstPathElement.y;
								}
							}
							deltaY = currentTargetY - nextY;
							deltaX = currentTargetX - nextX;
							if ((deltaX == 0.000f) && (deltaY == 0.000f) && this.path.isEmpty()) {
								onMoveGiveUp(simulation);
								return this.unit.pollNextOrderBehavior(simulation);
							}
							System.out.println("new target: " + currentTargetX + "," + currentTargetY);
							System.out.println("new delta: " + deltaX + "," + deltaY);
							goalAngleRad = Math.atan2(deltaY, deltaX);
							goalAngle = (float) Math.toDegrees(goalAngleRad);
							if (goalAngle < 0) {
								goalAngle += 360;
							}
							facing = this.unit.getFacing();
							delta = goalAngle - facing;

							if (delta < -180) {
								delta = 360 + delta;
							}
							if (delta > 180) {
								delta = -360 + delta;
							}
							absDelta = Math.abs(delta);
							if (absDelta >= propulsionWindow) {
								if (this.wasWithinPropWindow) {
									this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND,
											SequenceUtils.EMPTY, 1.0f, true);
								}
								this.wasWithinPropWindow = false;
								return this;
							}
						}
					}
				}
				else {
					if (this.followUnit != null) {
						this.target.x = this.followUnit.getX();
						this.target.y = this.followUnit.getY();
					}
					if (!this.pathfindingActive) {
						simulation.findNaiveSlowPath(this.unit, this.followUnit, startFloatingX, startFloatingY,
								this.target, movementType, collisionSize, this.searchCycles < 4, this);
						this.pathfindingActive = true;
						this.searchCycles++;
						return this;
					}
				}
				this.unit.getUnitAnimationListener().playWalkAnimation(false, this.unit.getSpeed(), true);
				this.wasWithinPropWindow = true;
			}
			while (continueDistance > 0);
		}
		else {
			// If this happens, the unit is facing the wrong way, and has to turn before
			// moving.
			if (this.wasWithinPropWindow) {
				this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
						true);
			}
			this.wasWithinPropWindow = false;
		}

		return this;
	}

	private void onMoveGiveUp(final CSimulation simulation) {
		if (this.rangedBehavior != null) {
			this.rangedBehavior.endMove(simulation, true);
		}
	}

	private final class TargetVisitingResetter implements AbilityTargetVisitor<Void> {
		private int highlightOrderId;

		private TargetVisitingResetter reset(final int highlightOrderId) {
			this.highlightOrderId = highlightOrderId;
			return this;
		}

		@Override
		public Void accept(final AbilityPointTarget target) {
			internalResetMove(this.highlightOrderId, target.x, target.y);
			return null;
		}

		@Override
		public Void accept(final CUnit target) {
			internalResetMove(this.highlightOrderId, target);
			return null;
		}

		@Override
		public Void accept(final CDestructable target) {
			internalResetMove(this.highlightOrderId, target.getX(), target.getY());
			return null;
		}

		@Override
		public Void accept(final CItem target) {
			internalResetMove(this.highlightOrderId, target.getX(), target.getY());
			return null;
		}
	}

	@Override
	public void begin(final CSimulation game) {

	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
		if (ALWAYS_INTERRUPT_MOVE) {
			game.removeFromPathfindingQueue(this);
			this.pathfindingActive = false;
		}
		if (this.rangedBehavior != null) {
			this.rangedBehavior.endMove(game, interrupted);
		}
	}

	public CUnit getUnit() {
		return this.unit;
	}

	public void pathFound(final List<Point2D.Float> waypoints, final CSimulation simulation) {
		this.pathfindingActive = false;

		final float prevX = this.unit.getX();
		final float prevY = this.unit.getY();

		MovementType movementType = this.unit.getUnitType().getMovementType();
		if (movementType == null) {
			movementType = MovementType.DISABLED;
		}
		else if ((movementType == MovementType.FOOT) && this.disableCollision) {
			movementType = MovementType.FOOT_NO_COLLISION;
		}
		final PathingGrid pathingGrid = simulation.getPathingGrid();
		final CWorldCollision worldCollision = simulation.getWorldCollision();
		final float collisionSize = this.unit.getUnitType().getCollisionSize();
		final float startFloatingX = prevX;
		final float startFloatingY = prevY;

		this.path = waypoints;
		if (this.firstPathfindJob) {
			this.firstPathfindJob = false;
			System.out.println("init path " + this.path);
			// check for smoothing
			if (!this.path.isEmpty()) {
				float lastX = startFloatingX;
				float lastY = startFloatingY;
				float smoothingGroupStartX = startFloatingX;
				float smoothingGroupStartY = startFloatingY;
				final Point2D.Float firstPathElement = this.path.get(0);
				double totalPathDistance = firstPathElement.distance(lastX, lastY);
				lastX = firstPathElement.x;
				lastY = firstPathElement.y;
				int smoothingStartIndex = -1;
				for (int i = 0; i < (this.path.size() - 1); i++) {
					final Point2D.Float nextPossiblePathElement = this.path.get(i + 1);
					totalPathDistance += nextPossiblePathElement.distance(lastX, lastY);
					if ((totalPathDistance < (1.15
							* nextPossiblePathElement.distance(smoothingGroupStartX, smoothingGroupStartY)))
							&& pathingGrid.isPathable((smoothingGroupStartX + nextPossiblePathElement.x) / 2,
									(smoothingGroupStartY + nextPossiblePathElement.y) / 2, movementType)) {
						if (smoothingStartIndex == -1) {
							smoothingStartIndex = i;
						}
					}
					else {
						if (smoothingStartIndex != -1) {
							for (int j = i - 1; j >= smoothingStartIndex; j--) {
								this.path.remove(j);
							}
							i = smoothingStartIndex;
						}
						smoothingStartIndex = -1;
						final Point2D.Float smoothGroupNext = this.path.get(i);
						smoothingGroupStartX = smoothGroupNext.x;
						smoothingGroupStartY = smoothGroupNext.y;
						totalPathDistance = nextPossiblePathElement.distance(smoothGroupNext);
					}
					lastX = nextPossiblePathElement.x;
					lastY = nextPossiblePathElement.y;
				}
				if (smoothingStartIndex != -1) {
					for (int j = smoothingStartIndex; j < (this.path.size() - 1); j++) {
						final Point2D.Float removed = this.path.remove(j);
					}
				}
			}
		}
		else if (this.path.isEmpty() || (this.searchCycles > 6)) {
			if (this.searchCycles > 9) {
				this.pathfindingFailedGiveUp = true;
			}
			else {
				this.giveUpUntilTurnTick = simulation.getGameTurnTick()
						+ (int) (5 / WarsmashConstants.SIMULATION_STEP_TIME);
			}
		}
	}
}

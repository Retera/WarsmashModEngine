package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWorldCollision;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CPathfindingProcessor;

public class CMoveOrder implements COrder {
	private static final Rectangle tempRect = new Rectangle();
	private final CUnit unit;
	private boolean wasWithinPropWindow = false;
	private List<Point2D.Float> path = null;
	private final CPathfindingProcessor.GridMapping gridMapping;
	private final Point2D.Float target;

	public CMoveOrder(final CUnit unit, final float targetX, final float targetY) {
		this.unit = unit;
		this.gridMapping = CPathfindingProcessor.isCollisionSizeBetterSuitedForCorners(
				unit.getUnitType().getCollisionSize()) ? CPathfindingProcessor.GridMapping.CORNERS
						: CPathfindingProcessor.GridMapping.CELLS;
		this.target = new Point2D.Float(targetX, targetY);
	}

	@Override
	public boolean update(final CSimulation simulation) {
		final float prevX = this.unit.getX();
		final float prevY = this.unit.getY();

		final MovementType movementType = this.unit.getUnitType().getMovementType();
		final PathingGrid pathingGrid = simulation.getPathingGrid();
		final CWorldCollision worldCollision = simulation.getWorldCollision();
		final float collisionSize = this.unit.getUnitType().getCollisionSize();
		final float startFloatingX = prevX;
		final float startFloatingY = prevY;
		if (this.path == null) {
			this.path = simulation.findNaiveSlowPath(this.unit, startFloatingX, startFloatingY, this.target,
					movementType, collisionSize);
			System.out.println("init path " + this.path);
			// check for smoothing
			if (!this.path.isEmpty()) {
				this.path.add(this.target);
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
		float currentTargetX;
		float currentTargetY;
		if (this.path.isEmpty()) {
			currentTargetX = this.target.x;
			currentTargetY = this.target.y;
		}
		else {
			final Point2D.Float nextPathElement = this.path.get(0);
			currentTargetX = nextPathElement.x;
			currentTargetY = nextPathElement.y;
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
		final float propulsionWindow = simulation.getUnitData().getPropulsionWindow(this.unit.getTypeId());
		final float turnRate = simulation.getUnitData().getTurnRate(this.unit.getTypeId());
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
		if (absDelta < propulsionWindow) {
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
				if (pathingGrid.isPathable(nextX, nextY, movementType, collisionSize)// ((int) collisionSize / 16) * 16
						&& !worldCollision.intersectsAnythingOtherThan(tempRect, this.unit, movementType)) {
					this.unit.setPoint(nextX, nextY, worldCollision);
					if (done) {
						if (this.path.isEmpty()) {
							return true;
						}
						else {
							final Float removed = this.path.remove(0);
							System.out.println(
									"We think we reached  " + removed + " because are at " + nextX + "," + nextY);
							if (this.path.isEmpty()) {
								currentTargetX = this.target.x;
								currentTargetY = this.target.y;
							}
							else {
								final Point2D.Float firstPathElement = this.path.get(0);
								currentTargetX = firstPathElement.x;
								currentTargetY = firstPathElement.y;
							}
							deltaY = currentTargetY - prevY;
							deltaX = currentTargetX - prevX;
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
								this.wasWithinPropWindow = false;
								return false;
							}
						}
					}
				}
				else {
					this.path = simulation.findNaiveSlowPath(this.unit, startFloatingX, startFloatingY, this.target,
							movementType, collisionSize);
					System.out.println("new path " + this.path);
					if (this.path.isEmpty()) {
						return true;
					}
					else {
						this.path.add(this.target);
					}
				}
				this.wasWithinPropWindow = true;
			}
			while (continueDistance > 0);
		}
		else {
			// If this happens, the unit is facing the wrong way, and has to turn before
			// moving.
			this.wasWithinPropWindow = false;
		}

		return false;
	}

	@Override
	public int getOrderId() {
		return CAbilityMove.ORDER_ID;
	}

	@Override
	public AnimationTokens.PrimaryTag getAnimationName() {
		if (!this.wasWithinPropWindow) {
			return AnimationTokens.PrimaryTag.STAND;
		}
		return AnimationTokens.PrimaryTag.WALK;
	}

}

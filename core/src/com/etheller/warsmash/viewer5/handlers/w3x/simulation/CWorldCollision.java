package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.Quadtree;
import com.etheller.warsmash.util.QuadtreeIntersector;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;

public class CWorldCollision {
	private final Quadtree<CUnit> groundUnitCollision;
	private final Quadtree<CUnit> airUnitCollision;
	private final Quadtree<CUnit> seaUnitCollision;
	private final Quadtree<CUnit> buildingUnitCollision;
	private final float maxCollisionRadius;
	private final AnyUnitExceptTwoIntersector anyUnitExceptTwoIntersector;
	private final EachUnitOnlyOnceIntersector eachUnitOnlyOnceIntersector;

	public CWorldCollision(final Rectangle entireMapBounds, final float maxCollisionRadius) {
		this.groundUnitCollision = new Quadtree<>(entireMapBounds);
		this.airUnitCollision = new Quadtree<>(entireMapBounds);
		this.seaUnitCollision = new Quadtree<>(entireMapBounds);
		this.buildingUnitCollision = new Quadtree<>(entireMapBounds);
		this.maxCollisionRadius = maxCollisionRadius;
		this.anyUnitExceptTwoIntersector = new AnyUnitExceptTwoIntersector();
		this.eachUnitOnlyOnceIntersector = new EachUnitOnlyOnceIntersector();
	}

	public void addUnit(final CUnit unit) {
		Rectangle bounds = unit.getCollisionRectangle();
		if (bounds == null) {
			final float collisionSize = Math.min(this.maxCollisionRadius, unit.getUnitType().getCollisionSize());
			bounds = new Rectangle(unit.getX() - collisionSize, unit.getY() - collisionSize, collisionSize * 2,
					collisionSize * 2);
			unit.setCollisionRectangle(bounds);
		}
		if (unit.getUnitType().isBuilding()) {
			// buildings are here so that we can include them when enumerating all units in
			// a rect, but they don't really move dynamically, this is kind of pointless
			this.buildingUnitCollision.add(unit, bounds);
		}
		else {
			final MovementType movementType = unit.getUnitType().getMovementType();
			if (movementType != null) {
				switch (movementType) {
				case AMPHIBIOUS:
					this.seaUnitCollision.add(unit, bounds);
					this.groundUnitCollision.add(unit, bounds);
					break;
				case FLOAT:
					this.seaUnitCollision.add(unit, bounds);
					break;
				case FLY:
					this.airUnitCollision.add(unit, bounds);
					break;
				default:
				case DISABLED:
				case FOOT:
				case HORSE:
				case HOVER:
					this.groundUnitCollision.add(unit, bounds);
					break;
				}
			}
		}
	}

	public void removeUnit(final CUnit unit) {
		final Rectangle bounds = unit.getCollisionRectangle();
		if (bounds != null) {
			if (unit.getUnitType().isBuilding()) {
				this.buildingUnitCollision.remove(unit, bounds);
			}
			else {
				final MovementType movementType = unit.getUnitType().getMovementType();
				if (movementType != null) {
					switch (movementType) {
					case AMPHIBIOUS:
						this.seaUnitCollision.remove(unit, bounds);
						this.groundUnitCollision.remove(unit, bounds);
						break;
					case FLOAT:
						this.seaUnitCollision.remove(unit, bounds);
						break;
					case FLY:
						this.airUnitCollision.remove(unit, bounds);
						break;
					default:
					case DISABLED:
					case FOOT:
					case HORSE:
					case HOVER:
						this.groundUnitCollision.remove(unit, bounds);
						break;
					}
				}
			}
		}
		unit.setCollisionRectangle(null);
	}

	public void enumUnitsInRect(final Rectangle rect, final CUnitEnumFunction callback) {
		this.eachUnitOnlyOnceIntersector.reset(callback);
		this.groundUnitCollision.intersect(rect, this.eachUnitOnlyOnceIntersector);
		this.airUnitCollision.intersect(rect, this.eachUnitOnlyOnceIntersector);
		this.seaUnitCollision.intersect(rect, this.eachUnitOnlyOnceIntersector);
		this.buildingUnitCollision.intersect(rect, this.eachUnitOnlyOnceIntersector);
	}

	public boolean intersectsAnythingOtherThan(final Rectangle newPossibleRectangle, final CUnit sourceUnitToIgnore,
			final MovementType movementType) {
		return intersectsAnythingOtherThan(newPossibleRectangle, sourceUnitToIgnore, null, movementType);
	}

	public boolean intersectsAnythingOtherThan(final Rectangle newPossibleRectangle, final CUnit sourceUnitToIgnore,
			final CUnit sourceSecondUnitToIgnore, final MovementType movementType) {
		if (movementType != null) {
			switch (movementType) {
			case AMPHIBIOUS:
				if (this.seaUnitCollision.intersect(newPossibleRectangle,
						this.anyUnitExceptTwoIntersector.reset(sourceUnitToIgnore, sourceSecondUnitToIgnore))) {
					return true;
				}
				if (this.groundUnitCollision.intersect(newPossibleRectangle,
						this.anyUnitExceptTwoIntersector.reset(sourceUnitToIgnore, sourceSecondUnitToIgnore))) {
					return true;
				}
				return false;
			case FLOAT:
				return this.seaUnitCollision.intersect(newPossibleRectangle,
						this.anyUnitExceptTwoIntersector.reset(sourceUnitToIgnore, sourceSecondUnitToIgnore));
			case FLY:
				return this.airUnitCollision.intersect(newPossibleRectangle,
						this.anyUnitExceptTwoIntersector.reset(sourceUnitToIgnore, sourceSecondUnitToIgnore));
			default:
			case DISABLED:
			case FOOT:
			case HORSE:
			case HOVER:
				return this.groundUnitCollision.intersect(newPossibleRectangle,
						this.anyUnitExceptTwoIntersector.reset(sourceUnitToIgnore, sourceSecondUnitToIgnore));
			}
		}
		return false;
	}

	public void translate(final CUnit unit, final float xShift, final float yShift) {
		if (unit.getUnitType().isBuilding()) {
			throw new IllegalArgumentException("Cannot add building to the CWorldCollision");
		}
		final MovementType movementType = unit.getUnitType().getMovementType();
		final Rectangle bounds = unit.getCollisionRectangle();
		if (movementType != null) {
			switch (movementType) {
			case AMPHIBIOUS:
				final float oldX = bounds.x;
				final float oldY = bounds.y;
				this.seaUnitCollision.translate(unit, bounds, xShift, yShift);
				bounds.x = oldX;
				bounds.y = oldY;
				this.groundUnitCollision.translate(unit, bounds, xShift, yShift);
				break;
			case FLOAT:
				this.seaUnitCollision.translate(unit, bounds, xShift, yShift);
				break;
			case FLY:
				this.airUnitCollision.translate(unit, bounds, xShift, yShift);
				break;
			default:
			case DISABLED:
			case FOOT:
			case HORSE:
			case HOVER:
				this.groundUnitCollision.translate(unit, bounds, xShift, yShift);
				break;
			}
		}
	}

	private static final class AnyUnitExceptTwoIntersector implements QuadtreeIntersector<CUnit> {
		private CUnit firstUnit;
		private CUnit secondUnit;

		public AnyUnitExceptTwoIntersector reset(final CUnit firstUnit, final CUnit secondUnit) {
			this.firstUnit = firstUnit;
			this.secondUnit = secondUnit;
			return this;
		}

		@Override
		public boolean onIntersect(final CUnit intersectingObject) {
			return (intersectingObject != this.firstUnit) && (intersectingObject != this.secondUnit);
		}
	}

	private static final class EachUnitOnlyOnceIntersector implements QuadtreeIntersector<CUnit> {
		private CUnitEnumFunction consumerDelegate;
		private final Set<CUnit> intersectedUnits = new HashSet<>();
		private boolean done;

		public EachUnitOnlyOnceIntersector reset(final CUnitEnumFunction consumerDelegate) {
			this.consumerDelegate = consumerDelegate;
			this.intersectedUnits.clear();
			this.done = false;
			return this;
		}

		@Override
		public boolean onIntersect(final CUnit intersectingObject) {
			if (this.done) {
				return true;
			}
			if (this.intersectedUnits.add(intersectingObject)) {
				this.done = this.consumerDelegate.call(intersectingObject);
				return this.done;
			}
			return false;
		}
	}
}

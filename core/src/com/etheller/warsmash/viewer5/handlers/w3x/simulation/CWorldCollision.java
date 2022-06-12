package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.Quadtree;
import com.etheller.warsmash.util.QuadtreeIntersector;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;

public class CWorldCollision {
	private static final float MINIMUM_COLLISION_SIZE = 0.001f /* THIS IS TO STOP QUADTREE FROM BUSTING */;
	private final Quadtree<CUnit> groundUnitCollision;
	private final Quadtree<CUnit> airUnitCollision;
	private final Quadtree<CUnit> seaUnitCollision;
	private final Quadtree<CUnit> buildingUnitCollision;
	private final Quadtree<CDestructable> destructablesForEnum;
	private final float maxCollisionRadius;
	private final AnyUnitExceptTwoIntersector anyUnitExceptTwoIntersector;
	private final EachUnitOnlyOnceIntersector eachUnitOnlyOnceIntersector;
	private final DestructableEnumIntersector destructableEnumIntersector;

	public CWorldCollision(final Rectangle entireMapBounds, final float maxCollisionRadius) {
		groundUnitCollision = new Quadtree<>(entireMapBounds);
		airUnitCollision = new Quadtree<>(entireMapBounds);
		seaUnitCollision = new Quadtree<>(entireMapBounds);
		buildingUnitCollision = new Quadtree<>(entireMapBounds);
		destructablesForEnum = new Quadtree<>(entireMapBounds);
		anyUnitExceptTwoIntersector = new AnyUnitExceptTwoIntersector();
		eachUnitOnlyOnceIntersector = new EachUnitOnlyOnceIntersector();
		destructableEnumIntersector = new DestructableEnumIntersector();
		this.maxCollisionRadius = maxCollisionRadius;
	}

	public void addUnit(final CUnit unit) {
		Rectangle bounds = unit.getCollisionRectangle();
		if (bounds == null) {
			final float collisionSize = Math.max(MINIMUM_COLLISION_SIZE,
					Math.min(maxCollisionRadius, unit.getUnitType().getCollisionSize()));
			bounds = new Rectangle(unit.getX() - collisionSize, unit.getY() - collisionSize, collisionSize * 2,
					collisionSize * 2);
			unit.setCollisionRectangle(bounds);
		}
		if (unit.isBuilding()) {
			// buildings are here so that we can include them when enumerating all units in
			// a rect, but they don't really move dynamically, this is kind of pointless
			buildingUnitCollision.add(unit, bounds);
		}
		else {
			final MovementType movementType = unit.getUnitType().getMovementType();
			if (movementType != null) {
				switch (movementType) {
				case AMPHIBIOUS:
					seaUnitCollision.add(unit, bounds);
					groundUnitCollision.add(unit, bounds);
					break;
				case FLOAT:
					seaUnitCollision.add(unit, bounds);
					break;
				case FLY:
					airUnitCollision.add(unit, bounds);
					break;
				case DISABLED:
					break;
				case FOOT:
				case FOOT_NO_COLLISION:
				case HORSE:
				case HOVER:
				default:
					groundUnitCollision.add(unit, bounds);
					break;
				}
			}
		}
	}

	public void addDestructable(final CDestructable dest) {
		final Rectangle bounds = dest.getOrCreateRegisteredEnumRectangle();
		destructablesForEnum.add(dest, bounds);
	}

	public void removeDestructable(final CDestructable dest) {
		final Rectangle bounds = dest.getOrCreateRegisteredEnumRectangle();
		destructablesForEnum.remove(dest, bounds);
	}

	public void removeUnit(final CUnit unit) {
		final Rectangle bounds = unit.getCollisionRectangle();
		if (bounds != null) {
			if (unit.isBuilding()) {
				buildingUnitCollision.remove(unit, bounds);
			}
			else {
				final MovementType movementType = unit.getUnitType().getMovementType();
				if (movementType != null) {
					switch (movementType) {
					case AMPHIBIOUS:
						seaUnitCollision.remove(unit, bounds);
						groundUnitCollision.remove(unit, bounds);
						break;
					case FLOAT:
						seaUnitCollision.remove(unit, bounds);
						break;
					case FLY:
						airUnitCollision.remove(unit, bounds);
						break;
					case DISABLED:
						break;
					case FOOT:
					case FOOT_NO_COLLISION:
					case HORSE:
					case HOVER:
					default:
						groundUnitCollision.remove(unit, bounds);
						break;
					}
				}
			}
		}
		unit.setCollisionRectangle(null);
	}

	public void enumUnitsInRect(final Rectangle rect, final CUnitEnumFunction callback) {
		eachUnitOnlyOnceIntersector.reset(callback);
		groundUnitCollision.intersect(rect, eachUnitOnlyOnceIntersector);
		airUnitCollision.intersect(rect, eachUnitOnlyOnceIntersector);
		seaUnitCollision.intersect(rect, eachUnitOnlyOnceIntersector);
		buildingUnitCollision.intersect(rect, eachUnitOnlyOnceIntersector);
	}

	public void enumBuildingsInRect(final Rectangle rect, final QuadtreeIntersector<CUnit> callback) {
		buildingUnitCollision.intersect(rect, callback);
	}

	public void enumBuildingsAtPoint(final float x, final float y, final QuadtreeIntersector<CUnit> callback) {
		buildingUnitCollision.intersect(x, y, callback);
	}

	public void enumDestructablesInRect(final Rectangle rect, final CDestructableEnumFunction callback) {
		destructablesForEnum.intersect(rect, destructableEnumIntersector.reset(callback));
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
				if (seaUnitCollision.intersect(newPossibleRectangle,
						anyUnitExceptTwoIntersector.reset(sourceUnitToIgnore, sourceSecondUnitToIgnore))) {
					return true;
				}
				return groundUnitCollision.intersect(newPossibleRectangle,
						anyUnitExceptTwoIntersector.reset(sourceUnitToIgnore, sourceSecondUnitToIgnore));
			case FLOAT:
				return seaUnitCollision.intersect(newPossibleRectangle,
						anyUnitExceptTwoIntersector.reset(sourceUnitToIgnore, sourceSecondUnitToIgnore));
			case FLY:
				return airUnitCollision.intersect(newPossibleRectangle,
						anyUnitExceptTwoIntersector.reset(sourceUnitToIgnore, sourceSecondUnitToIgnore));
			case DISABLED:
			case FOOT_NO_COLLISION:
				return false;
			case FOOT:
			case HORSE:
			case HOVER:
			default:
				return groundUnitCollision.intersect(newPossibleRectangle,
						anyUnitExceptTwoIntersector.reset(sourceUnitToIgnore, sourceSecondUnitToIgnore));
			}
		}
		return false;
	}

	public void translate(final CUnit unit, final float xShift, final float yShift) {
		assert !unit.isBuilding() : "Cannot add building to the CWorldCollision";
		final MovementType movementType = unit.getUnitType().getMovementType();
		final Rectangle bounds = unit.getCollisionRectangle();
		if (movementType != null) {
			switch (movementType) {
			case AMPHIBIOUS:
				final float oldX = bounds.x;
				final float oldY = bounds.y;
				seaUnitCollision.translate(unit, bounds, xShift, yShift);
				bounds.x = oldX;
				bounds.y = oldY;
				groundUnitCollision.translate(unit, bounds, xShift, yShift);
				break;
			case FLOAT:
				seaUnitCollision.translate(unit, bounds, xShift, yShift);
				break;
			case FLY:
				airUnitCollision.translate(unit, bounds, xShift, yShift);
				break;
			case DISABLED:
				break;
			case FOOT:
			case FOOT_NO_COLLISION:
			case HORSE:
			case HOVER:
			default:
				groundUnitCollision.translate(unit, bounds, xShift, yShift);
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
			if (intersectingObject.isHidden()) {
				return false;
			}
			return (!intersectingObject.equals(firstUnit)) && (!intersectingObject.equals(secondUnit));
		}
	}

	private static final class EachUnitOnlyOnceIntersector implements QuadtreeIntersector<CUnit> {
		private CUnitEnumFunction consumerDelegate;
		private final Set<CUnit> intersectedUnits = new HashSet<>();
		private boolean done;

		public EachUnitOnlyOnceIntersector reset(final CUnitEnumFunction consumerDelegate) {
			this.consumerDelegate = consumerDelegate;
			intersectedUnits.clear();
			done = false;
			return this;
		}

		@Override
		public boolean onIntersect(final CUnit intersectingObject) {
			if (intersectingObject.isHidden()) {
				return false;
			}
			if (done) {
				// This check is because we may use the intersector for multiple intersect
				// calls, see "enumUnitsInRect" and how it uses this intersector first on the
				// ground unit layer, then the flying unit layer, without recycling
				return true;
			}
			if (intersectedUnits.add(intersectingObject)) {
				done = consumerDelegate.call(intersectingObject);
				return done;
			}
			return false;
		}
	}

	private static final class DestructableEnumIntersector implements QuadtreeIntersector<CDestructable> {
		private CDestructableEnumFunction consumerDelegate;

		public DestructableEnumIntersector reset(final CDestructableEnumFunction consumerDelegate) {
			this.consumerDelegate = consumerDelegate;
			return this;
		}

		@Override
		public boolean onIntersect(final CDestructable intersectingObject) {
//			if (intersectingObject.isHidden()) { // at time of writing CDestructable did not have isHidden(), uncomment when available
//				return false;
//			}
			return consumerDelegate.call(intersectingObject);
		}
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.Quadtree;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;

public class CWorldCollision {
	private final Quadtree<CUnit> groundUnitCollision;
	private final Quadtree<CUnit> airUnitCollision;
	private final Quadtree<CUnit> seaUnitCollision;

	public CWorldCollision(final Rectangle entireMapBounds) {
		this.groundUnitCollision = new Quadtree<>(entireMapBounds);
		this.airUnitCollision = new Quadtree<>(entireMapBounds);
		this.seaUnitCollision = new Quadtree<>(entireMapBounds);
	}

	public void addUnit(final CUnit unit) {
		if (unit.getUnitType().isBuilding()) {
			throw new IllegalArgumentException("Cannot add building to the CWorldCollision");
		}
		Rectangle bounds = unit.getCollisionRectangle();
		if (bounds == null) {
			final float collisionSize = unit.getUnitType().getCollisionSize();
			bounds = new Rectangle(unit.getX() - collisionSize, unit.getY() - collisionSize, collisionSize * 2,
					collisionSize * 2);
			unit.setCollisionRectangle(bounds);
		}
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

	public boolean intersectsAnythingOtherThan(final Rectangle newPossibleRectangle, final CUnit sourceUnitToIgnore,
			final MovementType movementType) {
		if (movementType != null) {
			switch (movementType) {
			case AMPHIBIOUS:
				if (this.seaUnitCollision.intersectsAnythingOtherThan(sourceUnitToIgnore, newPossibleRectangle)) {
					return true;
				}
				if (this.groundUnitCollision.intersectsAnythingOtherThan(sourceUnitToIgnore, newPossibleRectangle)) {
					return true;
				}
				return false;
			case FLOAT:
				return this.seaUnitCollision.intersectsAnythingOtherThan(sourceUnitToIgnore, newPossibleRectangle);
			case FLY:
				return this.airUnitCollision.intersectsAnythingOtherThan(sourceUnitToIgnore, newPossibleRectangle);
			default:
			case DISABLED:
			case FOOT:
			case HORSE:
			case HOVER:
				return this.groundUnitCollision.intersectsAnythingOtherThan(sourceUnitToIgnore, newPossibleRectangle);
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
}

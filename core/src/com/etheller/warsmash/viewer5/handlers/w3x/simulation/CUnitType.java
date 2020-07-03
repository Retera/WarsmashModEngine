package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;

/**
 * The quick (symbol table instead of map) lookup for unit type values that we
 * probably cannot change per unit instance.
 */
public class CUnitType {
	private final boolean building;
	private final PathingGrid.MovementType movementType;
	private final float defaultFlyingHeight;
	private final float collisionSize;

	public CUnitType(final boolean isBldg, final MovementType movementType, final float defaultFlyingHeight,
			final float collisionSize) {
		this.building = isBldg;
		this.movementType = movementType;
		this.defaultFlyingHeight = defaultFlyingHeight;
		this.collisionSize = collisionSize;
	}

	public float getDefaultFlyingHeight() {
		return this.defaultFlyingHeight;
	}

	public PathingGrid.MovementType getMovementType() {
		return this.movementType;
	}

	public float getCollisionSize() {
		return this.collisionSize;
	}

	public boolean isBuilding() {
		return this.building;
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CAbilityData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CPathfindingProcessor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ProjectileCreator;

public class CSimulation {
	private final CUnitData unitData;
	private final CAbilityData abilityData;
	private final List<CUnit> units;
	private final List<CAttackProjectile> projectiles;
	private final HandleIdAllocator handleIdAllocator;
	private final ProjectileCreator projectileCreator;
	private int gameTurnTick = 0;
	private final PathingGrid pathingGrid;
	private final CPathfindingProcessor pathfindingProcessor;

	public CSimulation(final MutableObjectData parsedUnitData, final MutableObjectData parsedAbilityData,
			final ProjectileCreator projectileCreator, final PathingGrid pathingGrid) {
		this.projectileCreator = projectileCreator;
		this.pathingGrid = pathingGrid;
		this.pathfindingProcessor = new CPathfindingProcessor(pathingGrid);
		this.unitData = new CUnitData(parsedUnitData);
		this.abilityData = new CAbilityData(parsedAbilityData);
		this.units = new ArrayList<>();
		this.projectiles = new ArrayList<>();
		this.handleIdAllocator = new HandleIdAllocator();
	}

	public CUnitData getUnitData() {
		return this.unitData;
	}

	public CAbilityData getAbilityData() {
		return this.abilityData;
	}

	public List<CUnit> getUnits() {
		return this.units;
	}

	public CUnit createUnit(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing) {
		final CUnit unit = this.unitData.create(this, this.handleIdAllocator.createId(), playerIndex, typeId, x, y,
				facing);
		this.units.add(unit);
		return unit;
	}

	public CAttackProjectile createProjectile(final CUnit source, final int attackIndex, final CWidget target) {
		final CAttackProjectile projectile = this.projectileCreator.create(this, source, attackIndex, target);
		this.projectiles.add(projectile);
		return projectile;
	}

	public PathingGrid getPathingGrid() {
		return this.pathingGrid;
	}

	public List<Point> findNaiveSlowPath(final int startX, final int startY, final int goalX, final int goalY,
			final PathingGrid.MovementType movementType, final float collisionSize) {
		return this.pathfindingProcessor.findNaiveSlowPath(startX, startY, goalX, goalY, movementType, collisionSize);
	}

	public void update() {
		for (final CUnit unit : this.units) {
			unit.update(this);
		}
		final Iterator<CAttackProjectile> projectileIterator = this.projectiles.iterator();
		while (projectileIterator.hasNext()) {
			final CAttackProjectile projectile = projectileIterator.next();
			if (projectile.update(this)) {
				projectileIterator.remove();
			}
		}
		this.gameTurnTick++;
	}

	public int getGameTurnTick() {
		return this.gameTurnTick;
	}
}

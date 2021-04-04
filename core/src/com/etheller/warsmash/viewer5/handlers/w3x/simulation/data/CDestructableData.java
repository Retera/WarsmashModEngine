package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.HandleIdAllocator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;

public class CDestructableData {
	private static final War3ID NAME = War3ID.fromString("bnam");
	private static final War3ID HIT_POINT_MAXIMUM = War3ID.fromString("bhps");
	private static final War3ID TARGETED_AS = War3ID.fromString("btar");
	private static final War3ID ARMOR_TYPE = War3ID.fromString("barm");

	private static final War3ID BUILD_TIME = War3ID.fromString("bbut");
	private static final War3ID REPAIR_TIME = War3ID.fromString("bret");
	private static final War3ID GOLD_REPAIR = War3ID.fromString("breg");
	private static final War3ID LUMBER_REPAIR = War3ID.fromString("brel");

	private final MutableObjectData unitData;
	private final Map<War3ID, CDestructableType> unitIdToUnitType = new HashMap<>();
	private final SimulationRenderController simulationRenderController;

	public CDestructableData(final MutableObjectData unitData,
			final SimulationRenderController simulationRenderController) {
		this.unitData = unitData;
		this.simulationRenderController = simulationRenderController;
	}

	public CDestructable create(final CSimulation simulation, final War3ID typeId, final float x, final float y,
			final HandleIdAllocator handleIdAllocator, final RemovablePathingMapInstance pathingInstance,
			final RemovablePathingMapInstance pathingInstanceDeath) {
		final MutableGameObject unitType = this.unitData.get(typeId);
		final int handleId = handleIdAllocator.createId();

		final CDestructableType unitTypeInstance = getUnitTypeInstance(typeId, unitType);

		final float life = unitTypeInstance.getLife();

		final CDestructable destructable = new CDestructable(handleId, x, y, life, unitTypeInstance, pathingInstance,
				pathingInstanceDeath);
		return destructable;
	}

	private CDestructableType getUnitTypeInstance(final War3ID typeId, final MutableGameObject unitType) {
		CDestructableType unitTypeInstance = this.unitIdToUnitType.get(typeId);
		if (unitTypeInstance == null) {
			final BufferedImage buildingPathingPixelMap = this.simulationRenderController
					.getDestructablePathingPixelMap(typeId);
			final BufferedImage buildingPathingDeathPixelMap = this.simulationRenderController
					.getDestructablePathingDeathPixelMap(typeId);
			final String name = unitType.getFieldAsString(NAME, 0);
			final float life = unitType.getFieldAsFloat(HIT_POINT_MAXIMUM, 0);
			final EnumSet<CTargetType> targetedAs = CTargetType
					.parseTargetTypeSet(unitType.getFieldAsString(TARGETED_AS, 0));
			final String armorType = unitType.getFieldAsString(ARMOR_TYPE, 0);
			final int buildTime = unitType.getFieldAsInteger(BUILD_TIME, 0);

			unitTypeInstance = new CDestructableType(name, life, targetedAs, armorType, buildTime,
					buildingPathingPixelMap, buildingPathingDeathPixelMap);
			this.unitIdToUnitType.put(typeId, unitTypeInstance);
		}
		return unitTypeInstance;
	}

	public CDestructableType getUnitType(final War3ID rawcode) {
		final CDestructableType unitTypeInstance = this.unitIdToUnitType.get(rawcode);
		if (unitTypeInstance != null) {
			return unitTypeInstance;
		}
		final MutableGameObject unitType = this.unitData.get(rawcode);
		if (unitType == null) {
			return null;
		}
		return getUnitTypeInstance(rawcode, unitType);
	}
}

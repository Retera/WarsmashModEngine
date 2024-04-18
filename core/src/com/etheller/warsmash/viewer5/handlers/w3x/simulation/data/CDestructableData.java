package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.HandleIdAllocator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;

public class CDestructableData {
	private static final String NAME = "Name"; // replaced from 'bnam'
	private static final String HIT_POINT_MAXIMUM = "HP"; // replaced from 'bhps'
	private static final String TARGETED_AS = "targType"; // replaced from 'btar'
	private static final String ARMOR_TYPE = "armor"; // replaced from 'barm'

	private static final String BUILD_TIME = "buildTime"; // replaced from 'bbut'
	private static final String REPAIR_TIME = "repairTime"; // replaced from 'bret'
	private static final String GOLD_REPAIR = "goldRep"; // replaced from 'breg'
	private static final String LUMBER_REPAIR = "lumberRep"; // replaced from 'brel'

	private static final String OCCLUSION_HEIGHT = "occH";

	private final ObjectData unitData;
	private final Map<War3ID, CDestructableType> unitIdToUnitType = new HashMap<>();
	private final SimulationRenderController simulationRenderController;

	public CDestructableData(final ObjectData unitData, final SimulationRenderController simulationRenderController) {
		this.unitData = unitData;
		this.simulationRenderController = simulationRenderController;
	}

	public CDestructable create(final CSimulation simulation, final War3ID typeId, final float x, final float y,
			final HandleIdAllocator handleIdAllocator, final RemovablePathingMapInstance pathingInstance,
			final RemovablePathingMapInstance pathingInstanceDeath) {
		final GameObject unitType = this.unitData.get(typeId);
		final int handleId = handleIdAllocator.createId();

		final CDestructableType unitTypeInstance = getUnitTypeInstance(typeId, unitType);

		final float life = unitTypeInstance.getMaxLife();

		final CDestructable destructable = new CDestructable(handleId, x, y, life, unitTypeInstance, pathingInstance,
				pathingInstanceDeath);
		return destructable;
	}

	private CDestructableType getUnitTypeInstance(final War3ID typeId, final GameObject unitType) {
		CDestructableType unitTypeInstance = this.unitIdToUnitType.get(typeId);
		if (unitTypeInstance == null) {
			final BufferedImage buildingPathingPixelMap = this.simulationRenderController
					.getDestructablePathingPixelMap(typeId);
			final BufferedImage buildingPathingDeathPixelMap = this.simulationRenderController
					.getDestructablePathingDeathPixelMap(typeId);
			final String name = unitType.getFieldAsString(NAME, 0);
			final float life = unitType.getFieldAsFloat(HIT_POINT_MAXIMUM, 0);
			final EnumSet<CTargetType> targetedAs = CTargetType
					.parseTargetTypeSet(unitType.getFieldAsList(TARGETED_AS));
			final String armorType = unitType.getFieldAsString(ARMOR_TYPE, 0);
			final int buildTime = unitType.getFieldAsInteger(BUILD_TIME, 0);
			final int repairTime = unitType.getFieldAsInteger(REPAIR_TIME, 0);
			final int goldRepairCost = unitType.getFieldAsInteger(GOLD_REPAIR, 0);
			final int lumberRepairCost = unitType.getFieldAsInteger(LUMBER_REPAIR, 0);
			final float occlusionHeight = unitType.getFieldAsFloat(OCCLUSION_HEIGHT, 0);

			unitTypeInstance = new CDestructableType(name, life, targetedAs, armorType, buildTime, goldRepairCost,
					lumberRepairCost, repairTime, occlusionHeight, buildingPathingPixelMap,
					buildingPathingDeathPixelMap);
			this.unitIdToUnitType.put(typeId, unitTypeInstance);
		}
		return unitTypeInstance;
	}

	public CDestructableType getUnitType(final War3ID rawcode) {
		final CDestructableType unitTypeInstance = this.unitIdToUnitType.get(rawcode);
		if (unitTypeInstance != null) {
			return unitTypeInstance;
		}
		final GameObject unitType = this.unitData.get(rawcode.asStringValue());
		if (unitType == null) {
			return null;
		}
		return getUnitTypeInstance(rawcode, unitType);
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CAbilityData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;

public class CSimulation {
	private final CUnitData unitData;
	private final CAbilityData abilityData;
	private final List<CUnit> units;
	private final HandleIdAllocator handleIdAllocator;

	public CSimulation(final MutableObjectData parsedUnitData, final MutableObjectData parsedAbilityData) {
		this.unitData = new CUnitData(parsedUnitData);
		this.abilityData = new CAbilityData(parsedAbilityData);
		this.units = new ArrayList<>();
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

	public CUnit createUnit(final War3ID typeId, final float x, final float y, final float facing) {
		final CUnit unit = this.unitData.create(this, this.handleIdAllocator.createId(), typeId, x, y, facing);
		this.units.add(unit);
		return unit;
	}

	public void update() {
		for (final CUnit unit : this.units) {
			unit.update(this);
		}
	}
}

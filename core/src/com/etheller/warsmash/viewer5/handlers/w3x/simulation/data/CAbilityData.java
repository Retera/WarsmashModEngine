package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGeneric;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityData {
	private static final War3ID TARGETS_ALLOWED = War3ID.fromString("atar");
	private static final War3ID LEVELS = War3ID.fromString("alev");

	private static final War3ID COLD_ARROWS = War3ID.fromString("ACcw");
	private final MutableObjectData abilityData;
	private Map<War3ID, CAbilityType> aliasToAbilityType = new HashMap<>();

	public CAbilityData(final MutableObjectData abilityData) {
		this.abilityData = abilityData;
		this.aliasToAbilityType = new HashMap<>();
	}

	public CAbilityType getAbilityType(final War3ID alias) {
		CAbilityType abilityType = this.aliasToAbilityType.get(alias);
		if (abilityType == null) {
			final MutableGameObject mutableGameObject = this.abilityData.get(alias);
			final int levels = mutableGameObject.getFieldAsInteger(LEVELS, 0);
			final List<CAbilityTypeLevelData> levelData = new ArrayList<>();
			for (int level = 0; level < levels; level++) {
				final String targetsAllowedAtLevelString = mutableGameObject.getFieldAsString(TARGETS_ALLOWED, level);
				final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType
						.parseTargetTypeSet(targetsAllowedAtLevelString);
				levelData.add(new CAbilityTypeLevelData(targetsAllowedAtLevel));
			}
			abilityType = new CAbilityType(alias, mutableGameObject.getCode(), levelData);
		}
		return abilityType;
	}

	public CAbility createAbility(final String ability, final int handleId) {
		final War3ID war3Id = War3ID.fromString(ability);
		if (war3Id.equals(COLD_ARROWS)) {
			return new CAbilityColdArrows(war3Id, handleId);
		}
		return new CAbilityGeneric(war3Id, handleId);
	}
}

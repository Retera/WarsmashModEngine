package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeColdArrowsLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionColdArrows extends AbstractCAbilityTypeDefinition<CAbilityTypeColdArrowsLevelData> {

	@Override
	protected CAbilityTypeColdArrowsLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		return new CAbilityTypeColdArrowsLevelData(
				CTargetType.parseTargetTypeSet(abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level)));
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeColdArrowsLevelData> levelData) {
		return new CAbilityTypeColdArrows(alias, abilityEditorData.getCode(), levelData);
	}

}

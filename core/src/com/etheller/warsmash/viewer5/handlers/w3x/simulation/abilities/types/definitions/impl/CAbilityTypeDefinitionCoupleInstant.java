package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeCoupleInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeCoupleInstantLevelData;

public class CAbilityTypeDefinitionCoupleInstant
		extends AbstractCAbilityTypeDefinition<CAbilityTypeCoupleInstantLevelData> implements CAbilityTypeDefinition {
	protected static final War3ID RESULTING_UNIT_TYPE = War3ID.fromString("coau");
	protected static final War3ID PARTNER_UNIT_TYPE = War3ID.fromString("coa1");
	protected static final War3ID MOVE_TO_PARTNER = War3ID.fromString("coa2");
	protected static final War3ID GOLD_COST = War3ID.fromString("coa3");
	protected static final War3ID LUMBER_COST = War3ID.fromString("coa4");

	@Override
	protected CAbilityTypeCoupleInstantLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final War3ID resultingUnitTypeId = War3ID.fromString(abilityEditorData.getFieldAsString(UNIT_ID + level, 0));
		final War3ID partnerUnitTypeId = War3ID.fromString(abilityEditorData.getFieldAsString(DATA_A + level, 0));
		final boolean moveToPartner = abilityEditorData.getFieldAsBoolean(DATA_B + level, 0);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		final float area = abilityEditorData.getFieldAsFloat(AREA + level, 0);

//		final int goldCost = abilityEditorData.getFieldAsInteger(GOLD_COST, level);
//		final int lumberCost = abilityEditorData.getFieldAsInteger(LUMBER_COST, level);

		return new CAbilityTypeCoupleInstantLevelData(getTargetsAllowed(abilityEditorData, level), resultingUnitTypeId,
				partnerUnitTypeId, moveToPartner, castRange, area, 0, 0);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeCoupleInstantLevelData> levelData) {
		return new CAbilityTypeCoupleInstant(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeCoupleInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeCoupleInstantLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionCoupleInstant
		extends AbstractCAbilityTypeDefinition<CAbilityTypeCoupleInstantLevelData> implements CAbilityTypeDefinition {
	protected static final War3ID RESULTING_UNIT_TYPE = War3ID.fromString("coau");
	protected static final War3ID PARTNER_UNIT_TYPE = War3ID.fromString("coa1");
	protected static final War3ID MOVE_TO_PARTNER = War3ID.fromString("coa2");
	protected static final War3ID GOLD_COST = War3ID.fromString("coa3");
	protected static final War3ID LUMBER_COST = War3ID.fromString("coa4");

	@Override
	protected CAbilityTypeCoupleInstantLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final War3ID resultingUnitTypeId = War3ID
				.fromString(abilityEditorData.getFieldAsString(RESULTING_UNIT_TYPE, level));
		final War3ID partnerUnitTypeId = War3ID
				.fromString(abilityEditorData.getFieldAsString(PARTNER_UNIT_TYPE, level));
		final boolean moveToPartner = abilityEditorData.getFieldAsBoolean(MOVE_TO_PARTNER, level);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE, level);
		final float area = abilityEditorData.getFieldAsFloat(AREA, level);

//		final int goldCost = abilityEditorData.getFieldAsInteger(GOLD_COST, level);
//		final int lumberCost = abilityEditorData.getFieldAsInteger(LUMBER_COST, level);

		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeCoupleInstantLevelData(targetsAllowedAtLevel, resultingUnitTypeId, partnerUnitTypeId,
				moveToPartner, castRange, area, 0, 0);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeCoupleInstantLevelData> levelData) {
		return new CAbilityTypeCoupleInstant(alias, abilityEditorData.getCode(), levelData);
	}

}

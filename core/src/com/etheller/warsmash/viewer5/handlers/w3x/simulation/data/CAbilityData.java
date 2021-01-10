package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGeneric;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionGoldMine;

public class CAbilityData {

	private final MutableObjectData abilityData;
	private Map<War3ID, CAbilityType<?>> aliasToAbilityType = new HashMap<>();
	private final Map<War3ID, CAbilityTypeDefinition> codeToAbilityTypeDefinition = new HashMap<>();

	public CAbilityData(final MutableObjectData abilityData) {
		this.abilityData = abilityData;
		this.aliasToAbilityType = new HashMap<>();
	}

	private void registerCodes() {
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ACcw"), new CAbilityTypeDefinitionColdArrows());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Agld"), new CAbilityTypeDefinitionGoldMine());
	}

	public CAbilityType<?> getAbilityType(final War3ID alias) {
		CAbilityType<?> abilityType = this.aliasToAbilityType.get(alias);
		if (abilityType == null) {
			final MutableGameObject mutableGameObject = this.abilityData.get(alias);
			final War3ID code = mutableGameObject.getCode();
			final CAbilityTypeDefinition abilityTypeDefinition = this.codeToAbilityTypeDefinition.get(code);
			abilityType = abilityTypeDefinition.createAbilityType(alias, mutableGameObject);
		}
		return abilityType;
	}

	public CAbility createAbility(final String ability, final int handleId) {
		final War3ID war3Id = War3ID.fromString(ability);
		final CAbilityType<?> abilityType = getAbilityType(war3Id);
		if (abilityType != null) {
			return abilityType.createAbility(handleId);
		}
		return new CAbilityGeneric(war3Id, handleId);
	}
}

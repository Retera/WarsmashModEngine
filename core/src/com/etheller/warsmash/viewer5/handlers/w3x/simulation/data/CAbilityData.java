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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.*;

public class CAbilityData {

	private final MutableObjectData abilityData;
	private Map<War3ID, CAbilityType<?>> aliasToAbilityType = new HashMap<>();
	private final Map<War3ID, CAbilityTypeDefinition> codeToAbilityTypeDefinition = new HashMap<>();

	public CAbilityData(final MutableObjectData abilityData) {
		this.abilityData = abilityData;
		this.aliasToAbilityType = new HashMap<>();
		registerCodes();
	}

	private void registerCodes() {
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ACcw"), new CAbilityTypeDefinitionColdArrows());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Agld"), new CAbilityTypeDefinitionGoldMine());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Artn"), new CAbilityTypeDefinitionReturnResources());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Ahar"), new CAbilityTypeDefinitionHarvest());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Awha"), new CAbilityTypeDefinitionWispHarvest());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Ahrl"), new CAbilityTypeDefinitionHarvestLumber());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANcl"), new CAbilityTypeDefinitionChannelTest());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AInv"), new CAbilityTypeDefinitionInventory());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Arep"), new CAbilityTypeDefinitionHumanRepair());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Avul"), new CAbilityTypeDefinitionInvulnerable());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Acoi"), new CAbilityTypeDefinitionCoupleInstant());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIhe"), new CAbilityTypeDefinitionItemHeal());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIat"), new CAbilityTypeDefinitionItemAttackBonus());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIab"), new CAbilityTypeDefinitionItemStatBonus());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIim"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIsm"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIam"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIxm"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIde"), new CAbilityTypeDefinitionItemDefenseBonus());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AImi"), new CAbilityTypeDefinitionItemLifeBonus());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIml"), new CAbilityTypeDefinitionItemLifeBonus());
	}

	public CAbilityType<?> getAbilityType(final War3ID alias) {
		CAbilityType<?> abilityType = this.aliasToAbilityType.get(alias);
		if (abilityType == null) {
			final MutableGameObject mutableGameObject = this.abilityData.get(alias);
			if (mutableGameObject == null) {
				return null;
			}
			final War3ID code = War3ID.fromString(mutableGameObject.readSLKTag("code"));
			final CAbilityTypeDefinition abilityTypeDefinition = this.codeToAbilityTypeDefinition.get(code);
			if (abilityTypeDefinition != null) {
				abilityType = abilityTypeDefinition.createAbilityType(alias, mutableGameObject);
				this.aliasToAbilityType.put(alias, abilityType);
			}
		}
		return abilityType;
	}

	public CAbility createAbility(final String ability, final int handleId) {
		final War3ID war3Id = War3ID.fromString(ability.trim());
		final CAbilityType<?> abilityType = getAbilityType(war3Id);
		if (abilityType != null) {
			return abilityType.createAbility(handleId);
		}
		return new CAbilityGeneric(war3Id, handleId);
	}
}

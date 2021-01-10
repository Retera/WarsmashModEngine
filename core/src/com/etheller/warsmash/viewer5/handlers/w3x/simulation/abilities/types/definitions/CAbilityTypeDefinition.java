package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public interface CAbilityTypeDefinition {
	CAbilityType<?> createAbilityType(War3ID rawcode, MutableGameObject abilityEditorData);
}

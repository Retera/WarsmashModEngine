package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CAliasedLevelingAbility;

public interface CAbilitySpell extends CAliasedLevelingAbility {
	void populate(final MutableGameObject worldEditorAbility, final int level);
}

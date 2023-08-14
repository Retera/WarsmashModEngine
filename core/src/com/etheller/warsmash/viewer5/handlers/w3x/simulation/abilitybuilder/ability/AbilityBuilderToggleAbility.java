package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.SingleOrderAbility;

public interface AbilityBuilderToggleAbility extends SingleOrderAbility {
	public boolean isToggleOn();
	
	public void activate(final CSimulation game, final CUnit caster);
	
	public void deactivate(final CSimulation game, final CUnit caster);
}

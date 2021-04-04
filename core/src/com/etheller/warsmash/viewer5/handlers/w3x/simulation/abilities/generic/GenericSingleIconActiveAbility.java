package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;

public interface GenericSingleIconActiveAbility extends CAbility {
	War3ID getAlias();

	int getBaseOrderId();

	boolean isToggleOn();
}

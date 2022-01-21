package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;

public interface GenericSingleIconActiveAbility extends CLevelingAbility {
	War3ID getAlias();

	int getBaseOrderId();

	boolean isToggleOn();

	int getUIGoldCost();

	int getUILumberCost();

	int getUIManaCost();
}

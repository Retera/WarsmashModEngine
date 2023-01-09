package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;

public interface GenericSingleIconActiveAbility extends CLevelingAbility, SingleOrderAbility {
	War3ID getAlias();

	boolean isToggleOn();

	boolean isAutoCastOn();

	int getAutoCastOnOrderId();

	int getAutoCastOffOrderId();

	int getUIGoldCost();

	int getUILumberCost();

	int getUIManaCost();

	float getUIAreaOfEffect();

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;

public interface CLevelingAbility extends CAbility {
	int getLevel();

	void setLevel(int level);
}

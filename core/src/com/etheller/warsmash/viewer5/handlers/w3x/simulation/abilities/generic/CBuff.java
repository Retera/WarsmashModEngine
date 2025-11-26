package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;

public interface CBuff extends CAliasedLevelingAbility {
	float getDurationRemaining(CSimulation game, CUnit unit);

	float getDurationMax();

	boolean isTimedLifeBar();
	
	boolean isLeveled();
	
	boolean isPositive();
	
	boolean isDispellable();

	boolean isHero();

	boolean isPhysical();

	boolean isMagic();

	boolean isAura();

	String getVisibilityGroup();

	CAbility getSourceAbility();
	
	CUnit getSourceUnit();
	
}

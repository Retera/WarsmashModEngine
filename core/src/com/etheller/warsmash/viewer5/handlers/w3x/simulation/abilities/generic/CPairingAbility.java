package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import java.util.Set;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public interface CPairingAbility {

	// Method to search out partner unit
	Set<CUnit> findPairUnits(CSimulation game, CUnit caster);
	
	// Methods to identify partner unit(s)
	War3ID getPairAbilityCode(CSimulation game, CUnit caster);
	
	War3ID getPairUnitID(CSimulation game, CUnit caster);
	
	float getPairSearchRadius(CSimulation game, CUnit caster);

	// Methods to determine how targeting works
	boolean autoTargetParter(CSimulation game, CUnit caster);
	
	int maxPartners(CSimulation game, CUnit caster); // can only be one if autoTargetParter is false
	
	//Internal order to give to casting unit (generally used if auto targeting a partner. Not always needed)
	Integer getPairOrderId(CSimulation game, CUnit caster);

	Integer getPairOffOrderId(CSimulation game, CUnit caster);
	
	//Optional order to send to paired unit
	boolean orderPairedUnit(CSimulation game, CUnit caster);
	
	Integer orderPairedUnitOrderId(CSimulation game, CUnit caster);

	Integer orderPairedUnitOffOrderId(CSimulation game, CUnit caster);
	
	
}

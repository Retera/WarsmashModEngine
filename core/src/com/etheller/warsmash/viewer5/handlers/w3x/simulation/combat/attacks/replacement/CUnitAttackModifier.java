package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public interface CUnitAttackModifier {

	public int getPriority(CSimulation simulation, CUnit source, AbilityTarget target, CUnitAttack attack);

	public boolean checkPreLaunchApplication(CSimulation simulation, CUnit source, AbilityTarget target, CUnitAttack attack);

	public void applyPreLaunchModification(CSimulation simulation, CUnit unit, AbilityTarget target,
			CUnitAttack unitAttack, CUnitAttackSettings settings, CUnitPriorityLoopData loop);

	public boolean checkApplication(CSimulation simulation, CUnit source, AbilityTarget target, CUnitAttack attack);

	public void applyModification(CSimulation simulation, CUnit source, AbilityTarget target, CUnitAttack attack,
			CUnitAttackSettings settings, CUnitPriorityLoopData loop);

}

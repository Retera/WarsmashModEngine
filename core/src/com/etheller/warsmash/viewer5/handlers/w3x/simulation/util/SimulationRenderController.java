package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;

public interface SimulationRenderController {
	CAttackProjectile createAttackProjectile(CSimulation simulation, float launchX, float launchY, float launchFacing,
			CUnit source, CUnitAttackMissile attack, CWidget target, float damage, int bounceIndex);

	void createInstantAttackEffect(CSimulation cSimulation, CUnit source, CUnitAttackInstant attack, CWidget target);

	void spawnUnitDamageSound(CUnit damagedUnit, final String weaponSound, String armorType);

	void removeUnit(CUnit unit);
}

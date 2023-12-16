package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;

public class CUnitDefaultLifestealListener implements CUnitAttackPostDamageListener {
	private float amount;

	public CUnitDefaultLifestealListener(float amount) {
		super();
		this.amount = amount;
	}

	public void onHit(final CSimulation simulation, CUnit attacker, AbilityTarget target, float damage) {
		CUnit tarU = target.visit(AbilityTargetVisitor.UNIT);
		if (tarU != null && !tarU.isBuilding() && !tarU.isUnitType(CUnitTypeJass.MECHANICAL)
				&& !simulation.getPlayer(attacker.getPlayerIndex()).hasAlliance(tarU.getPlayerIndex(),
						CAllianceType.PASSIVE)
				&& (!simulation.getGameplayConstants().isMagicImmuneResistsLeech()
						|| !tarU.isUnitType(CUnitTypeJass.MAGIC_IMMUNE))) {
			attacker.heal(simulation, damage * this.amount);
		}
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float lifesteal) {
		this.amount = lifesteal;
	}
}

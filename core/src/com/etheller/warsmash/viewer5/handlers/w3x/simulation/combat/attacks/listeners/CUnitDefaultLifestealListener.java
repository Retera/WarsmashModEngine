package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitPriorityLoopData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;

public class CUnitDefaultLifestealListener implements CUnitAttackPostDamageListener {
	private float amount;

	public CUnitDefaultLifestealListener(float amount) {
		super();
		this.amount = amount;
	}

	@Override
	public void onHit(final CSimulation simulation, CUnit attacker, AbilityTarget target, CUnitAttack cUnitAttack, float damage, CUnitPriorityLoopData postListenerLoop) {
		CUnit tarU = target.visit(AbilityTargetVisitor.UNIT);
		if (tarU != null && !tarU.isBuilding() && !tarU.isUnitType(CUnitTypeJass.MECHANICAL)
				&& !simulation.getPlayer(attacker.getPlayerIndex()).hasAlliance(tarU.getPlayerIndex(),
						CAllianceType.PASSIVE)
				&& (!simulation.getGameplayConstants().isMagicImmuneResistsLeech()
						|| !tarU.isUnitType(CUnitTypeJass.MAGIC_IMMUNE))) {
			attacker.heal(simulation, damage * this.amount);
		}
	}
	
	@Override
	public int getPriority(CSimulation simulation, CUnit attacker, AbilityTarget target, CUnitAttack cUnitAttack) {
		return 0;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float lifesteal) {
		this.amount = lifesteal;
	}
}

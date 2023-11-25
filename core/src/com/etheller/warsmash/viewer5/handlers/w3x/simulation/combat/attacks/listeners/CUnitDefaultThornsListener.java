package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;

public class CUnitDefaultThornsListener implements CUnitAttackDamageTakenListener {

	private boolean percentage;
	private float amount;

	public CUnitDefaultThornsListener(boolean percentage, float amount) {
		this.percentage = percentage;
		this.amount = amount;
	}

	@Override
	public void onDamage(final CSimulation simulation, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged,
			CDamageType damageType, float damage, float bonusDamage, float trueDamage) {
		if (damageType == CDamageType.NORMAL && !isRanged && target.canBeTargetedBy(simulation, attacker, ENEMY_TARGET)
				&& (!simulation.getGameplayConstants().isMagicImmuneResistsThorns()
						|| !attacker.isUnitType(CUnitTypeJass.MAGIC_IMMUNE))) {
			float thornsAmount = amount;
			if (percentage) {
				thornsAmount *= damage;
			}
			attacker.damage(simulation, target, false, true, CAttackType.SPELLS, CDamageType.DEFENSIVE,
					CWeaponSoundTypeJass.WHOKNOWS.name(), thornsAmount);
		}
	}

	public boolean isPercentage() {
		return percentage;
	}

	public void setPercentage(boolean percentage) {
		this.percentage = percentage;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}
}

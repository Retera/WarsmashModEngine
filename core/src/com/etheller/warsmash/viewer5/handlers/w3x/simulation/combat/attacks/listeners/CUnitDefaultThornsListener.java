package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CSpellDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;

public class CUnitDefaultThornsListener implements CUnitAttackDamageTakenListener {
	private final static CDamageFlags DAMAGE_FLAGS = new CSpellDamageFlags();

	private boolean percentage;
	private float amount;

	public CUnitDefaultThornsListener(boolean percentage, float amount) {
		this.percentage = percentage;
		this.amount = amount;
	}

	@Override
	public int getPriority(CSimulation simulation, CUnit target, CDamageCalculation damage) {
		return 0;
	}

	@Override
	public void onDamage(CSimulation simulation, CUnit target, CDamageCalculation damage) {
		if (damage.getSource() != null && damage.getPrimaryDamageType() == CDamageType.NORMAL
				&& !damage.getPrimaryDamageFlags().isRanged()
				&& target.canBeTargetedBy(simulation, damage.getSource(), ENEMY_TARGET)
				&& damage.computeRawTotalDamage() > 0
				&& (!simulation.getGameplayConstants().isMagicImmuneResistsThorns()
						|| !damage.getSource().isUnitType(CUnitTypeJass.MAGIC_IMMUNE))) {
			float thornsAmount = amount;
			if (percentage) {
				thornsAmount *= damage.getPrimaryAmount();
				thornsAmount = Math.max(thornsAmount, 1);
			}
			damage.getSource().damage(simulation, target, DAMAGE_FLAGS, CAttackType.SPELLS, CDamageType.DEFENSIVE,
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

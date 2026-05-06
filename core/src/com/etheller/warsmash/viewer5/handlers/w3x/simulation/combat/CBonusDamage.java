package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;

public class CBonusDamage {
	protected final static int PREVENTMULTIPLY = 0b1;
	private float amount;
	private CAttackType attackType = null;
	private CDamageType damageType;
	private CDamageFlags damageFlags;
	private int internalFlags = 0b0;

	public float computeRawDamage(float multiplier) {
		return this.amount * (this.isPreventMultiply() ? 1 : multiplier);
	}

	public void applyMultiplier(float damageMultiplier) {
		this.amount *= damageMultiplier;
	}

	public float computeFinalDamage(CSimulation simulation, CUnit unit, CAttackType primaryAttackType, CDamageType primaryDamageType, float multiplier,
			float currentDamage) {
		if (isImmuneToDamage(simulation, unit, this.attackType == null ? primaryAttackType : this.attackType)
				|| (unit.isInvulnerable() && !this.isPassInvulnerable(simulation, unit))) {
			return 0;
		}
		float trueDamage = CDamageCalculation.calculateDamageByArmor(simulation, unit,
				this.attackType == null ? primaryAttackType : this.attackType, primaryDamageType, amount, damageFlags)
				* (this.isPreventMultiply() ? 1 : multiplier);
		if (damageFlags.isNonlethal() && trueDamage > 0 && (trueDamage + currentDamage) > unit.getLife() - 1) {
			trueDamage = Math.max(unit.getLife() - 1 - currentDamage, 0);
		}
		System.err.println("Computing damage for bonus damage... bonus:" + this.amount + " attackType:"
				+ (this.attackType == null ? primaryAttackType : this.attackType) + " damageType:" + this.damageType + " final:" + trueDamage);
		return trueDamage;
	}

	public boolean isImmuneToDamage(CSimulation simulation, CUnit unit, CAttackType attackType) {
		if (damageFlags != null && damageFlags.isOnlyDamageSummons() && !unit.isUnitType(CUnitTypeJass.SUMMONED)) {
			return true;
		}
		if (simulation.getGameplayConstants().isMagicImmuneResistsDamage()) {
			if (attackType.isMagic() || damageType.isMagic()) {
				if (unit.isMagicImmune() || (unit.isLimitedMagicImmune() && !damageFlags.isPassLimitedMagicImmune())) {
					return true;
				}
			} else if (attackType.isPhysical() || damageType.isPhysical()) {
				if (unit.isUnitType(CUnitTypeJass.ETHEREAL)) {
					return true;
				}
			}
		} else {
			if (damageType.isOldMagic() && (unit.isMagicImmune()
					|| (unit.isLimitedMagicImmune() && !damageFlags.isPassLimitedMagicImmune()))) {
				return true;
			}
		}
		return false;
	}

	public boolean isPassInvulnerable(CSimulation simulation, CUnit unit) {
		return (this.damageFlags != null
				&& (this.damageFlags.isIgnoreInvulnerable() || (this.damageFlags.isOnlyDamageSummons()
						&& simulation.getGameplayConstants().isInvulnerableSummonsTakeDispelDamage()
						&& unit.isUnitType(CUnitTypeJass.SUMMONED))));
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public CAttackType getAttackType() {
		return attackType;
	}

	public void setAttackType(CAttackType attackType) {
		this.attackType = attackType;
	}

	public CDamageType getDamageType() {
		return damageType;
	}

	public void setDamageType(CDamageType damageType) {
		this.damageType = damageType;
	}

	public CDamageFlags getDamageFlags() {
		return damageFlags;
	}

	public void setDamageFlags(CDamageFlags damageFlags) {
		this.damageFlags = damageFlags;
	}

	public void setPreventMultiply(boolean preventMultiply) {
		this.internalFlags = preventMultiply ? this.internalFlags | PREVENTMULTIPLY
				: this.internalFlags & ~PREVENTMULTIPLY;
	}

	public boolean isPreventMultiply() {
		return ((this.internalFlags & PREVENTMULTIPLY) != 0);
	}
}

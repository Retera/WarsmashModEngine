package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

public class CUnitAttackPreDamageListenerDamageModResult {
	private float baseDamage;
	private float bonusDamage;
	private float damageMultiplier;
	
	private boolean miss = false;

	private boolean unlockBonus;
	private boolean unlockMultiplier;

	public CUnitAttackPreDamageListenerDamageModResult(float baseDamage) {
		this.baseDamage = baseDamage;
		this.bonusDamage = 0;
		this.damageMultiplier = 1;

		unlockBonus = true;
		unlockMultiplier = true;
	}

	public float getBaseDamage() {
		return baseDamage;
	}

	public void setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
	}

	public float getBonusDamage() {
		return bonusDamage;
	}

	public void setBonusDamage(float bonusDamage) {
		if (unlockBonus) {
			this.bonusDamage = bonusDamage;
		}
	}
	
	public void addBonusDamage(float bonusDamage) {
		if (unlockBonus) {
			this.bonusDamage += bonusDamage;
		}
	}

	public float getDamageMultiplier() {
		return damageMultiplier;
	}

	public void setDamageMultiplier(float damageMultiplier) {
		if (unlockMultiplier) {
			this.damageMultiplier = damageMultiplier;
		}
	}
	
	public void addDamageMultiplier(float damageMultiplier) {
		if (unlockMultiplier) {
			this.damageMultiplier *= damageMultiplier;
		}
	}

	public void lockBonus() {
		unlockBonus = false;
		unlockMultiplier = false;
	}

	public void lock() {
		unlockBonus = false;
		unlockMultiplier = false;
	}

	public float computeFinalDamage() {
		return (baseDamage * damageMultiplier);
	}

	public boolean isMiss() {
		return miss;
	}

	public void setMiss(boolean miss) {
		this.miss = miss;
	}
}

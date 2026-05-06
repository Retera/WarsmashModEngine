package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;

public class CDamageCalculation {
	public static float calculateDamageByArmor(CSimulation simulation, CUnit unit, CAttackType attackType,
			CDamageType damageType, float amount, CDamageFlags damageFlags) {
		float trueDamage = 0;
		final float damageRatioFromArmorClass = simulation.getGameplayConstants().getDamageRatioAgainst(attackType,
				(unit.isBuilding() && unit.isConstructing()) ? CDefenseType.NORMAL : unit.getDefenseType());
		final float damageRatioFromDefense;
		final float defense = unit.getDefense();
		if (damageType != CDamageType.NORMAL || (unit.isBuilding() && unit.isConstructing())) {
			damageRatioFromDefense = 1.0f;
		} else if (defense >= 0) {
			damageRatioFromDefense = 1f - ((defense * simulation.getGameplayConstants().getDefenseArmor())
					/ (1 + (simulation.getGameplayConstants().getDefenseArmor() * defense)));
		} else {
			damageRatioFromDefense = 2f
					- (float) StrictMath.pow(1f - simulation.getGameplayConstants().getDefenseArmor(), -defense);
		}
		trueDamage = damageRatioFromArmorClass * damageRatioFromDefense * amount;
		if (damageFlags.isNonlethal() && trueDamage > 0 && trueDamage > unit.getLife() - 1) {
			trueDamage = unit.getLife() - 1;
		}
		return trueDamage;
	}

	private CAttackType attackType;
	private float primaryAmount;
	private CDamageType primaryDamageType;
	private CDamageFlags primaryDamageFlags;
	private String weaponSoundType;

	private LinkedList<CBonusDamage> bonusDamages;

	private CUnit source;

	private float damageMultiplier;
	private boolean miss;

	private boolean unlockBonus;
	private boolean unlockMultiplier;

	private boolean splash;
	private float splashFactor;

	private boolean endLoop = false;
	private boolean endLevel = false;

	public CDamageCalculation(CUnit source, float damage, CAttackType attackType, CDamageType damageType,
			CDamageFlags flags, String weaponSoundType) {
		this.source = source;
		this.primaryAmount = damage;
		this.attackType = attackType;
		this.primaryDamageType = damageType;
		this.primaryDamageFlags = flags;
		this.weaponSoundType = weaponSoundType;

		this.bonusDamages = null;
		this.damageMultiplier = 1;
		this.miss = false;
		this.unlockBonus = true;
		this.unlockMultiplier = true;
	}

	public void applyMultiplier() {
		if (this.damageMultiplier == 1f) {
			return;
		}
		this.primaryAmount *= this.damageMultiplier;
		if (bonusDamages != null) {
			for (CBonusDamage bonus : bonusDamages) {
				bonus.applyMultiplier(this.damageMultiplier);
			}
		}
		this.damageMultiplier = 1f;
	}

	public float computeFinalDamage(CSimulation simulation, CUnit unit) {
		if (this.miss) {
			return 0;
		}
		float trueDamage = 0;
		if (!isImmuneToPrimaryDamage(simulation, unit)
				&& (!unit.isInvulnerable() || this.isPassInvulnerable(simulation, unit))) {
			trueDamage = calculateDamageByArmor(simulation, unit, attackType, primaryDamageType, primaryAmount,
					primaryDamageFlags);
		}

		if (bonusDamages != null) {
			for (CBonusDamage bonus : bonusDamages) {
				trueDamage += bonus.computeFinalDamage(simulation, unit, this.attackType, this.primaryDamageType,
						this.damageMultiplier, trueDamage);
			}
		}

		return trueDamage * (this.splash ? this.splashFactor : 1);
	}

	public float computeRawPrimaryDamage() {
		return this.miss ? 0 : this.primaryAmount * this.damageMultiplier * (this.splash ? this.splashFactor : 1);
	}

	public float computeRawBonusDamage() {
		if (this.miss) {
			return 0;
		}
		float trueDamage = 0;

		if (bonusDamages != null) {
			for (CBonusDamage bonus : bonusDamages) {
				trueDamage += bonus.computeRawDamage(this.damageMultiplier);
			}
		}

		return trueDamage * (this.splash ? this.splashFactor : 1);
	}

	public float computeRawTotalDamage() {
		return this.miss ? 0 : this.computeRawPrimaryDamage() + this.computeRawBonusDamage();
	}

	public boolean isImmuneToPrimaryDamage(CSimulation simulation, CUnit unit) {
		if (primaryDamageFlags != null && primaryDamageFlags.isOnlyDamageSummons()
				&& !unit.isUnitType(CUnitTypeJass.SUMMONED)) {
			return true;
		}
		if (simulation.getGameplayConstants().isMagicImmuneResistsDamage()) {
			if (attackType.isMagic() || primaryDamageType.isMagic()) {
				if (unit.isMagicImmune()
						|| (unit.isLimitedMagicImmune() && !primaryDamageFlags.isPassLimitedMagicImmune())) {
					return true;
				}
			} else if (attackType.isPhysical() || primaryDamageType.isPhysical()) {
				if (unit.isUnitType(CUnitTypeJass.ETHEREAL)) {
					return true;
				}
			}
		} else {
			if (primaryDamageType.isOldMagic() && (unit.isMagicImmune()
					|| (unit.isLimitedMagicImmune() && !primaryDamageFlags.isPassLimitedMagicImmune()))) {
				return true;
			}
		}
		return false;
	}

	public boolean isImmuneToDamage(CSimulation simulation, CUnit unit) {
		if (this.isImmuneToPrimaryDamage(simulation, unit)) {
			if (bonusDamages != null) {
				for (CBonusDamage bonus : bonusDamages) {
					if (!bonus.isImmuneToDamage(simulation, unit, this.attackType)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean isExplode() {
		if (this.primaryDamageFlags.isExplode()) {
			return true;
		}
		if (this.bonusDamages != null) {
			for (CBonusDamage bonus : this.bonusDamages) {
				if (bonus.getDamageFlags().isExplode()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isPassInvulnerable(CSimulation simulation, CUnit unit) {
		if (this.primaryDamageFlags != null
				&& (this.primaryDamageFlags.isIgnoreInvulnerable() || (this.primaryDamageFlags.isOnlyDamageSummons()
						&& simulation.getGameplayConstants().isInvulnerableSummonsTakeDispelDamage()
						&& unit.isUnitType(CUnitTypeJass.SUMMONED)))) {
			return true;
		}
		if (this.bonusDamages != null) {
			for (CBonusDamage bonus : this.bonusDamages) {
				if (bonus.isPassInvulnerable(simulation, unit)) {
					return true;
				}
			}
		}
		return false;
	}

	public CAttackType getAttackType() {
		return attackType;
	}

	public void setAttackType(CAttackType attackType) {
		this.attackType = attackType;
	}

	public float getPrimaryAmount() {
		return primaryAmount;
	}

	public void setPrimaryAmount(float primaryAmount) {
		this.primaryAmount = primaryAmount;
	}

	public CDamageType getPrimaryDamageType() {
		return primaryDamageType;
	}

	public void setPrimaryDamageType(CDamageType primaryDamageType) {
		this.primaryDamageType = primaryDamageType;
	}

	public CDamageFlags getPrimaryDamageFlags() {
		return primaryDamageFlags;
	}

	public void setPrimaryDamageFlags(CDamageFlags primaryDamageFlags) {
		this.primaryDamageFlags = primaryDamageFlags;
	}

	public String getWeaponSoundType() {
		return weaponSoundType;
	}

	public void setWeaponSoundType(String weaponSoundType) {
		this.weaponSoundType = weaponSoundType;
	}

	public List<CBonusDamage> getBonusDamages() {
		return bonusDamages;
	}

	public void setBonusDamages(LinkedList<CBonusDamage> bonusDamages) {
		this.bonusDamages = bonusDamages;
	}

	public CUnit getSource() {
		return source;
	}

	public void setSource(CUnit source) {
		this.source = source;
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

	public boolean isMiss() {
		return miss;
	}

	public void setMiss(boolean miss) {
		this.miss = miss;
	}

	public boolean isUnlockBonus() {
		return unlockBonus;
	}

	public void setUnlockBonus(boolean unlockBonus) {
		this.unlockBonus = unlockBonus;
	}

	public boolean isUnlockMultiplier() {
		return unlockMultiplier;
	}

	public void setUnlockMultiplier(boolean unlockMultiplier) {
		this.unlockMultiplier = unlockMultiplier;
	}

	public void startSplash(float splashFactor) {
		this.splashFactor = splashFactor;
		this.splash = true;
	}

	public void endSplash() {
		this.splash = false;
	}

	// Loop controls
	public void startLoop(int i) {
		this.endLevel = false;
	}

	public void preventOtherModificationsWithSamePriority() {
		this.endLevel = true;
	}

	public void preventOtherModificationsOfOtherPriorities() {
		this.endLoop = true;
	}

	public void preventAllOtherModifications() {
		this.endLevel = true;
		this.endLoop = true;
	}

	public boolean isSkipCurrentLevel() {
		return this.endLevel;
	}

	public boolean isEndLoop() {
		return this.endLoop;
	}

	public void resetLoop() {
		this.endLevel = false;
		this.endLoop = false;
	}

	public void subtractTotalDamageDealt(float reduction, float minimum) {
		float primMinus = this.primaryAmount - reduction;
		if (primMinus > minimum) {
			this.primaryAmount = primMinus;
		} else {
			if (this.bonusDamages != null) {
				float total = this.primaryAmount;
				for (CBonusDamage bonus : this.bonusDamages) {
					total += bonus.getAmount();
				}
				float target = Math.max(minimum, total - reduction);
				if (target < total) {
					Iterator<CBonusDamage> reverse = this.bonusDamages.descendingIterator();
					CBonusDamage bonus = null;
					do {
						bonus = reverse.next();
						float cur = bonus.getAmount();
						if (cur > target) {
							bonus.setAmount(target);
							target = 0;
						} else {
							target = Math.max(0, target - cur);
						}
					} while (reverse.hasNext());
					this.primaryAmount = target;
				}
			} else {
				this.primaryAmount = Math.max(minimum, primMinus);
			}
		}

	}

	public CBonusDamage addBonusDamage(float amount, CAttackType attackType, CDamageType damageType,
			CDamageFlags flags) {
		if (bonusDamages == null) {
			this.bonusDamages = new LinkedList<>();
		}
		CBonusDamage nb = new CBonusDamage();
		nb.setAmount(amount);
		if (attackType != null) {
			nb.setAttackType(attackType);
		}
		if (damageType == null) {
			nb.setDamageType(primaryDamageType);
		} else {
			nb.setDamageType(damageType);
		}
		if (flags == null) {
			nb.setDamageFlags(primaryDamageFlags.copy());
		} else {
			nb.setDamageFlags(flags);
		}
		this.bonusDamages.add(nb);
		return nb;
	}
}

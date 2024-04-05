package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackEffectListenerStacking;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPostDamageListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerDamageModResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerPriority;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackReplacementEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

/**
 * The base class for unit-data-based combat attacks.
 *
 * I really wanted to split this out into sub classes based on weapon type, but
 * I came to realize the Ballista in RoC probably had the spill distance effect
 * & area of effect both after it upgrades Impaling Bolt, and this would point
 * out that the behaviors were not mutually exclusive.
 *
 * Then I reviewed it and decided that in RoC, the Impaling Bolts upgrade did
 * not interact with the damage spill combat settings from the UnitWeapons.slk,
 * because many of those settings did not exist. So I will attempt to emulate
 * these attacks as best as possible.
 */
public abstract class CUnitAttack {
	private float animationBackswingPointBase;
	private float animationBackswingPoint;
	private float animationDamagePoint;
	private float animationDamagePointBase;
	private CAttackType attackType;
	private final float cooldownTimeBase;
	private float cooldownTime;
	private int damageBase;
	private int damageDice;
	private int damageSidesPerDie;
	private int damageUpgradeAmount;
	private int range;
	private float rangeMotionBuffer;
	private boolean showUI;
	private EnumSet<CTargetType> targetsAllowed;
	private String weaponSound;
	private CWeaponType weaponType;

	private float agiAttackSpeedBonus;
	private float attackSpeedBonus;
	private int primaryAttributePermanentDamageBonus;
	private int primaryAttributeTemporaryDamageBonus;
	private int permanentDamageBonus;
	private int temporaryDamageBonus;

	private float attackSpeedModifier;

	private Map<String, List<NonStackingStatBuff>> nonStackingFlatBuffs = new HashMap<>();
	private Map<String, List<NonStackingStatBuff>> nonStackingPctBuffs = new HashMap<>();
	
	protected CUnitAttackReplacementEffect attackReplacement = null;

	// calculate
	private int totalBaseDamage;
	private int totalDamageDice;
	private int minDamageDisplay;
	private int maxDamageDisplay;
	private int totalTemporaryDamageBonus;
	private float totalAttackSpeedPercent;

	public CUnitAttack(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType) {
		this.animationBackswingPointBase = animationBackswingPoint;
		this.animationDamagePointBase = animationDamagePoint;
		this.attackType = attackType;
		this.cooldownTimeBase = cooldownTime;
		this.damageBase = damageBase;
		this.damageDice = damageDice;
		this.damageSidesPerDie = damageSidesPerDie;
		this.damageUpgradeAmount = damageUpgradeAmount;
		this.range = range;
		this.rangeMotionBuffer = rangeMotionBuffer;
		this.showUI = showUI;
		this.targetsAllowed = targetsAllowed;
		this.weaponSound = weaponSound;
		this.weaponType = weaponType;
		computeDerivedFields();
	}

	public CUnitAttack(final CUnitAttack other) {
		this.animationBackswingPointBase = other.animationBackswingPointBase;
		this.animationDamagePointBase = other.animationDamagePointBase;
		this.attackType = other.attackType;
		this.cooldownTimeBase = other.cooldownTimeBase;
		this.damageBase = other.damageBase;
		this.damageDice = other.damageDice;
		this.damageSidesPerDie = other.damageSidesPerDie;
		this.damageUpgradeAmount = other.damageUpgradeAmount;
		this.range = other.range;
		this.rangeMotionBuffer = other.rangeMotionBuffer;
		this.showUI = other.showUI;
		this.targetsAllowed = other.targetsAllowed;
		this.weaponSound = other.weaponSound;
		this.weaponType = other.weaponType;

		this.agiAttackSpeedBonus = other.agiAttackSpeedBonus;
		this.attackSpeedBonus = other.attackSpeedBonus;
		this.primaryAttributePermanentDamageBonus = other.primaryAttributePermanentDamageBonus;
		this.primaryAttributeTemporaryDamageBonus = other.primaryAttributeTemporaryDamageBonus;
		this.permanentDamageBonus = other.permanentDamageBonus;
		this.temporaryDamageBonus = other.temporaryDamageBonus;
		computeDerivedFields();
	}

	public abstract CUnitAttack copy();

	public void computeDerivedFields() {
		this.totalBaseDamage = this.damageBase + this.primaryAttributePermanentDamageBonus + this.permanentDamageBonus;
		this.totalDamageDice = this.damageDice;
		this.minDamageDisplay = this.totalBaseDamage + this.totalDamageDice;
		this.maxDamageDisplay = this.totalBaseDamage + (this.totalDamageDice * this.damageSidesPerDie);

		int totalNSAtkBuff = 0;
		for (final String key : this.nonStackingFlatBuffs.keySet()) {
			float buffForKey = 0;
			for (final NonStackingStatBuff buff : this.nonStackingFlatBuffs.get(key)) {
				if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
					buffForKey += buff.getValue();
				}
				else {
					buffForKey = Math.max(buffForKey, buff.getValue());
				}
			}
			totalNSAtkBuff += buffForKey;
		}
		int totalNSAtkPctBuff = 0;
		for (final String key : this.nonStackingPctBuffs.keySet()) {
			Float buffForKey = null;
			for (final NonStackingStatBuff buff : this.nonStackingPctBuffs.get(key)) {
				if (buffForKey == null) {
					buffForKey = buff.getValue();
				}
				else {
					if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
						buffForKey += buff.getValue();
					}
					else {
						buffForKey = Math.max(buffForKey, buff.getValue());
					}
				}
			}
			if (buffForKey == null) {
				continue;
			}
			int otherAtkBonus = (int) (this.totalBaseDamage * buffForKey)
					+ (int) Math.ceil(((this.totalDamageDice * (1 + this.damageSidesPerDie)) / 2) * buffForKey);
			if (otherAtkBonus == 0) {
				otherAtkBonus = (int) (buffForKey / Math.abs(buffForKey));
			}
			if (otherAtkBonus <= 0) {
				otherAtkBonus = Math.max(otherAtkBonus, -1 * this.minDamageDisplay);
			}
			totalNSAtkPctBuff += otherAtkBonus;
		}

		if (this.minDamageDisplay < 0) {
			this.minDamageDisplay = 0;
		}
		if (this.maxDamageDisplay < 0) {
			this.maxDamageDisplay = 0;
		}

		this.totalTemporaryDamageBonus = this.primaryAttributeTemporaryDamageBonus + this.temporaryDamageBonus
				+ totalNSAtkBuff + totalNSAtkPctBuff;
		float totalAttackSpeedBonus = this.agiAttackSpeedBonus + this.attackSpeedBonus + this.attackSpeedModifier;
		float totalAttackSpeedPercent = 1.0f + Math.max(Math.min(totalAttackSpeedBonus, 4), -0.9f);
		// TODO there might be a gameplay constants value for this instead of 0.0001, didn't look
		if (totalAttackSpeedPercent <= 0.0001f) {
			totalAttackSpeedPercent = 0.0001f;
		}
		this.cooldownTime = this.cooldownTimeBase / totalAttackSpeedPercent;
		this.totalAttackSpeedPercent = totalAttackSpeedPercent;
		this.animationBackswingPoint = this.animationBackswingPointBase / totalAttackSpeedPercent;
		this.animationDamagePoint = this.animationDamagePointBase / totalAttackSpeedPercent;
	}

	public float getAnimationBackswingPoint() {
		return this.animationBackswingPoint;
	}

	public float getAnimationDamagePoint() {
		return this.animationDamagePoint;
	}

	public CAttackType getAttackType() {
		return this.attackType;
	}

	public float getCooldownTime() {
		return this.cooldownTime;
	}

	public int getDamageBase() {
		return this.damageBase;
	}

	public int getDamageDice() {
		return this.damageDice;
	}

	public int getDamageSidesPerDie() {
		return this.damageSidesPerDie;
	}

	public int getDamageUpgradeAmount() {
		return this.damageUpgradeAmount;
	}

	public int getRange() {
		return this.range;
	}

	public float getRangeMotionBuffer() {
		return this.rangeMotionBuffer;
	}

	public boolean isShowUI() {
		return this.showUI;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	public String getWeaponSound() {
		return this.weaponSound;
	}

	public CWeaponType getWeaponType() {
		return this.weaponType;
	}

	public void setAnimationBackswingPoint(final float animationBackswingPoint) {
		this.animationBackswingPointBase = animationBackswingPoint;
	}

	public void setAnimationDamagePoint(final float animationDamagePoint) {
		this.animationDamagePointBase = animationDamagePoint;
	}

	public void setAttackType(final CAttackType attackType) {
		this.attackType = attackType;
	}

	public void setCooldownTime(final float cooldownTime) {
		this.cooldownTime = cooldownTime;
		computeDerivedFields();
	}

	public void setDamageBase(final int damageBase) {
		this.damageBase = damageBase;
		computeDerivedFields();
	}

	public void setDamageDice(final int damageDice) {
		this.damageDice = damageDice;
		computeDerivedFields();
	}

	public void setDamageSidesPerDie(final int damageSidesPerDie) {
		this.damageSidesPerDie = damageSidesPerDie;
		computeDerivedFields();
	}

	public void setDamageUpgradeAmount(final int damageUpgradeAmount) {
		this.damageUpgradeAmount = damageUpgradeAmount;
	}

	public void setRange(final int range) {
		this.range = range;
	}

	public void setRangeMotionBuffer(final float rangeMotionBuffer) {
		this.rangeMotionBuffer = rangeMotionBuffer;
	}

	public void setShowUI(final boolean showUI) {
		this.showUI = showUI;
	}

	public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	public void setWeaponSound(final String weaponSound) {
		this.weaponSound = weaponSound;
	}

	public void setWeaponType(final CWeaponType weaponType) {
		this.weaponType = weaponType;
	}

	public int getMinDamageDisplay() {
		return this.minDamageDisplay;
	}

	public int getMaxDamageDisplay() {
		return this.maxDamageDisplay;
	}

	public void setPrimaryAttributePermanentDamageBonus(final int primaryAttributeDamageBonus) {
		this.primaryAttributePermanentDamageBonus = primaryAttributeDamageBonus;
		computeDerivedFields();
	}

	public void setPrimaryAttributeTemporaryDamageBonus(final int primaryAttributeDamageBonus) {
		this.primaryAttributeTemporaryDamageBonus = primaryAttributeDamageBonus;
		computeDerivedFields();
	}

	public void setPermanentDamageBonus(final int permanentDamageBonus) {
		this.permanentDamageBonus = permanentDamageBonus;
		computeDerivedFields();
	}

	public void setTemporaryDamageBonus(final int temporaryDamageBonus) {
		this.temporaryDamageBonus = temporaryDamageBonus;
		computeDerivedFields();
	}

	public float getAttackSpeedModifier() {
		return this.attackSpeedModifier;
	}

	public void setAttackSpeedModifier(float attackSpeedModifier) {
		this.attackSpeedModifier = attackSpeedModifier;
		computeDerivedFields();
	}

	public Map<String, List<NonStackingStatBuff>> getNonStackingFlatBuffs() {
		return this.nonStackingFlatBuffs;
	}

	public void setNonStackingFlatBuffs(final Map<String, List<NonStackingStatBuff>> nonStackingFlatBuffs) {
		this.nonStackingFlatBuffs = nonStackingFlatBuffs;
	}

	public Map<String, List<NonStackingStatBuff>> getNonStackingPctBuffs() {
		return this.nonStackingPctBuffs;
	}

	public void setNonStackingPctBuffs(final Map<String, List<NonStackingStatBuff>> nonStackingPctBuffs) {
		this.nonStackingPctBuffs = nonStackingPctBuffs;
	}

	public void setAgilityAttackSpeedBonus(final float agiAttackSpeedBonus) {
		this.agiAttackSpeedBonus = agiAttackSpeedBonus;
		computeDerivedFields();
	}

	public void setAttackSpeedBonus(final float attackSpeedBonus) {
		this.attackSpeedBonus = attackSpeedBonus;
		computeDerivedFields();
	}

	public float getAttackSpeedBonus() {
		return this.attackSpeedBonus;
	}

	public int getPrimaryAttributePermanentDamageBonus() {
		return this.primaryAttributePermanentDamageBonus;
	}

	public int getPrimaryAttributeTemporaryDamageBonus() {
		return this.primaryAttributeTemporaryDamageBonus;
	}

	public int getPermanentDamageBonus() {
		return this.permanentDamageBonus;
	}

	public int getTemporaryDamageBonus() {
		return this.temporaryDamageBonus;
	}

	public int getTotalDamageDice() {
		return this.totalDamageDice;
	}

	public int getTotalBaseDamage() {
		return this.totalBaseDamage;
	}

	public int getTotalTemporaryDamageBonus() {
		return this.totalTemporaryDamageBonus;
	}

	public float getTotalAttackSpeedPercent() {
		return this.totalAttackSpeedPercent;
	}

	public abstract void launch(CSimulation simulation, CUnit unit, AbilityTarget target, float damage,
			CUnitAttackListener attackListener);

	public int roll(final Random seededRandom) {
		int damage = getTotalBaseDamage();
		final int dice = getTotalDamageDice();
		final int sidesPerDie = getDamageSidesPerDie();
		for (int i = 0; i < dice; i++) {
			final int singleRoll = sidesPerDie == 0 ? 0 : seededRandom.nextInt(sidesPerDie);
			damage += singleRoll + 1;
		}
		return damage + getTotalTemporaryDamageBonus();
	}

	public CUnitAttackPreDamageListenerDamageModResult runPreDamageListeners(final CSimulation simulation,
			final CUnit attacker, final AbilityTarget target, final float damage) {
		final CUnitAttackPreDamageListenerDamageModResult result = new CUnitAttackPreDamageListenerDamageModResult(
				damage);
		CUnitAttackEffectListenerStacking allowContinue = new CUnitAttackEffectListenerStacking();

		for (final CUnitAttackPreDamageListenerPriority priority : CUnitAttackPreDamageListenerPriority.values()) {
			if (allowContinue.isAllowStacking()) {
				if (priority == CUnitAttackPreDamageListenerPriority.ATTACKREPLACEMENT && this.attackReplacement != null && this.attackReplacement.getPreDamageListeners() != null) {
					for (CUnitAttackPreDamageListener listener : this.attackReplacement.getPreDamageListeners()) {
						if (allowContinue.isAllowSamePriorityStacking()) {
							allowContinue = listener.onAttack(simulation, attacker, target, weaponType, attackType, weaponType.getDamageType(), result);
						}
					}
				} else {
					for (CUnitAttackPreDamageListener listener : attacker.getPreDamageListenersForPriority(priority)) {
						if (allowContinue.isAllowSamePriorityStacking()) {
							allowContinue = listener.onAttack(simulation, attacker, target, weaponType, attackType, weaponType.getDamageType(), result);
						}
					}
				}
			}
		}
		if (result.isMiss()) {
			if (this.weaponType == CWeaponType.ARTILLERY) {
				result.setDamageMultiplier(simulation.getGameplayConstants().getMissDamageReduction());
			}
			else if (this.weaponType == CWeaponType.MSPLASH) {
				result.setDamageMultiplier(simulation.getGameplayConstants().getMissDamageReduction());
				simulation.spawnTextTag(attacker, attacker.getPlayerIndex(), TextTagConfigType.CRITICAL_STRIKE, "miss");
			}
			else {
				result.setBaseDamage(0);
				result.setBonusDamage(0);
				result.setDamageMultiplier(0);
				simulation.spawnTextTag(attacker, attacker.getPlayerIndex(), TextTagConfigType.CRITICAL_STRIKE, "miss"); // TODO
																															// Technically
																															// cheating
																															// here
			}
		}

		if (!result.isMiss() && (result.getDamageMultiplier() != 1) && (result.getDamageMultiplier() != 0)) {
			simulation.spawnTextTag(attacker, attacker.getPlayerIndex(), TextTagConfigType.CRITICAL_STRIKE,
					Math.round(result.computeFinalDamage()));
		}
		else if (result.getBonusDamage() != 0) {
			simulation.spawnTextTag(attacker, attacker.getPlayerIndex(), TextTagConfigType.BASH,
					Math.round(result.getBonusDamage()));
		}
		return result;
	}

	public void runPostDamageListeners(final CSimulation simulation, final CUnit attacker, final AbilityTarget target,
			final float actualDamage) {
		for (final CUnitAttackPostDamageListener listener : attacker.getPostDamageListeners()) {
			listener.onHit(simulation, attacker, target, actualDamage);

		}
	}
}

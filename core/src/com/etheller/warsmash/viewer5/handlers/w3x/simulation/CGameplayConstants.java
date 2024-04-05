package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.Arrays;

import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;

/**
 * Stores some gameplay constants at runtime in a java object (symbol table) to
 * maybe be faster than a map.
 */
public class CGameplayConstants {
	private final float attackHalfAngle;
	private final float[][] damageBonusTable;
	private final float maxCollisionRadius;
	private final float decayTime;
	private final float boneDecayTime;
	private final float dissipateTime;
	private final float bulletDeathTime;
	private final float closeEnoughRange;
	private final float dawnTimeGameHours;
	private final float duskTimeGameHours;
	private final float gameDayHours;
	private final float gameDayLength;
	private final float structureDecayTime;
	private final float buildingAngle;
	private final float rootAngle;
	
	private final float fogFlashTime;
	private final float dyingRevealRadius;
	private final float foggedAttackRevealRadius;

	private final float defenseArmor;

	private final float etherealDamageBonusSpells;
	private final float etherealDamageBonusMagic;
	private final boolean etherealDamageBonusAlly;
	
	private final boolean magicImmuneResistsDamage;
	private final boolean magicImmuneResistsLeech;
	private final boolean magicImmuneResistsThorns;
	private final boolean magicImmuneResistsUltimates;
	
	private final boolean defendDeflection;

	private final int heroMaxReviveCostGold;
	private final int heroMaxReviveCostLumber;
	private final int heroMaxReviveTime;

	private final int heroMaxAwakenCostGold;
	private final int heroMaxAwakenCostLumber;

	private final float heroReviveManaStart;
	private final float heroReviveManaFactor;
	private final float heroReviveLifeFactor;
	private final float heroAwakenManaStart;
	private final float heroAwakenManaFactor;
	private final float heroAwakenLifeFactor;

	private final int heroExpRange;

	private final float reviveBaseFactor;
	private final float reviveLevelFactor;
	private final float reviveBaseLumberFactor;
	private final float reviveLumberLevelFactor;
	private final float reviveMaxFactor;
	private final float reviveTimeFactor;
	private final float reviveMaxTimeFactor;

	private final float awakenBaseFactor;
	private final float awakenLevelFactor;
	private final float awakenBaseLumberFactor;
	private final float awakenLumberLevelFactor;
	private final float awakenMaxFactor;

	private final int maxHeroLevel;
	private final int maxUnitLevel;
	private final int[] needHeroXp;
	private final int[] needHeroXpSum;
	private final int[] grantHeroXp;
	private final int[] grantNormalXp;
	private final int[] heroFactorXp;
	private final float summonedKillFactor;
	private final float strAttackBonus;
	private final float strHitPointBonus;
	private final float strRegenBonus;
	private final float intManaBonus;
	private final float intRegenBonus;
	private final float agiDefenseBonus;
	private final float agiDefenseBase;
	private final int agiMoveBonus;
	private final float agiAttackSpeedBonus;

	private final int needHeroXPFormulaA;
	private final int needHeroXPFormulaB;
	private final int needHeroXPFormulaC;
	private final int grantHeroXPFormulaA;
	private final int grantHeroXPFormulaB;
	private final int grantHeroXPFormulaC;
	private final int grantNormalXPFormulaA;
	private final int grantNormalXPFormulaB;
	private final int grantNormalXPFormulaC;

	private final int heroAbilityLevelSkip;

	private final boolean globalExperience;
	private final boolean maxLevelHeroesDrainExp;
	private final boolean buildingKillsGiveExp;

	private final float dropItemRange;
	private final float giveItemRange;
	private final float pickupItemRange;
	private final float pawnItemRange;
	private final float pawnItemRate;

	private final float followRange;
	private final float structureFollowRange;
	private final float followItemRange;
	private final float spellCastRangeBuffer;

	private final boolean relativeUpgradeCosts;
	private final float minUnitSpeed;
	private final float maxUnitSpeed;
	private final float minBldgSpeed;
	private final float maxBldgSpeed;
	
	private final float chanceToMiss;
	private final float missDamageReduction;

	public CGameplayConstants(final DataTable parsedDataTable) {
		final Element miscData = parsedDataTable.get("Misc");
		// TODO use radians for half angle
		this.attackHalfAngle = (float) Math.toDegrees(miscData.getFieldFloatValue("AttackHalfAngle"));
		this.maxCollisionRadius = miscData.getFieldFloatValue("MaxCollisionRadius");
		this.decayTime = miscData.getFieldFloatValue("DecayTime");
		this.boneDecayTime = miscData.getFieldFloatValue("BoneDecayTime");
		this.dissipateTime = miscData.getFieldFloatValue("DissipateTime");
		this.structureDecayTime = miscData.getFieldFloatValue("StructureDecayTime");
		this.bulletDeathTime = miscData.getFieldFloatValue("BulletDeathTime");
		this.closeEnoughRange = miscData.getFieldFloatValue("CloseEnoughRange");

		this.dawnTimeGameHours = miscData.getFieldFloatValue("Dawn");
		this.duskTimeGameHours = miscData.getFieldFloatValue("Dusk");
		this.gameDayHours = miscData.getFieldFloatValue("DayHours");
		this.gameDayLength = miscData.getFieldFloatValue("DayLength");

		this.buildingAngle = miscData.getFieldFloatValue("BuildingAngle");
		this.rootAngle = miscData.getFieldFloatValue("RootAngle");

		this.fogFlashTime = miscData.getFieldFloatValue("FogFlashTime");
		this.dyingRevealRadius = miscData.getFieldFloatValue("DyingRevealRadius");
		this.foggedAttackRevealRadius = miscData.getFieldFloatValue("FoggedAttackRevealRadius");

		final CDefenseType[] defenseTypeOrder = { CDefenseType.SMALL, CDefenseType.MEDIUM, CDefenseType.LARGE,
				CDefenseType.FORT, CDefenseType.NORMAL, CDefenseType.HERO, CDefenseType.DIVINE, CDefenseType.NONE, };
		this.damageBonusTable = new float[CAttackType.values().length][defenseTypeOrder.length];
		for (int i = 0; i < CAttackType.VALUES.length; i++) {
			Arrays.fill(this.damageBonusTable[i], 1.0f);
			final CAttackType attackType = CAttackType.VALUES[i];
			String fieldName = "DamageBonus" + attackType.getDamageKey();
			if (!miscData.hasField(fieldName) && attackType == CAttackType.SPELLS) {
				fieldName = "DamageBonus" + CAttackType.MAGIC.getDamageKey();
			}
			final String damageBonus = miscData.getField(fieldName);
			final String[] damageComponents = damageBonus.split(",");
			for (int j = 0; j < damageComponents.length; j++) {
				if (damageComponents[j].length() > 0) {
					final CDefenseType defenseType = defenseTypeOrder[j];
					try {
						this.damageBonusTable[i][defenseType.ordinal()] = Float.parseFloat(damageComponents[j]);
//						System.out.println(attackType + ":" + defenseType + ": " + damageComponents[j]);
					}
					catch (final NumberFormatException e) {
						throw new RuntimeException(fieldName, e);
					}
				}
			}
		}

		this.defenseArmor = miscData.getFieldFloatValue("DefenseArmor");

		final String damageBonus = miscData.getField("EtherealDamageBonus");
		final String[] damageComponents = damageBonus.split(",");
		float magBonus = 1;
		float spellBonus = 1;
		for (int j = 0; j < damageComponents.length; j++) {
			if (j == 3) {
				if (damageComponents[j].length() > 0) {
					try {
						magBonus = Float.parseFloat(damageComponents[j]);
					}
					catch (final NumberFormatException e) {
						throw new RuntimeException("EtherealDamageBonus", e);
					}
				}
			} else if (j == 5) {
				if (damageComponents[j].length() > 0) {
					try {
						spellBonus = Float.parseFloat(damageComponents[j]);
					}
					catch (final NumberFormatException e) {
						throw new RuntimeException("EtherealDamageBonus", e);
					}
				}
			}
		}
		this.etherealDamageBonusMagic = magBonus;
		this.etherealDamageBonusSpells = spellBonus;
		this.etherealDamageBonusAlly = miscData.getFieldValue("EtherealDamageBonusAlly") != 0;

		this.magicImmuneResistsDamage = miscData.getFieldValue("MagicImmunesResistDamage") != 0;
		this.magicImmuneResistsLeech = miscData.getFieldValue("MagicImmunesResistLeech") != 0;
		this.magicImmuneResistsThorns = miscData.getFieldValue("MagicImmunesResistThorns") != 0;
		this.magicImmuneResistsUltimates = miscData.getFieldValue("MagicImmunesResistUltimates") != 0;
		
		this.defendDeflection = miscData.getFieldValue("DefendDeflection") != 0;

		this.globalExperience = miscData.getFieldValue("GlobalExperience") != 0;
		this.maxLevelHeroesDrainExp = miscData.getFieldValue("MaxLevelHeroesDrainExp") != 0;
		this.buildingKillsGiveExp = miscData.getFieldValue("BuildingKillsGiveExp") != 0;

		this.heroMaxReviveCostGold = miscData.getFieldValue("HeroMaxReviveCostGold");
		this.heroMaxReviveCostLumber = miscData.getFieldValue("HeroMaxReviveCostLumber");
		this.heroMaxReviveTime = miscData.getFieldValue("HeroMaxReviveTime");

		this.heroMaxAwakenCostGold = miscData.getFieldValue("HeroMaxAwakenCostGold");
		this.heroMaxAwakenCostLumber = miscData.getFieldValue("HeroMaxAwakenCostLumber");

		this.heroReviveManaStart = miscData.getFieldFloatValue("HeroReviveManaStart");
		this.heroReviveManaFactor = miscData.getFieldFloatValue("HeroReviveManaFactor");
		this.heroReviveLifeFactor = miscData.getFieldFloatValue("HeroReviveLifeFactor");
		this.heroAwakenManaStart = miscData.getFieldFloatValue("HeroAwakenManaStart");
		this.heroAwakenManaFactor = miscData.getFieldFloatValue("HeroAwakenManaFactor");
		this.heroAwakenLifeFactor = miscData.getFieldFloatValue("HeroAwakenLifeFactor");

		this.heroExpRange = miscData.getFieldValue("HeroExpRange");

		this.reviveBaseFactor = miscData.getFieldFloatValue("ReviveBaseFactor");
		this.reviveLevelFactor = miscData.getFieldFloatValue("ReviveLevelFactor");
		this.reviveBaseLumberFactor = miscData.getFieldFloatValue("ReviveBaseLumberFactor");
		this.reviveLumberLevelFactor = miscData.getFieldFloatValue("ReviveLumberLevelFactor");
		this.reviveMaxFactor = miscData.getFieldFloatValue("ReviveMaxFactor");
		this.reviveTimeFactor = miscData.getFieldFloatValue("ReviveTimeFactor");
		this.reviveMaxTimeFactor = miscData.getFieldFloatValue("ReviveMaxTimeFactor");

		this.awakenBaseFactor = miscData.getFieldFloatValue("AwakenBaseFactor");
		this.awakenLevelFactor = miscData.getFieldFloatValue("AwakenLevelFactor");
		this.awakenBaseLumberFactor = miscData.getFieldFloatValue("AwakenBaseLumberFactor");
		this.awakenLumberLevelFactor = miscData.getFieldFloatValue("AwakenLumberLevelFactor");
		this.awakenMaxFactor = miscData.getFieldFloatValue("AwakenMaxFactor");

		this.maxHeroLevel = miscData.getFieldValue("MaxHeroLevel");
		this.maxUnitLevel = miscData.getFieldValue("MaxUnitLevel");

		this.needHeroXPFormulaA = miscData.getFieldValue("NeedHeroXPFormulaA");
		this.needHeroXPFormulaB = miscData.getFieldValue("NeedHeroXPFormulaB");
		this.needHeroXPFormulaC = miscData.getFieldValue("NeedHeroXPFormulaC");
		this.grantHeroXPFormulaA = miscData.getFieldValue("GrantHeroXPFormulaA");
		this.grantHeroXPFormulaB = miscData.getFieldValue("GrantHeroXPFormulaB");
		this.grantHeroXPFormulaC = miscData.getFieldValue("GrantHeroXPFormulaC");
		this.grantNormalXPFormulaA = miscData.getFieldValue("GrantNormalXPFormulaA");
		this.grantNormalXPFormulaB = miscData.getFieldValue("GrantNormalXPFormulaB");
		this.grantNormalXPFormulaC = miscData.getFieldValue("GrantNormalXPFormulaC");

		this.needHeroXp = parseTable(miscData.getField("NeedHeroXP"), this.needHeroXPFormulaA, this.needHeroXPFormulaB,
				this.needHeroXPFormulaC, this.maxHeroLevel);
		this.needHeroXpSum = new int[this.needHeroXp.length];
		for (int i = 0; i < this.needHeroXpSum.length; i++) {
			if (i == 0) {
				this.needHeroXpSum[i] = this.needHeroXp[i];
			}
			else {
				this.needHeroXpSum[i] = this.needHeroXp[i] + this.needHeroXpSum[i - 1];
			}
		}
		this.grantHeroXp = parseTable(miscData.getField("GrantHeroXP"), this.grantHeroXPFormulaA,
				this.grantHeroXPFormulaB, this.grantHeroXPFormulaC, this.maxHeroLevel);
		this.grantNormalXp = parseTable(miscData.getField("GrantNormalXP"), this.grantNormalXPFormulaA,
				this.grantNormalXPFormulaB, this.grantNormalXPFormulaC, this.maxUnitLevel);
		this.heroFactorXp = parseIntArray(miscData.getField("HeroFactorXP"));
		this.summonedKillFactor = miscData.getFieldFloatValue("SummonedKillFactor");
		this.strAttackBonus = miscData.getFieldFloatValue("StrAttackBonus");
		this.strHitPointBonus = miscData.getFieldFloatValue("StrHitPointBonus");
		this.strRegenBonus = miscData.getFieldFloatValue("StrRegenBonus");
		this.intManaBonus = miscData.getFieldFloatValue("IntManaBonus");
		this.intRegenBonus = miscData.getFieldFloatValue("IntRegenBonus");
		this.agiDefenseBonus = miscData.getFieldFloatValue("AgiDefenseBonus");
		this.agiDefenseBase = miscData.getFieldFloatValue("AgiDefenseBase");
		this.agiMoveBonus = miscData.getFieldValue("AgiMoveBonus");
		this.agiAttackSpeedBonus = miscData.getFieldFloatValue("AgiAttackSpeedBonus");

		this.heroAbilityLevelSkip = miscData.getFieldValue("HeroAbilityLevelSkip");

		this.dropItemRange = miscData.getFieldFloatValue("DropItemRange");
		this.giveItemRange = miscData.getFieldFloatValue("GiveItemRange");
		this.pickupItemRange = miscData.getFieldFloatValue("PickupItemRange");
		this.pawnItemRange = miscData.getFieldFloatValue("PawnItemRange");
		this.pawnItemRate = miscData.getFieldFloatValue("PawnItemRate");

		this.followRange = miscData.getFieldFloatValue("FollowRange");
		this.structureFollowRange = miscData.getFieldFloatValue("StructureFollowRange");
		this.followItemRange = miscData.getFieldFloatValue("FollowItemRange");

		this.spellCastRangeBuffer = miscData.getFieldFloatValue("SpellCastRangeBuffer");

		this.relativeUpgradeCosts = miscData.getFieldValue("RelativeUpgradeCost") == 0;

		this.minUnitSpeed = miscData.getFieldFloatValue("MinUnitSpeed");
		this.maxUnitSpeed = miscData.getFieldFloatValue("MaxUnitSpeed");
		this.minBldgSpeed = miscData.getFieldFloatValue("MinBldgSpeed");
		this.maxBldgSpeed = miscData.getFieldFloatValue("MaxBldgSpeed");

		this.chanceToMiss = miscData.getFieldFloatValue("ChanceToMiss");
		this.missDamageReduction = miscData.getFieldFloatValue("MissDamageReduction");
	}

	public float getAttackHalfAngle() {
		return this.attackHalfAngle;
	}

	public float getDamageRatioAgainst(final CAttackType attackType, final CDefenseType defenseType) {
		return this.damageBonusTable[attackType.ordinal()][defenseType.ordinal()];
	}

	public float getMaxCollisionRadius() {
		return this.maxCollisionRadius;
	}

	public float getDecayTime() {
		return this.decayTime;
	}

	public float getBoneDecayTime() {
		return this.boneDecayTime;
	}

	public float getDissipateTime() {
		return this.dissipateTime;
	}

	public float getBulletDeathTime() {
		return this.bulletDeathTime;
	}

	public float getCloseEnoughRange() {
		return this.closeEnoughRange;
	}

	public float getGameDayHours() {
		return this.gameDayHours;
	}

	public float getGameDayLength() {
		return this.gameDayLength;
	}

	public float getDawnTimeGameHours() {
		return this.dawnTimeGameHours;
	}

	public float getDuskTimeGameHours() {
		return this.duskTimeGameHours;
	}

	public float getStructureDecayTime() {
		return this.structureDecayTime;
	}

	public float getBuildingAngle() {
		return this.buildingAngle;
	}

	public float getRootAngle() {
		return this.rootAngle;
	}

	public float getFogFlashTime() {
		return fogFlashTime;
	}

	public float getDyingRevealRadius() {
		return dyingRevealRadius;
	}

	public float getFoggedAttackRevealRadius() {
		return foggedAttackRevealRadius;
	}

	public float getDefenseArmor() {
		return this.defenseArmor;
	}

	public float getEtherealDamageBonusSpells() {
		return etherealDamageBonusSpells;
	}

	public float getEtherealDamageBonusMagic() {
		return etherealDamageBonusMagic;
	}

	public boolean isEtherealDamageBonusAlly() {
		return etherealDamageBonusAlly;
	}

	public boolean isMagicImmuneResistsDamage() {
		return magicImmuneResistsDamage;
	}

	public boolean isMagicImmuneResistsLeech() {
		return magicImmuneResistsLeech;
	}

	public boolean isMagicImmuneResistsThorns() {
		return magicImmuneResistsThorns;
	}

	public boolean isMagicImmuneResistsUltimates() {
		return magicImmuneResistsUltimates;
	}
	public boolean isDefendDeflection() {
		return defendDeflection;
	}

	public boolean isGlobalExperience() {
		return this.globalExperience;
	}

	public boolean isMaxLevelHeroesDrainExp() {
		return this.maxLevelHeroesDrainExp;
	}

	public boolean isBuildingKillsGiveExp() {
		return this.buildingKillsGiveExp;
	}

	public int getHeroAbilityLevelSkip() {
		return this.heroAbilityLevelSkip;
	}

	public int getHeroExpRange() {
		return this.heroExpRange;
	}

	public int getMaxHeroLevel() {
		return this.maxHeroLevel;
	}

	public int getMaxUnitLevel() {
		return this.maxUnitLevel;
	}

	public float getSummonedKillFactor() {
		return this.summonedKillFactor;
	}

	public float getStrAttackBonus() {
		return this.strAttackBonus;
	}

	public float getStrHitPointBonus() {
		return this.strHitPointBonus;
	}

	public float getStrRegenBonus() {
		return this.strRegenBonus;
	}

	public float getIntManaBonus() {
		return this.intManaBonus;
	}

	public float getIntRegenBonus() {
		return this.intRegenBonus;
	}

	public float getAgiDefenseBonus() {
		return this.agiDefenseBonus;
	}

	public float getAgiDefenseBase() {
		return this.agiDefenseBase;
	}

	public int getAgiMoveBonus() {
		return this.agiMoveBonus;
	}

	public float getAgiAttackSpeedBonus() {
		return this.agiAttackSpeedBonus;
	}

	public float getHeroFactorXp(final int level) {
		return getTableValue(this.heroFactorXp, level) / 100f;
	}

	public int getNeedHeroXP(final int level) {
		return getTableValue(this.needHeroXp, level);
	}

	public int getNeedHeroXPSum(final int level) {
		return getTableValue(this.needHeroXpSum, level);
	}

	public int getGrantHeroXP(final int level) {
		return getTableValue(this.grantHeroXp, level);
	}

	public int getGrantNormalXP(final int level) {
		return getTableValue(this.grantNormalXp, level);
	}

	public float getDropItemRange() {
		return this.dropItemRange;
	}

	public float getPickupItemRange() {
		return this.pickupItemRange;
	}

	public float getGiveItemRange() {
		return this.giveItemRange;
	}

	public float getPawnItemRange() {
		return this.pawnItemRange;
	}

	public float getPawnItemRate() {
		return this.pawnItemRate;
	}

	public float getFollowRange() {
		return this.followRange;
	}

	public float getStructureFollowRange() {
		return this.structureFollowRange;
	}

	public float getFollowItemRange() {
		return this.followItemRange;
	}

	public float getSpellCastRangeBuffer() {
		return this.spellCastRangeBuffer;
	}

	public int getHeroReviveGoldCost(final int originalCost, final int level) {
		final int goldRevivalCost = (int) (originalCost
				* (this.reviveBaseFactor + (this.reviveLevelFactor * (level - 1))));
		return Math.min(goldRevivalCost, (int) (originalCost * this.reviveMaxFactor));
	}

	public int getHeroReviveLumberCost(final int originalCost, final int level) {
		final int lumberRevivalCost = (int) (originalCost
				* (this.reviveBaseLumberFactor + (this.reviveLumberLevelFactor * (level - 1))));
		return Math.min(lumberRevivalCost, (int) (originalCost * this.reviveMaxFactor));
	}

	public int getHeroReviveTime(final int originalTime, final int level) {
		final int revivalTime = (int) (originalTime * level * this.reviveTimeFactor);
		return Math.min(revivalTime, (int) (originalTime * this.reviveMaxTimeFactor));
	}

	public float getHeroReviveLifeFactor() {
		return this.heroReviveLifeFactor;
	}

	public float getHeroReviveManaFactor() {
		return this.heroReviveManaFactor;
	}

	public float getHeroReviveManaStart() {
		return this.heroReviveManaStart;
	}

	public boolean isRelativeUpgradeCosts() {
		return this.relativeUpgradeCosts;
	}

	public float getMinUnitSpeed() {
		return minUnitSpeed;
	}

	public float getMaxUnitSpeed() {
		return maxUnitSpeed;
	}

	public float getMinBldgSpeed() {
		return minBldgSpeed;
	}

	public float getMaxBldgSpeed() {
		return maxBldgSpeed;
	}

	public float getChanceToMiss() {
		return chanceToMiss;
	}

	public float getMissDamageReduction() {
		return missDamageReduction;
	}

	private static int getTableValue(final int[] table, int level) {
		if (level <= 0) {
			return 0;
		}
		if (level > table.length) {
			level = table.length;
		}
		return table[level - 1];
	}

	/*
	 * This incorporates the function "f(x)" documented both on
	 * http://classic.battle.net/war3/basics/heroes.shtml and also on MiscGame.txt.
	 */
	private static int[] parseTable(final String txt, final int formulaA, final int formulaB, final int formulaC,
			final int tableSize) {
		final String[] splitTxt = txt.split(",");
		final int[] result = new int[tableSize];
		for (int i = 0; i < tableSize; i++) {
			if (i < splitTxt.length) {
				result[i] = Integer.parseInt(splitTxt[i]);
			}
			else {
				result[i] = (formulaA * result[i - 1]) + (formulaB * i) + formulaC;
			}
		}
		return result;
	}

	private static int[] parseIntArray(final String txt) {
		final String[] splitTxt = txt.split(",");
		final int[] result = new int[splitTxt.length];
		for (int i = 0; i < splitTxt.length; i++) {
			result[i] = Integer.parseInt(splitTxt[i]);
		}
		return result;
	}
}

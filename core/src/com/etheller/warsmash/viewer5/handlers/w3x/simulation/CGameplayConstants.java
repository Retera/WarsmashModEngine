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
	private final float bulletDeathTime;
	private final float closeEnoughRange;
	private final float dawnTimeGameHours;
	private final float duskTimeGameHours;
	private final float gameDayHours;
	private final float gameDayLength;
	private final float structureDecayTime;

	public CGameplayConstants(final DataTable parsedDataTable) {
		final Element miscData = parsedDataTable.get("Misc");
		// TODO use radians for half angle
		this.attackHalfAngle = (float) Math.toDegrees(miscData.getFieldFloatValue("AttackHalfAngle"));
		this.maxCollisionRadius = miscData.getFieldFloatValue("MaxCollisionRadius");
		this.decayTime = miscData.getFieldFloatValue("DecayTime");
		this.boneDecayTime = miscData.getFieldFloatValue("BoneDecayTime");
		this.structureDecayTime = miscData.getFieldFloatValue("StructureDecayTime");
		this.bulletDeathTime = miscData.getFieldFloatValue("BulletDeathTime");
		this.closeEnoughRange = miscData.getFieldFloatValue("CloseEnoughRange");

		this.dawnTimeGameHours = miscData.getFieldFloatValue("Dawn");
		this.duskTimeGameHours = miscData.getFieldFloatValue("Dusk");
		this.gameDayHours = miscData.getFieldFloatValue("DayHours");
		this.gameDayLength = miscData.getFieldFloatValue("DayLength");

		final CDefenseType[] defenseTypeOrder = { CDefenseType.SMALL, CDefenseType.MEDIUM, CDefenseType.LARGE,
				CDefenseType.FORT, CDefenseType.NORMAL, CDefenseType.HERO, CDefenseType.DIVINE, CDefenseType.NONE, };
		this.damageBonusTable = new float[CAttackType.values().length][defenseTypeOrder.length];
		for (int i = 0; i < CAttackType.VALUES.length; i++) {
			Arrays.fill(this.damageBonusTable[i], 1.0f);
			final CAttackType attackType = CAttackType.VALUES[i];
			final String damageBonus = miscData.getField("DamageBonus" + attackType.getDamageKey());
			final String[] damageComponents = damageBonus.split(",");
			for (int j = 0; j < damageComponents.length; j++) {
				if (damageComponents[j].length() > 0) {
					final CDefenseType defenseType = defenseTypeOrder[j];
					try {
						this.damageBonusTable[i][defenseType.ordinal()] = Float.parseFloat(damageComponents[j]);
//						System.out.println(attackType + ":" + defenseType + ": " + damageComponents[j]);
					}
					catch (final NumberFormatException e) {
						throw new RuntimeException("DamageBonus" + attackType.getDamageKey(), e);
					}
				}
			}
		}
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
}

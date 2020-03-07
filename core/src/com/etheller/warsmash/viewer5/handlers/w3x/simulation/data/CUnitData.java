package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityHoldPosition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityPatrol;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityStop;

public class CUnitData {
	private static final War3ID MANA_INITIAL_AMOUNT = War3ID.fromString("umpi");
	private static final War3ID MANA_MAXIMUM = War3ID.fromString("umpm");
	private static final War3ID HIT_POINT_MAXIMUM = War3ID.fromString("uhpm");
	private static final War3ID MOVEMENT_SPEED_BASE = War3ID.fromString("umvs");
	private static final War3ID PROPULSION_WINDOW = War3ID.fromString("uprw");
	private static final War3ID TURN_RATE = War3ID.fromString("umvr");
	private static final War3ID IS_BLDG = War3ID.fromString("ubdg");
	private static final War3ID NAME = War3ID.fromString("unam");
	private static final War3ID PROJECTILE_LAUNCH_X = War3ID.fromString("ulpx");
	private static final War3ID PROJECTILE_LAUNCH_Y = War3ID.fromString("ulpy");
	private static final War3ID PROJECTILE_LAUNCH_Z = War3ID.fromString("ulpz");
	private static final War3ID ATTACK1_DMG_BASE = War3ID.fromString("ua1b");
	private static final War3ID ATTACK1_DMG_DICE = War3ID.fromString("ua1d");
	private static final War3ID ATTACK1_DMG_SIDES_PER_DIE = War3ID.fromString("ua1s");
	private static final War3ID ATTACK1_PROJECTILE_SPEED = War3ID.fromString("ua1z");
	private static final War3ID ATTACK1_MISSILE_ART = War3ID.fromString("ua1m");
	private static final War3ID ATTACK1_PROJECTILE_ARC = War3ID.fromString("uma1");
	private static final War3ID ATTACK1_COOLDOWN = War3ID.fromString("ua1c");
	private static final War3ID ATTACK2_DMG_BASE = War3ID.fromString("ua2b");
	private static final War3ID ATTACK2_DMG_DICE = War3ID.fromString("ua2d");
	private static final War3ID ATTACK2_DMG_SIDES_PER_DIE = War3ID.fromString("ua2s");
	private static final War3ID ATTACK2_PROJECTILE_SPEED = War3ID.fromString("ua2z");
	private static final War3ID ATTACK2_MISSILE_ART = War3ID.fromString("ua2m");
	private static final War3ID ATTACK2_PROJECTILE_ARC = War3ID.fromString("uma2");
	private static final War3ID ATTACK2_COOLDOWN = War3ID.fromString("ua2c");
	private static final War3ID DEFENSE = War3ID.fromString("udef");
	private static final War3ID MOVE_HEIGHT = War3ID.fromString("umvh");
	private final MutableObjectData unitData;

	public CUnitData(final MutableObjectData unitData) {
		this.unitData = unitData;
	}

	public CUnit create(final CSimulation simulation, final int playerIndex, final int handleId, final War3ID typeId,
			final float x, final float y, final float facing) {
		final MutableGameObject unitType = this.unitData.get(typeId);
		final int life = unitType.getFieldAsInteger(HIT_POINT_MAXIMUM, 0);
		final int manaInitial = unitType.getFieldAsInteger(MANA_INITIAL_AMOUNT, 0);
		final int manaMaximum = unitType.getFieldAsInteger(MANA_MAXIMUM, 0);
		final int speed = unitType.getFieldAsInteger(MOVEMENT_SPEED_BASE, 0);
		final float moveHeight = unitType.getFieldAsFloat(MOVE_HEIGHT, 0);
		final CUnit unit = new CUnit(handleId, playerIndex, x, y, life, typeId, facing, manaInitial, life, manaMaximum,
				speed, moveHeight);
		if (speed > 0) {
			unit.add(simulation, CAbilityMove.INSTANCE);
			unit.add(simulation, CAbilityPatrol.INSTANCE);
			unit.add(simulation, CAbilityHoldPosition.INSTANCE);
			unit.add(simulation, CAbilityStop.INSTANCE);
		}
		final int dmgDice1 = unitType.getFieldAsInteger(ATTACK1_DMG_DICE, 0);
		final int dmgDice2 = unitType.getFieldAsInteger(ATTACK2_DMG_DICE, 0);
		if ((dmgDice1 != 0) || (dmgDice2 != 0)) {
			unit.add(simulation, CAbilityAttack.INSTANCE);
		}
		return unit;
	}

	public float getPropulsionWindow(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(PROPULSION_WINDOW, 0);
	}

	public float getTurnRate(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(TURN_RATE, 0);
	}

	public boolean isBuilding(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsBoolean(IS_BLDG, 0);
	}

	public String getName(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getName();
	}

	public int getA1MinDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_DMG_BASE, 0)
				+ this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_DMG_DICE, 0);
	}

	public int getA1MaxDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_DMG_BASE, 0)
				+ (this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_DMG_DICE, 0)
						* this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_DMG_SIDES_PER_DIE, 0));
	}

	public int getA2MinDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_DMG_BASE, 0)
				+ this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_DMG_DICE, 0);
	}

	public int getA2MaxDamage(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_DMG_BASE, 0)
				+ (this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_DMG_DICE, 0)
						* this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_DMG_SIDES_PER_DIE, 0));
	}

	public int getDefense(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(DEFENSE, 0);
	}

	public int getA1ProjectileSpeed(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK1_PROJECTILE_SPEED, 0);
	}

	public float getA1ProjectileArc(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(ATTACK1_PROJECTILE_ARC, 0);
	}

	public int getA2ProjectileSpeed(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsInteger(ATTACK2_PROJECTILE_SPEED, 0);
	}

	public float getA2ProjectileArc(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(ATTACK2_PROJECTILE_ARC, 0);
	}

	public String getA1MissileArt(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsString(ATTACK1_MISSILE_ART, 0);
	}

	public String getA2MissileArt(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsString(ATTACK2_MISSILE_ART, 0);
	}

	public float getA1Cooldown(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(ATTACK1_COOLDOWN, 0);
	}

	public float getA2Cooldown(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(ATTACK2_COOLDOWN, 0);
	}

	public float getProjectileLaunchX(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(PROJECTILE_LAUNCH_X, 0);
	}

	public float getProjectileLaunchY(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(PROJECTILE_LAUNCH_Y, 0);
	}

	public float getProjectileLaunchZ(final War3ID unitTypeId) {
		return this.unitData.get(unitTypeId).getFieldAsFloat(PROJECTILE_LAUNCH_Z, 0);
	}
}

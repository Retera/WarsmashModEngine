package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;

public class CAttackProjectile {
	private float x;
	private float y;
	private final float initialTargetX;
	private final float initialTargetY;
	private final float speed;
	private final AbilityTarget target;
	private boolean done;
	private final CUnit source;
	private final float damage;
	private final CUnitAttackMissile unitAttack;
	private final int bounceIndex;
	private final CUnitAttackListener attackListener;

	public CAttackProjectile(final float x, final float y, final float speed, final AbilityTarget target,
			final CUnit source, final float damage, final CUnitAttackMissile unitAttack, final int bounceIndex,
			final CUnitAttackListener attackListener) {
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.target = target;
		this.source = source;
		this.damage = damage;
		this.unitAttack = unitAttack;
		this.bounceIndex = bounceIndex;
		this.attackListener = attackListener;
		this.initialTargetX = target.getX();
		this.initialTargetY = target.getY();
	}

	public boolean update(final CSimulation cSimulation) {
		final float tx = getTargetX();
		final float ty = getTargetY();
		final float sx = this.x;
		final float sy = this.y;
		final float dtsx = tx - sx;
		final float dtsy = ty - sy;
		final float c = (float) Math.sqrt((dtsx * dtsx) + (dtsy * dtsy));

		final float d1x = dtsx / c;
		final float d1y = dtsy / c;

		float travelDistance = Math.min(c, this.speed * WarsmashConstants.SIMULATION_STEP_TIME);
		final boolean done = c <= travelDistance;
		if (done) {
			travelDistance = c;
		}

		final float dx = d1x * travelDistance;
		final float dy = d1y * travelDistance;

		this.x = this.x + dx;
		this.y = this.y + dy;

		if (done && !this.done) {
			this.unitAttack.doDamage(cSimulation, this.source, this.target, this.damage, this.x, this.y,
					this.bounceIndex, this.attackListener);
			this.done = true;
		}
		return this.done;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getSpeed() {
		return this.speed;
	}

	public AbilityTarget getTarget() {
		return this.target;
	}

	public boolean isDone() {
		return this.done;
	}

	public CUnitAttackMissile getUnitAttack() {
		return this.unitAttack;
	}

	public float getTargetX() {
		if (this.unitAttack.isProjectileHomingEnabled() && (this.unitAttack.getWeaponType() != CWeaponType.ARTILLERY)) {
			return this.target.getX();
		}
		else {
			return this.initialTargetX;
		}
	}

	public float getTargetY() {
		if (this.unitAttack.isProjectileHomingEnabled() && (this.unitAttack.getWeaponType() != CWeaponType.ARTILLERY)) {
			return this.target.getY();
		}
		else {
			return this.initialTargetY;
		}
	}
}

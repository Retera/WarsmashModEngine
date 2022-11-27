package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;

public abstract class CProjectile {
	private float x;
	private float y;
	private final float initialTargetX;
	private final float initialTargetY;
	private final float speed;
	private final AbilityTarget target;
	private boolean homingEnabled;
	private boolean done;
	private final CUnit source;

	public CProjectile(final float x, final float y, final float speed, final AbilityTarget target, boolean homingEnabled,
			final CUnit source) {
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.target = target;
		this.homingEnabled = homingEnabled;
		this.source = source;
		this.initialTargetX = target.getX();
		this.initialTargetY = target.getY();
	}

	public boolean update(final CSimulation game) {
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
			this.onHitTarget(game);
			this.done = true;
		}
		return this.done;
	}
	
	protected abstract void onHitTarget(CSimulation game);

	public final float getX() {
		return this.x;
	}

	public final float getY() {
		return this.y;
	}

	public final float getSpeed() {
		return this.speed;
	}
	
	public final CUnit getSource() {
		return source;
	}

	public final AbilityTarget getTarget() {
		return this.target;
	}

	public final boolean isDone() {
		return this.done;
	}

	public final float getTargetX() {
		if (homingEnabled) {
			return this.target.getX();
		}
		else {
			return this.initialTargetX;
		}
	}

	public final float getTargetY() {
		if (homingEnabled) {
			return this.target.getY();
		}
		else {
			return this.initialTargetY;
		}
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.projectile;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public class CAttackProjectile {
	private float x;
	private float y;
	private final float speed;
	private final CWidget target;
	private boolean done;
	private final CUnit source;
	private final int damage;

	public CAttackProjectile(final float x, final float y, final float speed, final CWidget target, final CUnit source,
			final int damage) {
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.target = target;
		this.source = source;
		this.damage = damage;
	}

	public boolean update(final CSimulation cSimulation) {
		final float tx = this.target.getX();
		final float ty = this.target.getY();
		final float sx = this.x;
		final float sy = this.y;
		final float dtsx = tx - sx;
		final float dtsy = ty - sy;
		final float c = (float) Math.sqrt((dtsx * dtsx) + (dtsy * dtsy));

		final float d1x = dtsx / c;
		final float d1y = dtsy / c;

		float travelDistance = Math.min(c, this.speed * WarsmashConstants.SIMULATION_STEP_TIME);
		if (c <= travelDistance) {
			if (!this.done) {
				this.target.damage(this.source, this.damage);
			}
			this.done = true;
			travelDistance = c;
		}

		final float dx = d1x * travelDistance;
		final float dy = d1y * travelDistance;

		this.x = this.x + dx;
		this.y = this.y + dy;

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

	public CWidget getTarget() {
		return this.target;
	}

	public boolean isDone() {
		return this.done;
	}
}

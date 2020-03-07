package com.etheller.warsmash.viewer5.handlers.w3x.simulation.projectile;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public class CAttackProjectile {
	private float x;
	private float y;
	private float z;
	private final float startingHeight;
	private final float speed;
	private final CWidget target;
	private final float halfStartingDistance;
	private final float arcPeakHeight;
	private float totalTravelDistance;
	private boolean done;
	private final CUnit source;
	private final int damage;
	private float arcCurrentHeight;

	public CAttackProjectile(final float x, final float y, final float z, final float speed, final float arc,
			final CWidget target, final CUnit source, final int damage) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.startingHeight = z;
		this.speed = speed;
		this.target = target;
		final float dx = target.getX() - x;
		final float dy = target.getY() - y;
		final float startingDistance = (float) Math.sqrt((dx * dx) + (dy * dy));
		this.halfStartingDistance = startingDistance / 2f;
		this.arcPeakHeight = arc * startingDistance;
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
		final float dtsz = this.target.getFlyHeight() - this.startingHeight;
		final float c = (float) Math.sqrt((dtsx * dtsx) + (dtsy * dtsy));

		final float d1x = dtsx / c;
		final float d1y = dtsy / c;
		final float d1z = dtsz / (this.halfStartingDistance * 2);

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
		this.totalTravelDistance += travelDistance;
		final float dz = d1z * this.totalTravelDistance;

		this.x = this.x + dx;
		this.y = this.y + dy;

		final float distanceToPeak = this.totalTravelDistance - this.halfStartingDistance;
		final float normPeakDist = distanceToPeak / this.halfStartingDistance;
		final float currentHeightPercentage = 1 - (normPeakDist * normPeakDist);
		this.arcCurrentHeight = currentHeightPercentage * this.arcPeakHeight;
		this.z = this.startingHeight + dz + this.arcCurrentHeight;

		return this.done;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
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

	public float getArcCurrentHeight() {
		return this.arcCurrentHeight;
	}
}

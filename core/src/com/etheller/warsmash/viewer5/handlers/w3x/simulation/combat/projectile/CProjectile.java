package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import com.etheller.interpreter.ast.util.CExtensibleHandleAbstract;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public abstract class CProjectile extends CExtensibleHandleAbstract implements CEffect {
	protected float x;
	protected float y;
	private float initialTargetX;
	private float initialTargetY;
	private final float speed;
	private AbilityTarget target;
	private final boolean homingEnabled;
	protected boolean done;
	private final CUnit source;
	private boolean reflected = false;

	public CProjectile(final float x, final float y, final float speed, final AbilityTarget target,
			final boolean homingEnabled, final CUnit source) {
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.target = target;
		this.homingEnabled = homingEnabled;
		this.source = source;
		this.initialTargetX = target.getX();
		this.initialTargetY = target.getY();
	}

	@Override
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
			this.done = true;
			this.onHitTarget(game);
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
		return this.source;
	}

	public final AbilityTarget getTarget() {
		return this.target;
	}

	public void setTarget(final AbilityTarget target) {
		this.target = target;
		this.initialTargetX = target.getX();
		this.initialTargetY = target.getY();
	}

	public final boolean isDone() {
		return this.done;
	}

	public void setDone(final boolean done) {
		this.done = done;
	}

	public final float getTargetX() {
		if (this.homingEnabled) {
			return this.target.getX();
		}
		else {
			return this.initialTargetX;
		}
	}

	public final float getTargetY() {
		if (this.homingEnabled) {
			return this.target.getY();
		}
		else {
			return this.initialTargetY;
		}
	}

	public boolean isReflected() {
		return this.reflected;
	}

	public void setReflected(final boolean reflected) {
		this.reflected = reflected;
	}
}

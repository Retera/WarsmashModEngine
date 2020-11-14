package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public abstract class CWidget {
	private final int handleId;
	private float x;
	private float y;
	protected float life;

	public CWidget(final int handleId, final float x, final float y, final float life) {
		this.handleId = handleId;
		this.x = x;
		this.y = y;
		this.life = life;
	}

	public int getHandleId() {
		return this.handleId;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getLife() {
		return this.life;
	}

	protected void setX(final float x) {
		this.x = x;
	}

	protected void setY(final float y) {
		this.y = y;
	}

	public void setLife(final CSimulation simulation, final float life) {
		this.life = life;
	}

	public abstract void damage(final CSimulation simulation, final CUnit source, final CAttackType attackType,
			final String weaponType, final float damage);

	public abstract float getFlyHeight();

	public abstract float getImpactZ();

	public boolean isDead() {
		return this.life <= 0;
	}

	public abstract boolean canBeTargetedBy(CSimulation simulation, CUnit source,
			final EnumSet<CTargetType> targetsAllowed);
}

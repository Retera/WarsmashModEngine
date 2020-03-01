package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;

public class CUnit extends CWidget {
	private War3ID typeId;
	private float facing; // degrees
	private float mana;
	private int maximumLife;
	private int maximumMana;
	private int speed;
	private int cooldownEndTime = 0;
	private float flyHeight;

	private final List<CAbility> abilities = new ArrayList<>();

	private COrder currentOrder;
	private final Queue<COrder> orderQueue = new LinkedList<>();

	public CUnit(final int handleId, final float x, final float y, final float life, final War3ID typeId,
			final float facing, final float mana, final int maximumLife, final int maximumMana, final int speed,
			final float defaultFlyingHeight) {
		super(handleId, x, y, life);
		this.typeId = typeId;
		this.facing = facing;
		this.mana = mana;
		this.maximumLife = maximumLife;
		this.maximumMana = maximumMana;
		this.speed = speed;
		this.flyHeight = defaultFlyingHeight;
	}

	public void add(final CSimulation simulation, final CAbility ability) {
		this.abilities.add(ability);
		ability.onAdd(simulation, this);
	}

	public War3ID getTypeId() {
		return this.typeId;
	}

	/**
	 * @return facing in DEGREES
	 */
	public float getFacing() {
		return this.facing;
	}

	public float getMana() {
		return this.mana;
	}

	public int getMaximumLife() {
		return this.maximumLife;
	}

	public int getMaximumMana() {
		return this.maximumMana;
	}

	public void setTypeId(final War3ID typeId) {
		this.typeId = typeId;
	}

	public void setFacing(final float facing) {
		// java modulo output can be negative, but not if we
		// force positive and modulo again
		this.facing = ((facing % 360) + 360) % 360;
	}

	public void setMana(final float mana) {
		this.mana = mana;
	}

	public void setMaximumLife(final int maximumLife) {
		this.maximumLife = maximumLife;
	}

	public void setMaximumMana(final int maximumMana) {
		this.maximumMana = maximumMana;
	}

	public void setSpeed(final int speed) {
		this.speed = speed;
	}

	public int getSpeed() {
		return this.speed;
	}

	/**
	 * Updates one tick of simulation logic.
	 */
	public void update(final CSimulation game) {
		if (this.currentOrder != null) {
			if (this.currentOrder.update(game)) {
				// remove current order, because it's completed, polling next
				// item from order queue
				this.currentOrder = this.orderQueue.poll();
			}
		}
	}

	public void order(final COrder order, final boolean queue) {
		if (queue && (this.currentOrder != null)) {
			this.orderQueue.add(order);
		}
		else {
			this.currentOrder = order;
		}
	}

	public COrder getCurrentOrder() {
		return this.currentOrder;
	}

	public List<CAbility> getAbilities() {
		return this.abilities;
	}

	public void setCooldownEndTime(final int cooldownEndTime) {
		this.cooldownEndTime = cooldownEndTime;
	}

	public int getCooldownEndTime() {
		return this.cooldownEndTime;
	}

	public float getFlyHeight() {
		return this.flyHeight;
	}

	public void setFlyHeight(final float flyHeight) {
		this.flyHeight = flyHeight;
	}

}

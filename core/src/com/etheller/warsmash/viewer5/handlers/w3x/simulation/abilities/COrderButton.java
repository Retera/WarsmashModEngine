package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

import com.etheller.interpreter.ast.util.CExtensibleHandleAbstract;

public class COrderButton extends CExtensibleHandleAbstract {
	private static final int INFINITE_CHARGES = -1;

	private int orderId;
	private int autoCastOrderId;
	private int autoCastUnOrderId;
	private int containerMenuOrderId;
	private boolean disabled;
	private int manaCost;
	private int goldCost;
	private int lumberCost;
	private int charges = INFINITE_CHARGES;
	private int foodCost;
	private JassOrderButtonType type;
	private boolean autoCastActive;

	public COrderButton(final int orderId) {
		this.orderId = orderId;
	}

	public int getOrderId() {
		return this.orderId;
	}

	public void setOrderId(final int orderId) {
		this.orderId = orderId;
	}

	public int getAutoCastOrderId() {
		return this.autoCastOrderId;
	}

	public void setAutoCastOrderId(final int autoCastOrderId) {
		this.autoCastOrderId = autoCastOrderId;
	}

	public int getAutoCastUnOrderId() {
		return this.autoCastUnOrderId;
	}

	public void setAutoCastUnOrderId(final int autoCastUnOrderId) {
		this.autoCastUnOrderId = autoCastUnOrderId;
	}

	public int getContainerMenuOrderId() {
		return this.containerMenuOrderId;
	}

	public void setContainerMenuOrderId(final int containerMenuOrderId) {
		this.containerMenuOrderId = containerMenuOrderId;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	public int getManaCost() {
		return this.manaCost;
	}

	public void setManaCost(final int manaCost) {
		this.manaCost = manaCost;
	}

	public int getGoldCost() {
		return this.goldCost;
	}

	public void setGoldCost(final int goldCost) {
		this.goldCost = goldCost;
	}

	public int getLumberCost() {
		return this.lumberCost;
	}

	public void setLumberCost(final int lumberCost) {
		this.lumberCost = lumberCost;
	}

	public int getCharges() {
		return this.charges;
	}

	public void setCharges(final int charges) {
		this.charges = charges;
	}

	public int getFoodCost() {
		return this.foodCost;
	}

	public void setFoodCost(final int foodCost) {
		this.foodCost = foodCost;
	}

	public JassOrderButtonType getType() {
		return this.type;
	}

	public void setType(final JassOrderButtonType type) {
		this.type = type;
	}

	public boolean isAutoCastActive() {
		return this.autoCastActive;
	}

	public void setAutoCastActive(final boolean autoCastActive) {
		this.autoCastActive = autoCastActive;
	}

	public static enum JassOrderButtonType {
		INSTANT_NO_TARGET,
		UNIT_TARGET,
		POINT_TARGET,
		UNIT_OR_POINT_TARGET,
		INSTANT_NO_TARGET_NO_INTERRUPT,
		PASSIVE,
		MENU;

		public static JassOrderButtonType[] VALUES = values();
	}
}

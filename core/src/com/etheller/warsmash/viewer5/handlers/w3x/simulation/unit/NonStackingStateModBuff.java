package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

public class NonStackingStateModBuff {
	public static final String ALLOW_STACKING_KEY = "STACK";
	
	private StateModBuffType buffType;
	private String stackingKey;
	private float value;
	
	public NonStackingStateModBuff(StateModBuffType buffType, String stackingKey, float value) {
		super();
		this.buffType = buffType;
		this.stackingKey = stackingKey;
		this.value = value;
	}
	
	public StateModBuffType getBuffType() {
		return buffType;
	}
	public void setBuffType(StateModBuffType buffType) {
		this.buffType = buffType;
	}
	public String getStackingKey() {
		return stackingKey;
	}
	public void setStackingKey(String stackingKey) {
		this.stackingKey = stackingKey;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
}

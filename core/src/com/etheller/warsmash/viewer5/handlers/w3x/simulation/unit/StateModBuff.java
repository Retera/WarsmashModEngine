package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

public class StateModBuff {
	
	private StateModBuffType buffType;
	private float value;
	
	public StateModBuff(StateModBuffType buffType, float value) {
		super();
		this.buffType = buffType;
		this.value = value;
	}
	
	public StateModBuffType getBuffType() {
		return buffType;
	}
	public void setBuffType(StateModBuffType buffType) {
		this.buffType = buffType;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
}

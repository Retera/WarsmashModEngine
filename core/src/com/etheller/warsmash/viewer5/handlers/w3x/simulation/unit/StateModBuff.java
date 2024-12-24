package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

public class StateModBuff {
	
	private StateModBuffType buffType;
	private long value;
	
	public StateModBuff(StateModBuffType buffType, long value) {
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
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
}

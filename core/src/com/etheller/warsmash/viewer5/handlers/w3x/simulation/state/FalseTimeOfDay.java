package com.etheller.warsmash.viewer5.handlers.w3x.simulation.state;

public class FalseTimeOfDay {
	int hour;
	int minute;
	int ticksRemaining;
	boolean initialized = false;
	
	public FalseTimeOfDay(int hour, int minute, int ticksRemaining) {
		this.hour = hour;
		this.minute = minute;
		this.ticksRemaining = ticksRemaining;
	}
	
	public boolean tick() {
		this.initialized = true;
		this.ticksRemaining--;
		return this.ticksRemaining>0;
	}
	
	public boolean isInitialized() {
		return this.initialized;
	}

	public float getTimeOfDay() {
		return (float) (this.hour + (this.minute / 60.0));
	}

	public void setTimeOfDay(float value) {
		this.hour = (int) value;
		this.minute = (int) ((value - (int)value) * 60);
	}
}

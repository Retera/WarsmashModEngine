package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.interpreter.ast.util.CHandle;

public class NonStackingStatBuff implements CHandle {
	public static final String ALLOW_STACKING_KEY = "STACK";

	private NonStackingStatBuffType buffType;
	private String stackingKey;
	private float value;

	public NonStackingStatBuff(final NonStackingStatBuffType buffType, final String stackingKey, final float value) {
		super();
		this.buffType = buffType;
		this.stackingKey = stackingKey;
		this.value = value;
	}

	public NonStackingStatBuffType getBuffType() {
		return this.buffType;
	}

	public void setBuffType(final NonStackingStatBuffType buffType) {
		this.buffType = buffType;
	}

	public String getStackingKey() {
		return this.stackingKey;
	}

	public void setStackingKey(final String stackingKey) {
		this.stackingKey = stackingKey;
	}

	public float getValue() {
		return this.value;
	}

	public void setValue(final float value) {
		this.value = value;
	}

	@Override
	public int getHandleId() {
		// NOTE: these are all zero right now, so jass doesnt crash, but it's useless
		return 0;
	}
}

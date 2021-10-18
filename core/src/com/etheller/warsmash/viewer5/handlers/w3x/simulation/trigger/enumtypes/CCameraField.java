package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CCameraField implements CHandle {
	TARGET_DISTANCE,
	FARZ,
	ANGLE_OF_ATTACK,
	FIELD_OF_VIEW,
	ROLL,
	ROTATION,
	ZOFFSET,
	NEARZ, // 1.32
	LOCAL_PITCH,
	LOCAL_YAW,
	LOCAL_ROLL;

	public static CCameraField[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}

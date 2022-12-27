package com.etheller.warsmash.parsers.jass.triggers;

import java.util.ArrayList;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class UnitGroup extends ArrayList<CUnit> implements CHandle {
	private int handleId;

	public UnitGroup(int handleId) {
		this.handleId = handleId;
	}

	@Override
	public int getHandleId() {
		return handleId;
	}

}

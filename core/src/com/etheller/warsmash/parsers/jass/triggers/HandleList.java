package com.etheller.warsmash.parsers.jass.triggers;

import java.util.ArrayList;

import com.etheller.interpreter.ast.util.CHandle;

public class HandleList extends ArrayList<CHandle> implements CHandle {
	private final int handleId;

	public HandleList(final int handleId) {
		this.handleId = handleId;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

}

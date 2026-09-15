package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.interpreter.ast.util.CHandle;

import java.util.HashMap;
import java.util.Map;

public class CHashtable implements CHandle {
	private final int handleId;
	private final Map<Integer, Map<Integer, Object>> parentKeyToChildTable = new HashMap<>();

	public CHashtable(final int handleId) {
		this.handleId = handleId;
	}

	@Override
	public int getHandleId() {
		return handleId;
	}

	public void save(final Integer parentKey, final Integer childKey, final Object object) {
		Map<Integer, Object> childTable = this.parentKeyToChildTable.get(parentKey);
		if (childTable == null) {
			childTable = new HashMap<>();
			this.parentKeyToChildTable.put(parentKey, childTable);
		}
		if (object == null) {
			childTable.remove(childKey);
		}
		else {
			childTable.put(childKey, object);
		}
	}

	public Object load(final Integer parentKey, final Integer childKey) {
		final Map<Integer, Object> childTable = this.parentKeyToChildTable.get(parentKey);
		if (childTable != null) {
			return childTable.get(childKey);
		}
		return null;
	}
}

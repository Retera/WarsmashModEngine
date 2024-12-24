package com.etheller.warsmash.units;

import java.util.Set;

import com.etheller.warsmash.util.War3ID;

public interface ObjectData {
	GameObject get(String id);

	default GameObject get(final War3ID id) {
		return get(id.asStringValue());
	}

	void cloneUnit(final String parentId, final String cloneId);

	void inheritFrom(String childKey, String parentKey);

	void setValue(String slk, String id, String field, String value);

	Set<String> keySet();

	String getLocalizedString(String key);
}

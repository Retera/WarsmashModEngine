package com.etheller.warsmash.units.custom;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.etheller.warsmash.util.War3ID;

public final class ObjectMap implements Iterable<Map.Entry<War3ID, ObjectDataChangeEntry>> {
	private final Map<War3ID, ObjectDataChangeEntry> idToDataChangeEntry;
	private final Set<War3ID> lowerCaseKeySet;

	public ObjectMap() {
		this.idToDataChangeEntry = new LinkedHashMap<>();
		this.lowerCaseKeySet = new HashSet<>();
	}

	public void clear() {
		this.idToDataChangeEntry.clear();
		this.lowerCaseKeySet.clear();
	}

	public ObjectDataChangeEntry remove(final War3ID key) {
		this.lowerCaseKeySet.remove(War3ID.fromString(key.toString().toLowerCase()));
		return this.idToDataChangeEntry.remove(key);
	}

	public Set<War3ID> keySet() {
		return this.idToDataChangeEntry.keySet();
	}

	public ObjectDataChangeEntry put(final War3ID key, final ObjectDataChangeEntry value) {
		this.lowerCaseKeySet.add(War3ID.fromString(key.toString().toLowerCase()));
		return this.idToDataChangeEntry.put(key, value);
	}

	public Set<Map.Entry<War3ID, ObjectDataChangeEntry>> entrySet() {
		return this.idToDataChangeEntry.entrySet();
	}

	public ObjectDataChangeEntry get(final War3ID key) {
		return this.idToDataChangeEntry.get(key);
	}

	public boolean containsKey(final War3ID key) {
		return this.idToDataChangeEntry.containsKey(key);
	}

	public boolean containsKeyCaseInsensitive(final War3ID key) {
		return this.lowerCaseKeySet.contains(War3ID.fromString(key.toString().toLowerCase()));
	}

	public boolean containsValue(final ObjectDataChangeEntry value) {
		return this.idToDataChangeEntry.containsValue(value);
	}

	public Collection<ObjectDataChangeEntry> values() {
		return this.idToDataChangeEntry.values();
	}

	public int size() {
		return this.idToDataChangeEntry.size();
	}

	public void forEach(final BiConsumer<? super War3ID, ? super ObjectDataChangeEntry> forEach) {
		this.idToDataChangeEntry.forEach(forEach);
	}

	@Override
	public Iterator<Map.Entry<War3ID, ObjectDataChangeEntry>> iterator() {
		return this.idToDataChangeEntry.entrySet().iterator();
	}

	@Override
	public ObjectMap clone() {
		final ObjectMap clone = new ObjectMap();
		forEach(new BiConsumer<War3ID, ObjectDataChangeEntry>() {
			@Override
			public void accept(final War3ID key, final ObjectDataChangeEntry value) {
				clone.put(key, value);
			}
		});
		return clone;
	}
}

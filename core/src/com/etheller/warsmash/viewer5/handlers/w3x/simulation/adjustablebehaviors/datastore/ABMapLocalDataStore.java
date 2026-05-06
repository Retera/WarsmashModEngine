package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public class ABMapLocalDataStore extends ABLocalDataStore {
	private Map<String, Object> store;

	public ABMapLocalDataStore() {
		this.store = new HashMap<>();
	}

	public ABMapLocalDataStore(CSimulation game) {
		this.store = new HashMap<>();
		this.game = game;
	}

	@Override
	public boolean containsKey(String key) {
		return store.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return store.containsValue(value);
	}

	@Override
	public Object get(String key) {
		return store.get(key);
	}

	@Override
	public Object getOrDefault(String key, Object def) {
		return store.getOrDefault(key, def);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> D) {
		Object val = store.get(key);
		if (val != null && D.isAssignableFrom(val.getClass())) {
			return (T) val;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getOrDefault(String key, T def, Class<T> D) {
		Object val = store.getOrDefault(key, def);
		if (val != null && D.isAssignableFrom(val.getClass())) {
			return (T) val;
		}
		return null;
	}

	@Override
	public int getInt(String key) {
		Object val = store.get(key);
		if (val != null && val instanceof Integer) {
			return (int) val;
		}
		return 0;
	}

	@Override
	public int getIntOrDefault(String key, int def) {
		Object val = store.getOrDefault(key, def);
		if (val != null && val instanceof Integer) {
			return (int) val;
		}
		return 0;
	}

	@Override
	public boolean getBoolean(String key) {
		Object val = store.get(key);
		if (val != null && val instanceof Boolean) {
			return (boolean) val;
		}
		return false;
	}

	@Override
	public boolean getBooleanOrDefault(String key, boolean def) {
		Object val = store.getOrDefault(key, def);
		if (val != null && val instanceof Boolean) {
			return (boolean) val;
		}
		return false;
	}

	@Override
	public float getFloat(String key) {
		Object val = store.get(key);
		if (val != null && val instanceof Float) {
			return (float) val;
		}
		return 0f;
	}

	@Override
	public float getFloatOrDefault(String key, float def) {
		Object val = store.getOrDefault(key, def);
		if (val != null && val instanceof Float) {
			return (float) val;
		}
		return 0f;
	}

	@Override
	public Object put(String key, Object value) {
		return store.put(key, value);
	}

	@Override
	public Object remove(String key) {
		return store.remove(key);
	}

	@Override
	public void clear() {
		store.clear();
	}

	@Override
	public Set<String> keySet() {
		return store.keySet();
	}

	@Override
	public Collection<Object> values() {
		return store.values();
	}
}

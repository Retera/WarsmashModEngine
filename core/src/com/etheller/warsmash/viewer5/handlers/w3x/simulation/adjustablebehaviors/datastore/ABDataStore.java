package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore;

import java.util.Collection;
import java.util.Set;

public interface ABDataStore {

	public boolean containsKey(String key);

	public boolean containsValue(Object value);

	public Object get(String key);

	public Object getOrDefault(String key, Object def);

	public <T> T get(String key, Class<T> D);

	public <T> T getOrDefault(String key, T def, Class<T> D);

	public Object put(String key, Object value);

	public Object remove(String key);

	public void clear();

	public Set<String> keySet();

	public Collection<Object> values();

	int getInt(String key);

	int getIntOrDefault(String key, int def);

	boolean getBoolean(String key);

	boolean getBooleanOrDefault(String key, boolean def);

	float getFloat(String key);

	float getFloatOrDefault(String key, float def);

}

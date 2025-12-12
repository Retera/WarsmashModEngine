package com.etheller.warsmash.desktop.launcher.emulator;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.util.StringBundle;

public class HardcodedStringBundle implements StringBundle {
	private final Map<String, String> keyToString = new HashMap<>();
	private final Map<String, String> upperKeyToString = new HashMap<>();

	public void putString(final String key, final String value) {
		this.keyToString.put(key, value);
		this.upperKeyToString.put(key.toUpperCase(), value);
	}

	@Override
	public String getString(final String string) {
		return this.upperKeyToString.get(string.toUpperCase());
	}

	@Override
	public String getStringCaseSensitive(final String key) {
		return this.keyToString.get(key);
	}

}

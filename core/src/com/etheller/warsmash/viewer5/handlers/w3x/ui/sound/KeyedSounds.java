package com.etheller.warsmash.viewer5.handlers.w3x.ui.sound;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;

public class KeyedSounds {
	private final DataTable uiSoundsTable;
	private final DataSource dataSource;
	private final Map<String, UnitSound> keyToSound;

	public KeyedSounds(final DataTable uiSoundsTable, final DataSource dataSource) {
		this.uiSoundsTable = uiSoundsTable;
		this.dataSource = dataSource;
		this.keyToSound = new HashMap<>();
	}

	public UnitSound getSound(final String key) {
		UnitSound sound = this.keyToSound.get(key);
		if (sound == null) {
			sound = UnitSound.create(this.dataSource, this.uiSoundsTable, key, "");
			this.keyToSound.put(key, sound);
		}
		return sound;
	}
}

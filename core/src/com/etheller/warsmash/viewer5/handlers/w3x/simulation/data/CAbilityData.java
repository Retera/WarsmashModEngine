package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.War3ID;

public class CAbilityData {
	private static final War3ID ABILITY_ICON = War3ID.fromString("aart");
	private final MutableObjectData abilityData;

	public CAbilityData(final MutableObjectData abilityData) {
		this.abilityData = abilityData;
	}

	public String getIconPath(final War3ID id, final int level) {
		final MutableGameObject mutableGameObject = this.abilityData.get(id);
		if (mutableGameObject == null) {
			return ImageUtils.DEFAULT_ICON_PATH;
		}
		final String iconPath = mutableGameObject.getFieldAsString(ABILITY_ICON, level);
		if ((iconPath == null) || "".equals(iconPath)) {
			return ImageUtils.DEFAULT_ICON_PATH;
		}
		return iconPath;
	}
}

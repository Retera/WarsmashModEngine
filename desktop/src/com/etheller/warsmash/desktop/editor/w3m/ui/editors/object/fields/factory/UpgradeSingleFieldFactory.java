package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public class UpgradeSingleFieldFactory extends AbstractSingleFieldFactory {
	private final Map<String, GameObject> effectIDToUpgradeEffect = new HashMap<>();

	public UpgradeSingleFieldFactory(final ObjectData upgradeEffectMetaData) {
		for (final String notEffectId : upgradeEffectMetaData.keySet()) {
			final GameObject upgradeEffect = upgradeEffectMetaData.get(notEffectId);
			this.effectIDToUpgradeEffect.put(upgradeEffect.getField("effectID") + upgradeEffect.getField("dataType"),
					upgradeEffect);
		}
	}

	@Override
	protected String getDisplayName(final ObjectData metaData, final WorldEditStrings worldEditStrings,
			final War3ID metaKey, final int level, final MutableGameObject gameObject) {
		final String defaultDisplayName = LevelsSingleFieldFactory.INSTANCE.getDisplayName(metaData, worldEditStrings,
				metaKey, level, gameObject);
		final GameObject metaDataField = metaData.get(metaKey.toString());
		final String effectType = metaDataField.getField("effectType");
		if ("Base".equalsIgnoreCase(effectType) || "Mod".equalsIgnoreCase(effectType)
				|| "Code".equalsIgnoreCase(effectType)) {
			final GameObject upgradeEffect = this.effectIDToUpgradeEffect
					.get(gameObject.getFieldAsString(War3ID.fromString("gef" + metaDataField.getId().charAt(3)), 0)
							+ metaDataField.getField("effectType"));
			final String displayNameOfSubMetaField = upgradeEffect == null ? "WESTRING_ERROR_BADTRIGVAL"
					: upgradeEffect.getField("displayName");
			return String.format(defaultDisplayName, worldEditStrings.getString(displayNameOfSubMetaField));
		}
		return defaultDisplayName;
	}

	@Override
	protected String getDisplayPrefix(final ObjectData metaData, final WorldEditStrings worldEditStrings,
			final War3ID metaKey, final int level, final MutableGameObject gameObject) {
		return LevelsSingleFieldFactory.INSTANCE.getDisplayPrefix(metaData, worldEditStrings, metaKey, level,
				gameObject);
	}

}

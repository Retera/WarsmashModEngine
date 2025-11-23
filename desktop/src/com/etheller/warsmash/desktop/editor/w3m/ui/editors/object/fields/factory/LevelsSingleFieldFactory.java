package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.EditableOnscreenObjectField;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public final class LevelsSingleFieldFactory extends AbstractSingleFieldFactory {
	public static final LevelsSingleFieldFactory INSTANCE = new LevelsSingleFieldFactory();

	@Override
	protected String getDisplayName(final ObjectData metaData, final WorldEditStrings worldEditStrings,
			final War3ID metaKey, final int level, final MutableGameObject gameObject) {
		final GameObject metaDataFieldObject = metaData.get(metaKey.toString());
		final String prefix = EditableOnscreenObjectField.categoryName(metaDataFieldObject.getField("category"),
				worldEditStrings) + " - ";
		return prefix + worldEditStrings.getString(metaDataFieldObject.getField("displayName"));
	}

	@Override
	protected String getDisplayPrefix(final ObjectData metaData, final WorldEditStrings worldEditStrings,
			final War3ID metaKey, final int level, final MutableGameObject gameObject) {
		String prefix = "";
		if (level > 0) {
			prefix = String.format(worldEditStrings.getString("WESTRING_AEVAL_LVL"), level) + " - " + prefix;
		}
		return prefix;
	}
}

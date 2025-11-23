package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.BooleanObjectField;
import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.EditableOnscreenObjectField;
import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.FloatObjectField;
import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.GameEnumObjectField;
import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.IntegerObjectField;
import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.StringObjectField;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public abstract class AbstractSingleFieldFactory implements SingleFieldFactory {
	@Override
	public final EditableOnscreenObjectField create(final MutableGameObject gameObject, final ObjectData metaData,
			final WorldEditStrings worldEditStrings, final DataTable unitEditorData, final War3ID metaKey,
			final int level, final WorldEditorDataType worldEditorDataType, final boolean hasMoreThanOneLevel) {
		final GameObject metaField = metaData.get(metaKey.toString());

		final String displayName = getDisplayName(metaData, worldEditStrings, metaKey, hasMoreThanOneLevel ? level : 0,
				gameObject);
		final String displayPrefix = getDisplayPrefix(metaData, worldEditStrings, metaKey,
				hasMoreThanOneLevel ? level : 0, gameObject);
		final String rawDataName = getRawDataName(metaData, metaKey, hasMoreThanOneLevel ? level : 0);
		final String metaDataType = metaField.getField("type");
		switch (metaDataType) {
		case "attackBits":
		case "teamColor":
		case "deathType":
		case "versionFlags":
		case "channelFlags":
		case "channelType":
		case "int":
			return new IntegerObjectField(displayPrefix + displayName, displayName, rawDataName, hasMoreThanOneLevel,
					metaKey, level, worldEditorDataType, metaField);
		case "real":
		case "unreal":
			return new FloatObjectField(displayPrefix + displayName, displayName, rawDataName, hasMoreThanOneLevel,
					metaKey, level, worldEditorDataType, metaField);
		case "bool":
			return new BooleanObjectField(displayPrefix + displayName, displayName, rawDataName, hasMoreThanOneLevel,
					metaKey, level, worldEditorDataType, metaField);
		case "unitRace":
			return new GameEnumObjectField(displayPrefix + displayName, displayName, rawDataName, hasMoreThanOneLevel,
					metaKey, level, worldEditorDataType, metaField, "unitRace", "WESTRING_COD_TYPE_UNITRACE",
					unitEditorData);

		default:
		case "string":
			return new StringObjectField(displayPrefix + displayName, displayName, rawDataName, hasMoreThanOneLevel,
					metaKey, level, worldEditorDataType, metaField);
		}
	}

	protected abstract String getDisplayName(final ObjectData metaData, WorldEditStrings worldEditStrings,
			final War3ID metaKey, final int level, MutableGameObject gameObject);

	protected abstract String getDisplayPrefix(ObjectData metaData, WorldEditStrings worldEditStrings, War3ID metaKey,
			int level, MutableGameObject gameObject);

	private String getRawDataName(final ObjectData metaData, final War3ID metaKey, final int level) {
		final GameObject metaDataFieldObject = metaData.get(metaKey.toString());
		return MutableObjectData.getEditorMetaDataDisplayKey(level, metaDataFieldObject);
	}
}

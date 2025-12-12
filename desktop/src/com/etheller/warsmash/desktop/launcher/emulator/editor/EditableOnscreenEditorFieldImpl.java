package com.etheller.warsmash.desktop.launcher.emulator.editor;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.EditableOnscreenObjectField;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public final class EditableOnscreenEditorFieldImpl {
	private final String cachedMetaKeyString;
	private final War3ID metaKey;
	private final int level;

	public EditableOnscreenEditorFieldImpl(final War3ID metaKey, final int level) {
		this.metaKey = metaKey;
		this.level = level;
		this.cachedMetaKeyString = metaKey.toString();
	}

	public String getDisplayName(final ObjectData metaData, final WorldEditStrings worldEditStrings,
			final MutableGameObject gameUnit) {
		final GameObject metaDataFieldObject = metaData.get(this.cachedMetaKeyString);
		String prefix = EditableOnscreenObjectField.categoryName(metaDataFieldObject.getField("category"),
				worldEditStrings) + " - ";
		if (this.level > 0) {
			if ((metaData.get("alev") != null) || (metaData.get("glvl") != null)) {
				// abilities, TODO less hacky
				prefix = String.format(worldEditStrings.getString("WESTRING_AEVAL_LVL"), this.level) + " - " + prefix;
			}
			else if (metaData.get("dvar") != null) {
				// doodads
				prefix += String.format(worldEditStrings.getString("WESTRING_DEVAL_VAR"), this.level) + " - ";
			}
			else {
				prefix = this.level + " - " + prefix; // ??? this should never happen
			}
		}
		// TODO upgrade data, depends on current object data
		return prefix + worldEditStrings.getString(metaDataFieldObject.getField("displayName"));
	}

	public String getRawDataName(final ObjectData metaData) {
		final GameObject metaDataFieldObject = metaData.get(this.cachedMetaKeyString);
		return MutableObjectData.getEditorMetaDataDisplayKey(this.level, metaDataFieldObject);
	}

	public Object getValue(final ObjectData metaData, final MutableGameObject gameUnit) {
		final GameObject metaDataFieldObject = metaData.get(this.cachedMetaKeyString);
		final String metaDataType = metaDataFieldObject.getField("type");
		switch (metaDataType) {
		case "int":
			return gameUnit.getFieldAsInteger(this.metaKey, this.level);
		case "real":
		case "unreal":
			return gameUnit.getFieldAsFloat(this.metaKey, this.level);
		case "bool":
			return gameUnit.getFieldAsBoolean(this.metaKey, this.level);
		default:
		case "string":
			return gameUnit.getFieldAsString(this.metaKey, this.level);
		}
	}

	public void setValue(final ObjectData metaData, final MutableGameObject gameUnit, final Object value) {
		final GameObject metaDataFieldObject = metaData.get(this.cachedMetaKeyString);
		final String metaDataType = metaDataFieldObject.getField("type");
		switch (metaDataType) {
		case "int":
			gameUnit.setField(this.metaKey, this.level, ((Number) value).intValue());
			break;
		case "real":
		case "unreal":
			gameUnit.setField(this.metaKey, this.level, ((Number) value).floatValue());
			break;
		case "bool":
			gameUnit.setField(this.metaKey, this.level, ((Boolean) value));
			break;
		default:
		case "string":
			gameUnit.setField(this.metaKey, this.level, value.toString());
		}
	}

}

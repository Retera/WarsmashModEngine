package com.etheller.warsmash.units.collapsed;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.custom.Change;
import com.etheller.warsmash.units.custom.ObjectDataChangeEntry;
import com.etheller.warsmash.units.custom.War3ObjectDataChangeset;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public class CollapsedObjectData {

	public static void apply(final WorldEditStrings worldEditStrings, final WorldEditorDataType worldEditorDataType,
			final ObjectData sourceSLKData, final ObjectData sourceSLKMetaData,
			final War3ObjectDataChangeset editorData) {
		if (worldEditorDataType == WorldEditorDataType.ABILITIES) {
			for (final String originalKey : sourceSLKData.keySet()) {
				final GameObject originalObject = sourceSLKData.get(originalKey);
				final String code = originalObject.getFieldAsString("code", 0);
				if (code != null) {
					final GameObject codeObject = sourceSLKData.get(code);
					if (codeObject != null) {
						sourceSLKData.inheritFrom(originalKey, code);
					}
				}
			}
		}
		for (final Map.Entry<War3ID, ObjectDataChangeEntry> entry : editorData.getCustom()) {
			final War3ID unitId = entry.getKey();
			final ObjectDataChangeEntry unitChanges = entry.getValue();
			final War3ID oldId = unitChanges.getOldId();
			final War3ID newId = unitChanges.getNewId();
			final String unitIdString = newId.toString();
			sourceSLKData.cloneUnit(oldId.asStringValue(), newId.asStringValue());
			final GameObject gameObject = sourceSLKData.get(unitIdString);
			if (gameObject != null) {
				for (final Map.Entry<War3ID, List<Change>> changeEntry : unitChanges.getChanges()) {
					final War3ID metaKey = changeEntry.getKey();
					final List<Change> changes = changeEntry.getValue();
					final GameObject metaDataField = sourceSLKMetaData.get(metaKey.asStringValue());
					if (metaDataField == null) {
						System.err.println("UNKNOWN META DATA FIELD: " + metaKey + " on " + unitId);
						continue;
					}
					applyChange(gameObject, changes, metaDataField);
				}
			}
		}
		for (final Map.Entry<War3ID, ObjectDataChangeEntry> entry : editorData.getOriginal()) {
			final War3ID unitId = entry.getKey();
			final ObjectDataChangeEntry unitChanges = entry.getValue();
			final War3ID oldId = unitChanges.getOldId();
			final War3ID newId = unitChanges.getNewId();
			final String unitIdString = oldId.toString();
			final GameObject gameObject = sourceSLKData.get(unitIdString);
			if (gameObject != null) {
				for (final Map.Entry<War3ID, List<Change>> changeEntry : unitChanges.getChanges()) {
					final War3ID metaKey = changeEntry.getKey();
					final List<Change> changes = changeEntry.getValue();
					final GameObject metaDataField = sourceSLKMetaData.get(metaKey.asStringValue());
					if (metaDataField == null) {
						System.err.println("UNKNOWN META DATA FIELD: " + metaKey + " on " + unitId);
						continue;
					}
					applyChange(gameObject, changes, metaDataField);
				}
			}
		}
		resolveStringReferencesInNames(worldEditStrings, sourceSLKData);
	}

	private static void applyChange(final GameObject gameObject, final List<Change> changes,
			final GameObject metaDataField) {
		for (final Change change : changes) {
			int level = change.getLevel();
			final String slk = metaDataField.getField("slk");
			int index = metaDataField.getFieldValue("index");
			String metaDataName = metaDataField.getField("field");
			final int repeatCount = metaDataField.getFieldValue("repeat");
			final String appendIndexMode = metaDataField.getField("appendIndex");
			final int data = metaDataField.getFieldValue("data");
			if (data > 0) {
				metaDataName += (char) ('A' + (data - 1));
			}
			if (repeatCount > 0) {
				switch (appendIndexMode) {
				case "0": {
					index = level - 1;
					break;
				}
				case "1": {
					final int upgradeExtensionLevel = level - 1;
					if (upgradeExtensionLevel > 0) {
						metaDataName += Integer.toString(upgradeExtensionLevel);
					}
					break;
				}
				default:
				case "": {
					if ((index == -1) || (repeatCount >= 10)) {
						if (level == 0) {
							level = 1;
						}
						if (repeatCount >= 10) {
							metaDataName += String.format("%2d", level).replace(' ', '0');
						}
						else {
							metaDataName += Integer.toString(level);
						}
					}
					else {
						index = level - 1;
					}
					break;
				}
				}
			}
			final String slkKey = metaDataName;
			switch (change.getVartype()) {
			case War3ObjectDataChangeset.VAR_TYPE_BOOLEAN: {
				gameObject.setField(slk, slkKey, Integer.toString(change.getLongval()), index);
				break;
			}
			case War3ObjectDataChangeset.VAR_TYPE_INT: {
				gameObject.setField(slk, slkKey, Integer.toString(change.getLongval()), index);
				break;
			}
			case War3ObjectDataChangeset.VAR_TYPE_REAL: {
				gameObject.setField(slk, slkKey, Float.toString(change.getRealval()), index);
				break;
			}
			case War3ObjectDataChangeset.VAR_TYPE_STRING: {
				final String fieldValue = change.getStrval();
				if (index == -1) {
					gameObject.clearFieldList(slk, slkKey);
					final int indexOfComma = fieldValue.indexOf(",");
					if (indexOfComma != -1) {
						final String[] splitLine = fieldValue.split(",");
						for (int splitChunkId = 0; splitChunkId < splitLine.length; splitChunkId++) {
							gameObject.setField(slk, slkKey, splitLine[splitChunkId], splitChunkId);
						}
					}
					else {
						gameObject.setField(slk, slkKey, fieldValue, index);
					}
				}
				else {
					gameObject.setField(slk, slkKey, fieldValue, index);
				}
				break;
			}
			case War3ObjectDataChangeset.VAR_TYPE_UNREAL: {
				gameObject.setField(slk, slkKey, Float.toString(change.getRealval()), index);
				break;
			}
			default:
				throw new IllegalStateException("Unsupported type: " + change.getVartype());
			}
		}
	}

	private static void resolveStringReferencesInNames(final WorldEditStrings worldEditStrings,
			final ObjectData sourceSLKData) {
		for (final String key : sourceSLKData.keySet()) {
			final GameObject gameObject = sourceSLKData.get(key);
			String name = gameObject.getField("Name");
			final String suffix = gameObject.getField("EditorSuffix");
			if (name.startsWith("WESTRING")) {
				if (!name.contains(" ")) {
					name = worldEditStrings.getString(name);
				}
				else {
					final String[] names = name.split(" ");
					name = "";
					for (final String subName : names) {
						if (name.length() > 0) {
							name += " ";
						}
						if (subName.startsWith("WESTRING")) {
							name += worldEditStrings.getString(subName);
						}
						else {
							name += subName;
						}
					}
				}
				if (name.startsWith("\"") && name.endsWith("\"")) {
					name = name.substring(1, name.length() - 1);
				}
				gameObject.setField("Name", name);
			}
			if (suffix.startsWith("WESTRING")) {
				gameObject.setField("EditorSuffix", worldEditStrings.getString(suffix));
			}
		}
	}
}

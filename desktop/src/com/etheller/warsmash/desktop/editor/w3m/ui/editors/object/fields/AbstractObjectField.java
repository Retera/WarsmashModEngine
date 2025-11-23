package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields;

import java.awt.Component;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public abstract class AbstractObjectField implements EditableOnscreenObjectField {
	private final String displayName;
	private final String sortName;
	private final String rawDataName;
	private final boolean showingLevelDisplay;
	private final War3ID metaKey;
	private final int level;
	private final WorldEditorDataType dataType;
	private final GameObject metaDataField;

	public AbstractObjectField(final String displayName, final String sortName, final String rawDataName,
			final boolean showLevelDisplay, final War3ID metaKey, final int level,
			final MutableObjectData.WorldEditorDataType dataType, final GameObject metaDataField) {
		this.displayName = displayName;
		this.sortName = sortName;
		this.rawDataName = rawDataName;
		this.showingLevelDisplay = showLevelDisplay;
		this.metaKey = metaKey;
		this.level = level;
		this.dataType = dataType;
		this.metaDataField = metaDataField;
	}

	@Override
	public final String getDisplayName(final MutableGameObject gameUnit) {
		return this.displayName;
	}

	@Override
	public String getSortName(final MutableGameObject gameUnit) {
		return this.sortName;
	}

	@Override
	public boolean isShowingLevelDisplay() {
		return this.showingLevelDisplay;
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public final String getRawDataName() {
		return this.rawDataName;
	}

	@Override
	public final Object getValue(final MutableGameObject gameUnit) {
		return getValue(gameUnit, this.metaKey, this.level);
	}

	protected abstract Object getValue(MutableGameObject gameUnit, War3ID metaKey, int level);

	@Override
	public boolean popupEditor(final MutableGameObject gameUnit, final Component parent,
			final WorldEditStrings worldEditStrings, final boolean editRawData, final boolean disableLimits) {
		String worldEditValueStringKey;
		switch (this.dataType) {
		case ABILITIES:
			worldEditValueStringKey = "WESTRING_AE_DLG_EDITVALUE";
			break;
		case BUFFS_EFFECTS:
			worldEditValueStringKey = "WESTRING_FE_DLG_EDITVALUE";
			break;
		case DESTRUCTIBLES:
			worldEditValueStringKey = "WESTRING_BE_DLG_EDITVALUE";
			break;
		case DOODADS:
			worldEditValueStringKey = "WESTRING_DE_DLG_EDITVALUE";
			break;
		case ITEM:
			worldEditValueStringKey = "WESTRING_IE_DLG_EDITVALUE";
			break;
		case UPGRADES:
			worldEditValueStringKey = "WESTRING_GE_DLG_EDITVALUE";
			break;
		default:
		case UNITS:
			worldEditValueStringKey = "WESTRING_UE_DLG_EDITVALUE";
			break;
		}
		final String defaultDialogTitle = worldEditStrings.getString(worldEditValueStringKey);
		return popupEditor(gameUnit, parent, worldEditStrings, editRawData, disableLimits, this.metaKey, this.level,
				defaultDialogTitle, this.metaDataField);
	}

	@Override
	public boolean hasEditedValue(final MutableGameObject gameUnit) {
		return gameUnit.hasCustomField(this.metaKey, this.level);
	}

	protected abstract boolean popupEditor(MutableGameObject gameUnit, Component parent,
			WorldEditStrings worldEditStrings, boolean editRawData, boolean disableLimits, War3ID metaKey, int level,
			String defaultDialogTitle, GameObject metaDataField);

}

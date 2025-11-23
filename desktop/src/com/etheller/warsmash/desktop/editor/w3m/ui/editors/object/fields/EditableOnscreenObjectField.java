package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields;

import java.awt.Component;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.WorldEditStrings;

public interface EditableOnscreenObjectField {
	String getDisplayName(final MutableGameObject gameUnit);

	/* for sorting */
	String getSortName(final MutableGameObject gameUnit);

	/* for sorting */
	int getLevel();

	boolean isShowingLevelDisplay();

	String getRawDataName();

	Object getValue(final MutableGameObject gameUnit);

	boolean hasEditedValue(MutableGameObject gameUnit);

	boolean popupEditor(MutableGameObject gameUnit, Component parent, final WorldEditStrings worldEditStrings,
			boolean editRawData, boolean disableLimits);

	// void setValue(final MutableGameObject gameUnit, final Object value);

	public static String categoryName(final String cat, final WorldEditStrings worldEditStrings) {
		switch (cat.toLowerCase()) {
		case "abil":
			return worldEditStrings.getString("WESTRING_OE_CAT_ABILITIES").replace("&", "");
		case "art":
			return worldEditStrings.getString("WESTRING_OE_CAT_ART").replace("&", "");
		case "combat":
			return worldEditStrings.getString("WESTRING_OE_CAT_COMBAT").replace("&", "");
		case "data":
			return worldEditStrings.getString("WESTRING_OE_CAT_DATA").replace("&", "");
		case "editor":
			return worldEditStrings.getString("WESTRING_OE_CAT_EDITOR").replace("&", "");
		case "move":
			return worldEditStrings.getString("WESTRING_OE_CAT_MOVEMENT").replace("&", "");
		case "path":
			return worldEditStrings.getString("WESTRING_OE_CAT_PATHING").replace("&", "");
		case "sound":
			return worldEditStrings.getString("WESTRING_OE_CAT_SOUND").replace("&", "");
		case "stats":
			return worldEditStrings.getString("WESTRING_OE_CAT_STATS").replace("&", "");
		case "tech":
			return worldEditStrings.getString("WESTRING_OE_CAT_TECHTREE").replace("&", "");
		case "text":
			return worldEditStrings.getString("WESTRING_OE_CAT_TEXT").replace("&", "");
		}
		return worldEditStrings.getString("WESTRING_UNKNOWN");
	}
}

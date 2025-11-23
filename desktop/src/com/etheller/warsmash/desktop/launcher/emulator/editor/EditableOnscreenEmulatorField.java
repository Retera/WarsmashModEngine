package com.etheller.warsmash.desktop.launcher.emulator.editor;

import java.awt.Component;

import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.StringBundle;

public interface EditableOnscreenEmulatorField {
	String getDisplayName(final Element gameUnit);

	/* for sorting */
	String getSortName(final Element gameUnit);

	String getRawDataName();

	Object getValue(final Element gameUnit);

	boolean hasEditedValue(Element gameUnit);

	boolean popupEditor(Element gameUnit, Component parent, final StringBundle worldEditStrings, boolean editRawData,
			boolean disableLimits);

	// void setValue(final MutableGameObject gameUnit, final Object value);
}

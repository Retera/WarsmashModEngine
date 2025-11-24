package com.etheller.warsmash.desktop.launcher.emulator.editor.fields;

import com.etheller.warsmash.desktop.launcher.emulator.editor.EditableOnscreenEmulatorField;
import com.etheller.warsmash.units.Element;

public abstract class AbstractEmulatorField implements EditableOnscreenEmulatorField {
	private final String displayName;
	private final String sortName;
	private final String rawDataName;
	private final String hintText;

	public AbstractEmulatorField(final String displayName, final String sortName, final String rawDataName,
			final String hintText) {
		this.displayName = displayName;
		this.sortName = sortName;
		this.rawDataName = rawDataName;
		this.hintText = hintText;
	}

	@Override
	public final String getDisplayName(final Element gameUnit) {
		return this.displayName;
	}

	@Override
	public String getSortName(final Element gameUnit) {
		return this.sortName;
	}

	@Override
	public final String getRawDataName() {
		return this.rawDataName;
	}

	public String getHintText() {
		return this.hintText;
	}
}

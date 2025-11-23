package com.etheller.warsmash.desktop.launcher.emulator.editor;

import java.util.List;

import com.etheller.warsmash.units.Element;

public interface EmulatorEditorFieldBuilder {
	List<EditableOnscreenEmulatorField> buildFields(Element emulatorConstants);
}

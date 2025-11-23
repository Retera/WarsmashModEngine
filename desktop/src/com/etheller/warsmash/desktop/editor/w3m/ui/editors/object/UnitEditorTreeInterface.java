package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object;

import com.etheller.warsmash.units.custom.War3ObjectDataChangeset;

public interface UnitEditorTreeInterface {

	char getWar3ObjectDataChangesetKindChar();

	void acceptPastedObjectData(War3ObjectDataChangeset pastedObjects);

	War3ObjectDataChangeset copySelectedObjects();

}

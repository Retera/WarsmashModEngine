package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.EditableOnscreenObjectField;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public interface SingleFieldFactory {
	EditableOnscreenObjectField create(MutableGameObject gameObject, ObjectData metaData,
			WorldEditStrings worldEditStrings, DataTable unitEditorData, War3ID metaKey, int level,
			MutableObjectData.WorldEditorDataType worldEditorDataType, boolean hasMoreThanOneLevel);
}

package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object;

import java.util.List;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.EditableOnscreenObjectField;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.WorldEditStrings;

public interface EditorFieldBuilder {
	List<EditableOnscreenObjectField> buildFields(ObjectData metaData, WorldEditStrings worldEditStrings,
			DataTable unitEditorData, MutableGameObject gameObject);
}

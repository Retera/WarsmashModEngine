package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.builders;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.EditorFieldBuilder;
import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.EditableOnscreenObjectField;
import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory.SingleFieldFactory;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public abstract class AbstractFieldBuilder implements EditorFieldBuilder {
	protected final SingleFieldFactory singleFieldFactory;
	protected final WorldEditorDataType worldEditorDataType;

	public AbstractFieldBuilder(final SingleFieldFactory singleFieldFactory,
			final WorldEditorDataType worldEditorDataType) {
		this.singleFieldFactory = singleFieldFactory;
		this.worldEditorDataType = worldEditorDataType;
	}

	@Override
	public final List<EditableOnscreenObjectField> buildFields(final ObjectData metaData,
			final WorldEditStrings worldEditStrings, final DataTable unitEditorData,
			final MutableGameObject gameObject) {
		final List<EditableOnscreenObjectField> fields = new ArrayList<>();
		for (final String key : metaData.keySet()) {
			final GameObject metaDataField = metaData.get(key);
			final War3ID metaKey = War3ID.fromString(key);
			if (includeField(gameObject, metaDataField, metaKey)) {
				makeAndAddFields(fields, metaKey, metaDataField, gameObject, metaData, worldEditStrings,
						unitEditorData);
			}
		}
		return fields;
	}

	protected abstract void makeAndAddFields(List<EditableOnscreenObjectField> fields, War3ID metaKey,
			GameObject metaDataField, MutableGameObject gameObject, final ObjectData metaData,
			WorldEditStrings worldEditStrings, DataTable unitEditorData);

	protected abstract boolean includeField(MutableGameObject gameObject, GameObject metaDataField, War3ID metaKey);
}

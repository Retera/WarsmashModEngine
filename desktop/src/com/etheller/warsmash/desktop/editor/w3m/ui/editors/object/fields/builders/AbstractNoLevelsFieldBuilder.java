package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.builders;

import java.util.List;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.EditableOnscreenObjectField;
import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory.SingleFieldFactory;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public abstract class AbstractNoLevelsFieldBuilder extends AbstractFieldBuilder {
	public AbstractNoLevelsFieldBuilder(final SingleFieldFactory singleFieldFactory,
			final WorldEditorDataType worldEditorDataType) {
		super(singleFieldFactory, worldEditorDataType);
	}

	@Override
	protected void makeAndAddFields(final List<EditableOnscreenObjectField> fields, final War3ID metaKey,
			final GameObject metaDataField, final MutableGameObject gameObject, final ObjectData metaData,
			final WorldEditStrings worldEditStrings, final DataTable unitEditorData) {
		fields.add(this.singleFieldFactory.create(gameObject, metaData, worldEditStrings, unitEditorData, metaKey, 0,
				this.worldEditorDataType, false));
	}

}

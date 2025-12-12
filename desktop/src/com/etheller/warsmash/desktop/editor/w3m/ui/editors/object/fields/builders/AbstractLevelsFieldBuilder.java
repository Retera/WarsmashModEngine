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

public abstract class AbstractLevelsFieldBuilder extends AbstractFieldBuilder {
	private final War3ID levelField;

	public AbstractLevelsFieldBuilder(final SingleFieldFactory singleFieldFactory,
			final WorldEditorDataType worldEditorDataType, final War3ID levelField) {
		super(singleFieldFactory, worldEditorDataType);
		this.levelField = levelField;
	}

	@Override
	protected final void makeAndAddFields(final List<EditableOnscreenObjectField> fields, final War3ID metaKey,
			final GameObject metaDataField, final MutableGameObject gameObject, final ObjectData metaData,
			final WorldEditStrings worldEditStrings, final DataTable unitEditorData) {
		final int repeatCount = metaDataField.getFieldValue("repeat");
		final int actualRepeatCount = gameObject.getFieldAsInteger(this.levelField, 0);
		if ((repeatCount >= 1) && (actualRepeatCount > 1)) {
			for (int level = 1; level <= actualRepeatCount; level++) {
				fields.add(this.singleFieldFactory.create(gameObject, metaData, worldEditStrings, unitEditorData,
						metaKey, level, this.worldEditorDataType, true));
			}
		}
		else {
			fields.add(this.singleFieldFactory.create(gameObject, metaData, worldEditStrings, unitEditorData, metaKey,
					repeatCount >= 1 ? 1 : 0, this.worldEditorDataType, false));
		}
	}
}

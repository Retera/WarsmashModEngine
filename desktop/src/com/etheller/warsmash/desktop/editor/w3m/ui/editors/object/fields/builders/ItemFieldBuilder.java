package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.builders;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory.BasicSingleFieldFactory;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;

public class ItemFieldBuilder extends AbstractNoLevelsFieldBuilder {
	public ItemFieldBuilder() {
		super(BasicSingleFieldFactory.INSTANCE, WorldEditorDataType.ITEM);
	}

	@Override
	protected boolean includeField(final MutableGameObject gameObject, final GameObject metaDataField,
			final War3ID metaKey) {
		return metaDataField.getFieldValue("useItem") > 0;
	}

}

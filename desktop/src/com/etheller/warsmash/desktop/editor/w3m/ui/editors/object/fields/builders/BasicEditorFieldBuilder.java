package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.builders;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory.SingleFieldFactory;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;

public final class BasicEditorFieldBuilder extends AbstractNoLevelsFieldBuilder {
	public BasicEditorFieldBuilder(final SingleFieldFactory singleFieldFactory,
			final WorldEditorDataType worldEditorDataType) {
		super(singleFieldFactory, worldEditorDataType);
	}

	@Override
	protected boolean includeField(final MutableGameObject gameObject, final GameObject metaDataField,
			final War3ID metaKey) {
		return true;
	}

}

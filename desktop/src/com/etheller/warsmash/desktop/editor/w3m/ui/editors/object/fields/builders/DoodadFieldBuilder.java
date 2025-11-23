package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.builders;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory.DoodadSingleFieldFactory;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;

public class DoodadFieldBuilder extends AbstractLevelsFieldBuilder {
	private static final War3ID DOODAD_VARIATIONS_FIELD = War3ID.fromString("dvar");

	public DoodadFieldBuilder() {
		super(DoodadSingleFieldFactory.INSTANCE, WorldEditorDataType.DOODADS, DOODAD_VARIATIONS_FIELD);
	}

	@Override
	protected boolean includeField(final MutableGameObject gameObject, final GameObject metaDataField,
			final War3ID metaKey) {
		return true;
	}

}

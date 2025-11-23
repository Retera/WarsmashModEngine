package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.builders;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory.BasicSingleFieldFactory;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;

public class UnitFieldBuilder extends AbstractNoLevelsFieldBuilder {
	private static final War3ID IS_A_BUILDING = War3ID.fromString("ubdg");

	public UnitFieldBuilder() {
		super(BasicSingleFieldFactory.INSTANCE, WorldEditorDataType.UNITS);
	}

	@Override
	protected boolean includeField(final MutableGameObject gameObject, final GameObject metaDataField,
			final War3ID metaKey) {
		return (metaDataField.getFieldValue("useUnit") > 0)
				|| (gameObject.getFieldAsBoolean(IS_A_BUILDING, 0) && (metaDataField.getFieldValue("useBuilding") > 0))
				|| (Character.isUpperCase(gameObject.getAlias().charAt(0))
						&& (metaDataField.getFieldValue("useHero") > 0));
	}

}

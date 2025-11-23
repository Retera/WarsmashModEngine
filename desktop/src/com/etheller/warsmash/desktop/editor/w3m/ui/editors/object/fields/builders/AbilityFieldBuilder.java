package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.builders;

import java.util.Arrays;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.factory.LevelsSingleFieldFactory;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;

public class AbilityFieldBuilder extends AbstractLevelsFieldBuilder {
	private static final War3ID ABILITY_LEVEL_FIELD = War3ID.fromString("alev");
	private static final War3ID HERO_ABILITY_FIELD = War3ID.fromString("aher");
	private static final War3ID ITEM_ABILITY_FIELD = War3ID.fromString("aite");

	public AbilityFieldBuilder() {
		super(LevelsSingleFieldFactory.INSTANCE, WorldEditorDataType.ABILITIES, ABILITY_LEVEL_FIELD);
	}

	@Override
	protected boolean includeField(final MutableGameObject gameObject, final GameObject metaDataField,
			final War3ID metaKey) {
		final boolean heroAbility = gameObject.getFieldAsBoolean(HERO_ABILITY_FIELD, 0);
		final boolean itemAbility = gameObject.getFieldAsBoolean(ITEM_ABILITY_FIELD, 0);
		final String useSpecific = metaDataField.getField("useSpecific");
		final String specificallyNotAllowedAbilityIds = metaDataField.getField("notSpecific");
		boolean passesSpecificCheck;
		if (useSpecific.length() > 0) {
			passesSpecificCheck = Arrays.asList(useSpecific.split(",")).contains(gameObject.getCode().asStringValue());
		}
		else {
			passesSpecificCheck = true;
		}
		if (specificallyNotAllowedAbilityIds.length() > 0) {
			if (Arrays.asList(specificallyNotAllowedAbilityIds.split(","))
					.contains(gameObject.getCode().asStringValue())) {
				passesSpecificCheck = false;
			}
		}
		if (((heroAbility && !itemAbility && (metaDataField.getFieldValue("useHero") == 1))
				|| (!heroAbility && !itemAbility && (metaDataField.getFieldValue("useUnit") == 1))
				|| (itemAbility && (metaDataField.getFieldValue("useItem") == 1))) && passesSpecificCheck) {
			return true;
		}
		return false;
	}

}

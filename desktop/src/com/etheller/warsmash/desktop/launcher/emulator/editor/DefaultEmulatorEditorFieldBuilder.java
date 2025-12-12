package com.etheller.warsmash.desktop.launcher.emulator.editor;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.desktop.launcher.emulator.HardcodedStringBundleDefaults;
import com.etheller.warsmash.desktop.launcher.emulator.editor.fields.BooleanEmulatorField;
import com.etheller.warsmash.desktop.launcher.emulator.editor.fields.IntegerEmulatorField;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.util.WarsmashConstants;

/**
 * Unlike some of the object editor stuff this was based on, it does not load
 * from a configuration, nor from a meta data SLK. This helps us so that it can
 * be used to configure a program that is failing to discover its configuration
 * and meta data.
 *
 * Later on down the line of someone refactors this to share code with the
 * similar "AbstractFieldBuilder" and such, that would be great, but for now we
 * want hardcoded configuration that's editing a different storage class.
 */
public class DefaultEmulatorEditorFieldBuilder implements EmulatorEditorFieldBuilder {
	private final StringBundle stringBundle;

	public DefaultEmulatorEditorFieldBuilder(final StringBundle stringBundle) {
		this.stringBundle = stringBundle;
	}

	private IntegerEmulatorField intField(final String textKey, final String sort, final String rawDataKey,
			final int min, final int max) {
		return new IntegerEmulatorField(this.stringBundle.getString(textKey), sort, rawDataKey,
				this.stringBundle.getString(textKey + "_HINT"), min, max);
	}

	private BooleanEmulatorField booleanField(final String textKey, final String sort, final String rawDataKey) {
		return new BooleanEmulatorField(this.stringBundle.getString(textKey), sort, rawDataKey,
				this.stringBundle.getString(textKey + "_HINT"));
	}

	@Override
	public List<EditableOnscreenEmulatorField> buildFields(final Element emulatorConstants) {
		final List<EditableOnscreenEmulatorField> fields = new ArrayList<>();
		fields.add(intField(HardcodedStringBundleDefaults.SMSHSTRING_EEVAL_EMPL, "000",
				WarsmashConstants.KEY_MAX_PLAYERS, 0, 100));
		fields.add(intField(HardcodedStringBundleDefaults.SMSHSTRING_EEVAL_EGVR, "001",
				WarsmashConstants.KEY_GAME_VERSION, 0, 2));
		fields.add(booleanField(HardcodedStringBundleDefaults.SMSHSTRING_EEVAL_ECCR, "002",
				WarsmashConstants.KEY_CATCH_CURSOR));
		fields.add(booleanField(HardcodedStringBundleDefaults.SMSHSTRING_EEVAL_EFMB, "003",
				WarsmashConstants.KEY_FULL_SCREEN_MENU_BACKDROP));
		fields.add(booleanField(HardcodedStringBundleDefaults.SMSHSTRING_EEVAL_EFTI, "004",
				WarsmashConstants.KEY_FIX_FLAT_FILES_TILESET_LOADING));
		fields.add(booleanField(HardcodedStringBundleDefaults.SMSHSTRING_EEVAL_EMUS, "005",
				WarsmashConstants.KEY_ENABLE_MUSIC));
		fields.add(booleanField(HardcodedStringBundleDefaults.SMSHSTRING_EEVAL_ELUW, "006",
				WarsmashConstants.KEY_LOAD_UNITS_FROM_WORLD_EDIT_DATA));
		fields.add(booleanField(HardcodedStringBundleDefaults.SMSHSTRING_EEVAL_E132, "007",
				WarsmashConstants.KEY_CRASH_ON_INCOMPATIBLE132_FEATURES));
		fields.add(booleanField(HardcodedStringBundleDefaults.SMSHSTRING_EEVAL_E132, "007",
				WarsmashConstants.KEY_CRASH_ON_INCOMPATIBLE132_FEATURES));
		return null;
	}

}

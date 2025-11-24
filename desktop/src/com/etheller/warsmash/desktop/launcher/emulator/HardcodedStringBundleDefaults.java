package com.etheller.warsmash.desktop.launcher.emulator;

/**
 * Access to the default hardcoded string bundles. The idea here is that if
 * someone is using the configuration tool, they probably have a bad
 * configuration, so we want these strings to load as a reasonable default
 * instead of having to find a configuration.
 *
 * Later if someone needs to translate this program to a different language,
 * they could do something with this file or make multiple options or something,
 * but for now we are supporting English.
 */
public class HardcodedStringBundleDefaults {
	public static final String SMSHSTRING_COD_TYPE_INT = "SMSHSTRING_COD_TYPE_INT";
	public static final String SMSHSTRING_COD_TYPE_STRING = "SMSHSTRING_COD_TYPE_STRING";
	public static final String SMSHSTRING_COD_TYPE_BOOL = "SMSHSTRING_COD_TYPE_BOOL";
	public static final String SMSHSTRING_EE_DLG_EDITVALUE = "SMSHSTRING_EE_DLG_EDITVALUE";
	public static final String SMSHSTRING_EEVAL_EMPL = "SMSHSTRING_EEVAL_EMPL";
	public static final String SMSHSTRING_EEVAL_EMPL_HINT = "SMSHSTRING_EEVAL_EMPL_HINT";
	public static final String SMSHSTRING_EEVAL_EGVR = "SMSHSTRING_EEVAL_EGVR";
	public static final String SMSHSTRING_EEVAL_EGVR_HINT = "SMSHSTRING_EEVAL_EGVR_HINT";
	public static final String SMSHSTRING_EEVAL_ECCR = "SMSHSTRING_EEVAL_ECCR";
	public static final String SMSHSTRING_EEVAL_ECCR_HINT = "SMSHSTRING_EEVAL_ECCR_HINT";
	public static final String SMSHSTRING_EEVAL_EFMB = "SMSHSTRING_EEVAL_EFMB";
	public static final String SMSHSTRING_EEVAL_EFMB_HINT = "SMSHSTRING_EEVAL_EFMB_HINT";
	public static final String SMSHSTRING_EEVAL_EFTI = "SMSHSTRING_EEVAL_EFTI";
	public static final String SMSHSTRING_EEVAL_EFTI_HINT = "SMSHSTRING_EEVAL_EFTI_HINT";
	public static final String SMSHSTRING_EEVAL_EMUS = "SMSHSTRING_EEVAL_EMUS";
	public static final String SMSHSTRING_EEVAL_EMUS_HINT = "SMSHSTRING_EEVAL_EMUS_HINT";
	public static final String SMSHSTRING_EEVAL_ELUW = "SMSHSTRING_EEVAL_ELUW";
	public static final String SMSHSTRING_EEVAL_ELUW_HINT = "SMSHSTRING_EEVAL_ELUW_HINT";
	public static final String SMSHSTRING_EEVAL_E132 = "SMSHSTRING_EEVAL_E132";
	public static final String SMSHSTRING_EEVAL_E132_HINT = "SMSHSTRING_EEVAL_E132_HINT";
	public static final String SMSHSTRING_EEVAL_EHTK = "SMSHSTRING_EEVAL_EHTK";
	public static final String SMSHSTRING_EEVAL_EHTK_HINT = "SMSHSTRING_EEVAL_EHTK_HINT";
	public static final String SMSHSTRING_EEVAL_ERCB = "SMSHSTRING_EEVAL_ERCB";
	public static final String SMSHSTRING_EEVAL_ERCB_HINT = "SMSHSTRING_EEVAL_ERCB_HINT";
	public static final String SMSHSTRING_EEVAL_E9IT = "SMSHSTRING_EEVAL_E9IT";
	public static final String SMSHSTRING_EEVAL_E9IT_HINT = "SMSHSTRING_EEVAL_E9IT_HINT";

	public static HardcodedStringBundle loadEnglish() {
		final HardcodedStringBundle hardcodedStringBundle = new HardcodedStringBundle();
		hardcodedStringBundle.putString("WESTRING_UE_FIELDNAME", "Field");
		hardcodedStringBundle.putString("WESTRING_UE_FIELDVALUE", "Value");
		hardcodedStringBundle.putString("WESTRING_UNKNOWN", "Unknown");

		hardcodedStringBundle.putString(SMSHSTRING_EE_DLG_EDITVALUE, "Edit Emulator Value - %s");
		hardcodedStringBundle.putString(SMSHSTRING_COD_TYPE_STRING, "String");
		hardcodedStringBundle.putString(SMSHSTRING_COD_TYPE_INT, "Integer");
		hardcodedStringBundle.putString(SMSHSTRING_COD_TYPE_BOOL, "Boolean");

		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EMPL, "Maximum Players");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EMPL_HINT,
				"Includes neutrals, so it must be 16 for 1.00 to 1.28, and 28 for 1.29 onwards.\nMaybe 5 for single player third person engine performance.");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EGVR, "Game Version");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EGVR_HINT, "0: Reign of Chaos; \n1: Frozen Throne");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_ECCR, "Catch Cursor");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_ECCR_HINT, "Uses LibGDX catch cursor function, hides cursor.");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EFMB, "Full Screen Menu Backdrop");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EFMB_HINT,
				"Hides 'Black Bars' on menu. I usually only do it when I have custom 3D asset mod loaded with backgrounds that actually look good widescreen.");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EFTI, "Fix Flat Files Tileset Loading");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EFTI_HINT,
				"Fixes cliff tiles always being the same across all tilesets, if you are using 'Folder' instead of 'MPQ' mode.");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EMUS, "Enable Music");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EMUS_HINT,
				"TODO this should be an ingame toggle later on (CTRL+M).");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_ELUW, "Load Units from World-Editor-only Files");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_ELUW_HINT,
				"Basically pointless, would make most maps not work, was used prior to JASS unit loading.");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_E132, "Crash on Incompatible Patch 1.32+ Features");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_E132_HINT,
				"If disabled, we do our best to continue on error with these.");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EHTK, "Input Hotkey Mode");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_EHTK_HINT,
				"0: old style each ability declares its own; \n1: Grid Hotkeys (QWER / ASDF / ZXCV)");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_ERCB, "Parse Reign of Chaos Beta Instead");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_ERCB_HINT,
				"There is a very old version that uses the same MDX headers but different MDX format, which cannot auto detect. If you are on that version, enable this.");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_E9IT, "Use 9 Item Inventory");
		hardcodedStringBundle.putString(SMSHSTRING_EEVAL_E9IT_HINT,
				"Allocates more OrderIDs for item manipulation. Generally only used with a companion asset mod.");

		return hardcodedStringBundle;
	}

	private HardcodedStringBundleDefaults() {
	}
}

package com.etheller.warsmash.util;

import java.util.List;

import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRaceManager;

import net.warsmash.uberserver.GamingNetwork;

public class WarsmashConstants {
	public static final String KEY_JASS_FILE_LIST = "JassFileList";
	public static final String KEY_MAX_PLAYERS = "MaxPlayers";
	public static final String KEY_GAME_VERSION = "GameVersion";
	public static final String KEY_CATCH_CURSOR = "CatchCursor";
	public static final String KEY_FULL_SCREEN_MENU_BACKDROP = "FullScreenMenuBackdrop";
	public static final String KEY_FIX_FLAT_FILES_TILESET_LOADING = "FixFlatFilesTilesetLoading";
	public static final String KEY_ENABLE_MUSIC = "EnableMusic";
	public static final String KEY_LOAD_UNITS_FROM_WORLD_EDIT_DATA = "LoadUnitsFromWorldEditData";
	public static final String KEY_CRASH_ON_INCOMPATIBLE132_FEATURES = "CrashOnIncompatible132Features";
	public static final String KEY_INPUT_HOTKEY_MODE = "InputHotkeyMode";
	public static final String KEY_PARSE_REIGN_OF_CHAOS_BETA_MODELS_INSTEAD = "ParseReignOfChaosBetaModelsInstead";
	public static final String KEY_PARSE_ABILITY_DATA_NUMERIC = "AbilityDataNumbersNotLettersForIndex";
	public static final String KEY_USE_NINE_ITEM_INVENTORY = "UseNineItemInventory";

	public static int MAX_PLAYERS = 28;
	/*
	 * With version, we use 0 for RoC, 1 for TFT emulation, and probably 2+ or
	 * whatever for custom mods and other stuff
	 */
	public static int GAME_VERSION = 1;
	public static final int REPLACEABLE_TEXTURE_LIMIT = 64;
	public static final float SIMULATION_STEP_TIME = 1 / 20f;
	public static final int PORT_NUMBER = GamingNetwork.UDP_SINGLE_GAME_PORT;
	public static final float BUILDING_CONSTRUCT_START_LIFE = 0.1f;
	public static final int BUILD_QUEUE_SIZE = 7;
	public static final int MAX_SELECTION_SIZE = 18;
	// It looks like in Patch 1.22, "Particle" in video settings will change this
	// factor:
	// Low - unknown ?
	// Medium - 1.0f
	// High - 1.5f
	public static final float MODEL_DETAIL_PARTICLE_FACTOR = 1.5f;
	public static final float MODEL_DETAIL_PARTICLE_FACTOR_INVERSE = 1f / MODEL_DETAIL_PARTICLE_FACTOR;

	// I know this default string is from somewhere, maybe a language file? Didn't
	// find it yet so I used this
	public static final String DEFAULT_STRING = "Default string";

	public static boolean CATCH_CURSOR = false;
	public static boolean FULL_SCREEN_MENU_BACKDROP = false;
	public static final boolean VERBOSE_LOGGING = true;
	public static final boolean ENABLE_DEBUG = false;
	public static final char SPECIAL_ESCAPE_KEYCODE = 0x7E;

	// My tileset loader is "always on top", even for local files. This is different
	// from some MPQ loader engines that would use
	// load index as a numeric value and could be changed. For now, I have this
	// workaround to fix it if you need the local files
	// to take priority over built-ins for tilesets.
	public static boolean FIX_FLAT_FILES_TILESET_LOADING = false;
	public static boolean ENABLE_MUSIC = false;
	public static boolean LOAD_UNITS_FROM_WORLDEDIT_DATA = false;
	public static boolean CRASH_ON_INCOMPATIBLE_132_FEATURES = false;
	public static final boolean FIRE_DEATH_EVENTS_ON_REMOVEUNIT = false;
	public static int INPUT_HOTKEY_MODE = 1;
	public static boolean PARSE_REIGN_OF_CHAOS_BETA_MODELS_INSTEAD = false;
	public static boolean PARSE_ABILITY_DATA_NUMERIC = false;

	public static boolean USE_NINE_ITEM_INVENTORY = true;

	public static CRaceManager RACE_MANAGER;

	public static String[] JASS_FILE_LIST = { "Scripts\\common.j", "Scripts\\Blizzard.j", "Scripts\\war3map.j" };
	public static final float GAME_SPEED_TIME_FACTOR = 0.5f;
	public static final int ONGOING_BEHAVIOR_NOTIFICATION_TICKS = (int) (0.5f / SIMULATION_STEP_TIME);

	public static final boolean SHOW_FPS = true;
	public static String[] ABILITY_DATA_LETTERS;

	public static List<String> ABILITY_COMPATIBILITY = null;

	public static void loadConstants(final GameObject emulatorConstants, final DataTable warsmashIni) {
		MAX_PLAYERS = emulatorConstants.getFieldValue(KEY_MAX_PLAYERS);
		GAME_VERSION = emulatorConstants.getFieldValue(KEY_GAME_VERSION);
		CATCH_CURSOR = emulatorConstants.getFieldValue(KEY_CATCH_CURSOR) == 1;
		if (emulatorConstants.getField(KEY_FULL_SCREEN_MENU_BACKDROP) != null) {
			FULL_SCREEN_MENU_BACKDROP = emulatorConstants.getFieldValue(KEY_FULL_SCREEN_MENU_BACKDROP) == 1;
		}
		final List<String> jassFileList = emulatorConstants.getFieldAsList(KEY_JASS_FILE_LIST);
		if ((jassFileList != null) && !jassFileList.isEmpty()
				&& !((jassFileList.size() == 1) && jassFileList.get(0).isEmpty())) {
			final String[] jassFileArray = jassFileList.toArray(new String[0]);
			JASS_FILE_LIST = jassFileArray;
		}
		FIX_FLAT_FILES_TILESET_LOADING = emulatorConstants.getFieldValue(KEY_FIX_FLAT_FILES_TILESET_LOADING) == 1;
		ENABLE_MUSIC = emulatorConstants.getFieldValue(KEY_ENABLE_MUSIC) == 1;
		LOAD_UNITS_FROM_WORLDEDIT_DATA = emulatorConstants.getFieldValue(KEY_LOAD_UNITS_FROM_WORLD_EDIT_DATA) == 1;
		CRASH_ON_INCOMPATIBLE_132_FEATURES = emulatorConstants
				.getFieldValue(KEY_CRASH_ON_INCOMPATIBLE132_FEATURES) == 1;
		INPUT_HOTKEY_MODE = emulatorConstants.getFieldValue(KEY_INPUT_HOTKEY_MODE);
		PARSE_REIGN_OF_CHAOS_BETA_MODELS_INSTEAD = emulatorConstants
				.getFieldValue(KEY_PARSE_REIGN_OF_CHAOS_BETA_MODELS_INSTEAD) == 1;
		PARSE_ABILITY_DATA_NUMERIC = emulatorConstants.getFieldValue(KEY_PARSE_ABILITY_DATA_NUMERIC) == 1;
		if (PARSE_ABILITY_DATA_NUMERIC) {
			ABILITY_DATA_LETTERS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		}
		else {
			ABILITY_DATA_LETTERS = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" };
		}
		USE_NINE_ITEM_INVENTORY = emulatorConstants.getFieldValue(KEY_USE_NINE_ITEM_INVENTORY) == 1;
		final String races = emulatorConstants.getField("Races");
		RACE_MANAGER = new CRaceManager();
		if ((races == null) || races.isEmpty()) {
			RACE_MANAGER.addRace("Human", 1, 1);
			RACE_MANAGER.addRace("Orc", 2, 2);
			RACE_MANAGER.addRace("Undead", 3, 4);
			RACE_MANAGER.addRace("NightElf", 4, 3);
		}
		else {
			final String[] raceKeys = races.split(",");
			for (final String raceKey : raceKeys) {
				final Element raceElement = warsmashIni.get(raceKey);
				if (raceElement == null) {
					throw new IllegalStateException("Missing data in warsmash.ini for race: " + raceKey);
				}
				RACE_MANAGER.addRace(raceKey, raceElement.getFieldValue("RaceID"),
						raceElement.getFieldValue("RacePrefID"));
			}
		}
		RACE_MANAGER.build();
		ABILITY_COMPATIBILITY = emulatorConstants.getFieldAsList("AbilityCompatibility");
	}

	public static final String getGameId() {
		return (GAME_VERSION == 0) ? GamingNetwork.GAME_ID_BASE : GamingNetwork.GAME_ID_XPAC;
	}

}

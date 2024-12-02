package com.etheller.warsmash.util;

import java.util.List;

import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRaceManager;

import net.warsmash.uberserver.GamingNetwork;

public class WarsmashConstants {
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

	public static boolean USE_NINE_ITEM_INVENTORY = true;

	public static CRaceManager RACE_MANAGER;

	public static String[] JASS_FILE_LIST = { "Scripts\\common.j", "Scripts\\Blizzard.j", "Scripts\\war3map.j" };
	public static final float GAME_SPEED_TIME_FACTOR = 0.5f;

	public static final boolean SHOW_FPS = true;

	public static void loadConstants(final GameObject emulatorConstants, final DataTable warsmashIni) {
		MAX_PLAYERS = emulatorConstants.getFieldValue("MaxPlayers");
		GAME_VERSION = emulatorConstants.getFieldValue("GameVersion");
		CATCH_CURSOR = emulatorConstants.getFieldValue("CatchCursor") == 1;
		if (emulatorConstants.getField("FullScreenMenuBackdrop") != null) {
			FULL_SCREEN_MENU_BACKDROP = emulatorConstants.getFieldValue("FullScreenMenuBackdrop") == 1;
		}
		final List<String> jassFileList = emulatorConstants.getFieldAsList("JassFileList");
		if ((jassFileList != null) && !jassFileList.isEmpty()
				&& !((jassFileList.size() == 1) && jassFileList.get(0).isEmpty())) {
			final String[] jassFileArray = jassFileList.toArray(new String[0]);
			JASS_FILE_LIST = jassFileArray;
		}
		FIX_FLAT_FILES_TILESET_LOADING = emulatorConstants.getFieldValue("FixFlatFilesTilesetLoading") == 1;
		ENABLE_MUSIC = emulatorConstants.getFieldValue("EnableMusic") == 1;
		LOAD_UNITS_FROM_WORLDEDIT_DATA = emulatorConstants.getFieldValue("LoadUnitsFromWorldEditData") == 1;
		CRASH_ON_INCOMPATIBLE_132_FEATURES = emulatorConstants.getFieldValue("CrashOnIncompatible132Features") == 1;
		INPUT_HOTKEY_MODE = emulatorConstants.getFieldValue("InputHotkeyMode");
		PARSE_REIGN_OF_CHAOS_BETA_MODELS_INSTEAD = emulatorConstants
				.getFieldValue("ParseReignOfChaosBetaModelsInstead") == 1;
		USE_NINE_ITEM_INVENTORY = emulatorConstants.getFieldValue("UseNineItemInventory") == 1;
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
	}

	public static final String getGameId() {
		return (GAME_VERSION == 0) ? GamingNetwork.GAME_ID_BASE : GamingNetwork.GAME_ID_XPAC;
	}

}

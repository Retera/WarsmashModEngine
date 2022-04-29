package com.etheller.warsmash.util;

import net.warsmash.uberserver.GamingNetwork;

public class WarsmashConstants {
	public static final int MAX_PLAYERS = 16;
	/*
	 * With version, we use 0 for RoC, 1 for TFT emulation, and probably 2+ or
	 * whatever for custom mods and other stuff
	 */
	public static int GAME_VERSION = 1;
	public static final int REPLACEABLE_TEXTURE_LIMIT = 64;
	public static final float SIMULATION_STEP_TIME = 1 / 20f;
	public static final int PORT_NUMBER = 6115;
	public static final float BUILDING_CONSTRUCT_START_LIFE = 0.1f;
	public static final int BUILD_QUEUE_SIZE = 7;
	public static final int MAX_SELECTION_SIZE = 12;
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

	public static final boolean CATCH_CURSOR = false;
	public static final boolean VERBOSE_LOGGING = false;
	public static final boolean ENABLE_DEBUG = false;
	public static final char SPECIAL_ESCAPE_KEYCODE = 0x7E;

	// My tileset loader is "always on top", even for local files. This is different
	// from some MPQ loader engines that would use
	// load index as a numeric value and could be changed. For now, I have this
	// workaround to fix it if you need the local files
	// to take priority over built-ins for tilesets.
	public static final boolean FIX_FLAT_FILES_TILESET_LOADING = false;
	public static final boolean ENABLE_MUSIC = false;
	public static final boolean LOAD_UNITS_FROM_WORLDEDIT_DATA = false;
	public static final boolean CRASH_ON_INCOMPATIBLE_132_FEATURES = false;
	public static final boolean FIRE_DEATH_EVENTS_ON_REMOVEUNIT = false;
	public static final int INPUT_HOTKEY_MODE = 1;

	public static final String getGameId() {
		return (GAME_VERSION == 0) ? GamingNetwork.GAME_ID_BASE : GamingNetwork.GAME_ID_XPAC;
	}

}

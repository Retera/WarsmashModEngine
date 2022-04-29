package com.etheller.warsmash.parsers.w3x.w3i;

public class War3MapW3iFlags {
	// definitions for these are based on what was found online located at:
	// wc3maps.com/InsideTheW3M.html
	public static final int HIDE_MINIMAP_IN_PREVIEW_SCREENS = 0x0001;
	public static final int MODIFY_ALLY_PRIORITIES = 0x0002;
	public static final int MELEE_MAP = 0x0004;
	public static final int PLAYABLE_MAP_SIZE_LARGE_AND_NEVER_REDUCED_TO_MEDIUM = 0x0008; // ? not sure
	public static final int MASKED_AREAS_ARE_PARTIALLY_VISIBLE = 0x0010;
	public static final int FIXED_PLAYER_SETTINGS_FOR_CUSTOM_FORCES = 0x0020;
	public static final int USE_CUSTOM_FORCES = 0x0040;
	public static final int USE_CUSTOM_TECHTREE = 0x0080;
	public static final int USE_CUSTOM_ABILITIES = 0x0100;
	public static final int USE_CUSTOM_UPGRADES = 0x0200;
	public static final int MAP_PROPERTIES_MENU_OPENED_AT_LEAST_ONCE_SINCE_MAP_CREATED = 0x0400;
	public static final int SHOW_WATER_WAVES_ON_CLIFF_SHORES = 0x0800;
	public static final int SHOW_WATER_WAVES_ON_ROLLING_SHORES = 0x1000;
}

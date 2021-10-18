package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger;

import com.etheller.interpreter.ast.util.CHandle;

//===================================================
//Game, Player and Unit Events
//
//When an event causes a trigger to fire these
//values allow the action code to determine which
//event was dispatched and therefore which set of
//native functions should be used to get information
//about the event.
//
//Do NOT change the order or value of these constants
//without insuring that the JASS_GAME_EVENTS_WAR3 enum
//is changed to match.
//
//===================================================

public enum JassGameEventsWar3 implements CHandle {
	// ===================================================
	// For use with TriggerRegisterGameEvent
	// ===================================================
	EVENT_GAME_VICTORY,
	EVENT_GAME_END_LEVEL,

	EVENT_GAME_VARIABLE_LIMIT,
	EVENT_GAME_STATE_LIMIT,

	EVENT_GAME_TIMER_EXPIRED,

	EVENT_GAME_ENTER_REGION,
	EVENT_GAME_LEAVE_REGION,

	EVENT_GAME_TRACKABLE_HIT,
	EVENT_GAME_TRACKABLE_TRACK,

	EVENT_GAME_SHOW_SKILL,
	EVENT_GAME_BUILD_SUBMENU,

	// ===================================================
	// For use with TriggerRegisterPlayerEvent
	// ===================================================
	EVENT_PLAYER_STATE_LIMIT,
	EVENT_PLAYER_ALLIANCE_CHANGED,

	EVENT_PLAYER_DEFEAT,
	EVENT_PLAYER_VICTORY,
	EVENT_PLAYER_LEAVE,
	EVENT_PLAYER_CHAT,
	EVENT_PLAYER_END_CINEMATIC,

	// ===================================================
	// For use with TriggerRegisterPlayerUnitEvent
	// ===================================================
	EVENT_PLAYER_UNIT_ATTACKED,
	EVENT_PLAYER_UNIT_RESCUED,

	EVENT_PLAYER_UNIT_DEATH,
	EVENT_PLAYER_UNIT_DECAY,

	EVENT_PLAYER_UNIT_DETECTED,
	EVENT_PLAYER_UNIT_HIDDEN,

	EVENT_PLAYER_UNIT_SELECTED,
	EVENT_PLAYER_UNIT_DESELECTED,

	EVENT_PLAYER_UNIT_CONSTRUCT_START,
	EVENT_PLAYER_UNIT_CONSTRUCT_CANCEL,
	EVENT_PLAYER_UNIT_CONSTRUCT_FINISH,

	EVENT_PLAYER_UNIT_UPGRADE_START,
	EVENT_PLAYER_UNIT_UPGRADE_CANCEL,
	EVENT_PLAYER_UNIT_UPGRADE_FINISH,

	EVENT_PLAYER_UNIT_TRAIN_START,
	EVENT_PLAYER_UNIT_TRAIN_CANCEL,
	EVENT_PLAYER_UNIT_TRAIN_FINISH,

	EVENT_PLAYER_UNIT_RESEARCH_START,
	EVENT_PLAYER_UNIT_RESEARCH_CANCEL,
	EVENT_PLAYER_UNIT_RESEARCH_FINISH,
	EVENT_PLAYER_UNIT_ISSUED_ORDER,
	EVENT_PLAYER_UNIT_ISSUED_POINT_ORDER,
	EVENT_PLAYER_UNIT_ISSUED_TARGET_ORDER,

	EVENT_PLAYER_HERO_LEVEL,
	EVENT_PLAYER_HERO_SKILL,

	EVENT_PLAYER_HERO_REVIVABLE,

	EVENT_PLAYER_HERO_REVIVE_START,
	EVENT_PLAYER_HERO_REVIVE_CANCEL,
	EVENT_PLAYER_HERO_REVIVE_FINISH,
	EVENT_PLAYER_UNIT_SUMMON,
	EVENT_PLAYER_UNIT_DROP_ITEM,
	EVENT_PLAYER_UNIT_PICKUP_ITEM,
	EVENT_PLAYER_UNIT_USE_ITEM,
	EVENT_PLAYER_UNIT_LOADED,

	// ===================================================
	// For use with TriggerRegisterUnitEvent
	// ===================================================

	EVENT_UNIT_DAMAGED,
	EVENT_UNIT_DEATH,
	EVENT_UNIT_DECAY,
	EVENT_UNIT_DETECTED,
	EVENT_UNIT_HIDDEN,
	EVENT_UNIT_SELECTED,
	EVENT_UNIT_DESELECTED,

	EVENT_UNIT_STATE_LIMIT,

	// Events which may have a filter for the "other unit"
	//
	EVENT_UNIT_ACQUIRED_TARGET,
	EVENT_UNIT_TARGET_IN_RANGE,
	EVENT_UNIT_ATTACKED,
	EVENT_UNIT_RESCUED,

	EVENT_UNIT_CONSTRUCT_CANCEL,
	EVENT_UNIT_CONSTRUCT_FINISH,

	EVENT_UNIT_UPGRADE_START,
	EVENT_UNIT_UPGRADE_CANCEL,
	EVENT_UNIT_UPGRADE_FINISH,

// Events which involve the specified unit performing
// training of other units
//
	EVENT_UNIT_TRAIN_START,
	EVENT_UNIT_TRAIN_CANCEL,
	EVENT_UNIT_TRAIN_FINISH,

	EVENT_UNIT_RESEARCH_START,
	EVENT_UNIT_RESEARCH_CANCEL,
	EVENT_UNIT_RESEARCH_FINISH,

	EVENT_UNIT_ISSUED_ORDER,
	EVENT_UNIT_ISSUED_POINT_ORDER,
	EVENT_UNIT_ISSUED_TARGET_ORDER,

	EVENT_UNIT_HERO_LEVEL,
	EVENT_UNIT_HERO_SKILL,

	EVENT_UNIT_HERO_REVIVABLE,
	EVENT_UNIT_HERO_REVIVE_START,
	EVENT_UNIT_HERO_REVIVE_CANCEL,
	EVENT_UNIT_HERO_REVIVE_FINISH,

	EVENT_UNIT_SUMMON,

	EVENT_UNIT_DROP_ITEM,
	EVENT_UNIT_PICKUP_ITEM,
	EVENT_UNIT_USE_ITEM,

	EVENT_UNIT_LOADED,

	EVENT_WIDGET_DEATH,

	EVENT_DIALOG_BUTTON_CLICK,
	EVENT_DIALOG_CLICK,

	// ===================================================
	// Frozen Throne Expansion Events
	// Need to be added here to preserve compat
	// ===================================================

	// ===================================================
	// For use with TriggerRegisterGameEvent
	// ===================================================

	EVENT_GAME_LOADED,
	EVENT_GAME_TOURNAMENT_FINISH_SOON,
	EVENT_GAME_TOURNAMENT_FINISH_NOW,
	EVENT_GAME_SAVE,

	EVENT_UNKNOWN_TFT_CODE_260,

	// ===================================================
	// For use with TriggerRegisterPlayerEvent
	// ===================================================

	EVENT_PLAYER_ARROW_LEFT_DOWN,
	EVENT_PLAYER_ARROW_LEFT_UP,
	EVENT_PLAYER_ARROW_RIGHT_DOWN,
	EVENT_PLAYER_ARROW_RIGHT_UP,
	EVENT_PLAYER_ARROW_DOWN_DOWN,
	EVENT_PLAYER_ARROW_DOWN_UP,
	EVENT_PLAYER_ARROW_UP_DOWN,
	EVENT_PLAYER_ARROW_UP_UP,

	// ===================================================
	// For use with TriggerRegisterPlayerUnitEvent
	// ===================================================

	EVENT_PLAYER_UNIT_SELL,
	EVENT_PLAYER_UNIT_CHANGE_OWNER,
	EVENT_PLAYER_UNIT_SELL_ITEM,
	EVENT_PLAYER_UNIT_SPELL_CHANNEL,
	EVENT_PLAYER_UNIT_SPELL_CAST,
	EVENT_PLAYER_UNIT_SPELL_EFFECT,
	EVENT_PLAYER_UNIT_SPELL_FINISH,
	EVENT_PLAYER_UNIT_SPELL_ENDCAST,
	EVENT_PLAYER_UNIT_PAWN_ITEM,

	EVENT_UNKNOWN_TFT_CODE_278,
	EVENT_UNKNOWN_TFT_CODE_279,
	EVENT_UNKNOWN_TFT_CODE_280,
	EVENT_UNKNOWN_TFT_CODE_281,
	EVENT_UNKNOWN_TFT_CODE_282,
	EVENT_UNKNOWN_TFT_CODE_283,
	EVENT_UNKNOWN_TFT_CODE_284,
	EVENT_UNKNOWN_TFT_CODE_285,

	// ===================================================
	// For use with TriggerRegisterUnitEvent
	// ===================================================

	EVENT_UNIT_SELL,
	EVENT_UNIT_CHANGE_OWNER,
	EVENT_UNIT_SELL_ITEM,
	EVENT_UNIT_SPELL_CHANNEL,
	EVENT_UNIT_SPELL_CAST,
	EVENT_UNIT_SPELL_EFFECT,
	EVENT_UNIT_SPELL_FINISH,
	EVENT_UNIT_SPELL_ENDCAST,
	EVENT_UNIT_PAWN_ITEM,

	// ===================================================
	// ===================================================
	// Below are for 1.32 emulation support
	EVENT_UNKNOWN_RF_CODE_295,
	EVENT_UNKNOWN_RF_CODE_296,
	EVENT_UNKNOWN_RF_CODE_297,
	EVENT_UNKNOWN_RF_CODE_298,
	EVENT_UNKNOWN_RF_CODE_299,
	EVENT_UNKNOWN_RF_CODE_300,
	EVENT_UNKNOWN_RF_CODE_301,
	EVENT_UNKNOWN_RF_CODE_302,
	EVENT_UNKNOWN_RF_CODE_303,
	EVENT_UNKNOWN_RF_CODE_304,

	// use TriggerRegisterPlayerEvent
	EVENT_PLAYER_MOUSE_DOWN,
	EVENT_PLAYER_MOUSE_UP,
	EVENT_PLAYER_MOUSE_MOVE,

	// use TriggerRegisterPlayerUnitEvent
	EVENT_PLAYER_UNIT_DAMAGED,

	// use TriggerRegisterPlayerEvent
	EVENT_PLAYER_SYNC_DATA,

	// use TriggerRegisterGameEvent
	EVENT_GAME_CUSTOM_UI_FRAME,

	// use TriggerRegisterPlayerEvent
	EVENT_PLAYER_KEY,
	EVENT_PLAYER_KEY_DOWN,
	EVENT_PLAYER_KEY_UP,

	// use TriggerRegisterUnitEvent
	EVENT_UNIT_DAMAGING,

	// use TriggerRegisterPlayerUnitEvent
	EVENT_PLAYER_UNIT_DAMAGING,

	EVENT_UNKNOWN_RF_CODE_316,
	EVENT_UNKNOWN_RF_CODE_317,

	// use TriggerRegisterUnitEvent
	EVENT_UNIT_STACK_ITEM,

	// use TriggerRegisterPlayerUnitEvent
	EVENT_PLAYER_UNIT_STACK_ITEM,;

	private static final int TFT_CUTOFF = EVENT_GAME_LOADED.ordinal();

	public static JassGameEventsWar3[] VALUES;
	static {
		final JassGameEventsWar3[] localValuesArray = values();
		final JassGameEventsWar3 endValue = localValuesArray[localValuesArray.length - 1];
		VALUES = new JassGameEventsWar3[endValue.getEventId() + 1];
		for (final JassGameEventsWar3 event : localValuesArray) {
			VALUES[event.getEventId()] = event;
		}
	}

	public int getEventId() {
		final int ordinal = ordinal();
		if (ordinal >= TFT_CUTOFF) {
			return (ordinal - TFT_CUTOFF) + 256;
		}
		return ordinal;
	}

	@Override
	public int getHandleId() {
		return getEventId();
	}
}

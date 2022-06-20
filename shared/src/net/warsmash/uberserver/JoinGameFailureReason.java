package net.warsmash.uberserver;

public enum JoinGameFailureReason {
	NO_SUCH_GAME, GAME_FULL, GAME_ALREADY_STARTED, SESSION_ERROR;

	public static JoinGameFailureReason VALUES[] = values();
}

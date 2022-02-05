package net.warsmash.uberserver;

public enum PasswordResetFailureReason {
	INVALID_CREDENTIALS, UNKNOWN_USER;

	public static PasswordResetFailureReason VALUES[] = values();
}

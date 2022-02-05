package com.etheller.warsmash.networking.uberserver.users;

public interface UserManager {
	User getUserByName(String username);

	void passwordReset(String username, char[] password, char[] newPassword, PasswordResetListener listener);

	User createUser(String username, char[] password);

	void notifyUsersUpdated();
}

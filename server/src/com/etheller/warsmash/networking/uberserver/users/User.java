package com.etheller.warsmash.networking.uberserver.users;

public interface User extends UserView {
	void setUsername(String username);

	void setPasswordHash(String token);

	void addExperience(int amount);

	void addWin(boolean ranked);

	void addLoss(boolean ranked);
}

package com.etheller.warsmash.networking.uberserver.users;

public interface UserView {
	String getUsername();

	String getPasswordHash();

	String getHash();

	UserStats getUserStats();

	UserRanking getUserRanking();

	int getLevel();

	int getExperience();

	int getId();
}

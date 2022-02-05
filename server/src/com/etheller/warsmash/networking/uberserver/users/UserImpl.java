package com.etheller.warsmash.networking.uberserver.users;

import java.util.ArrayList;
import java.util.List;

public final class UserImpl implements User {
	private String username;
	private String passwordHash;
	private String userHash;
	private final int id;
	private UserStats userStats;
	private UserRanking userRanking;
	private int level;
	private int experience;
	private final List<String> friendUsernames;

	private transient UserManager changeListener;

	public UserImpl(final String username, final String passwordHash, final int id, final String userHash,
			final UserManager changeListener) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.id = id;
		this.changeListener = changeListener;
		this.userStats = new UserStats();
		this.userRanking = new UserRanking();
		this.level = 1;
		this.experience = 0;
		this.friendUsernames = new ArrayList<>();
	}

	public void resumeTransientFields(final UserManager changeListener) {
		this.changeListener = changeListener;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public void setUsername(final String username) {
		this.username = username;
		this.changeListener.notifyUsersUpdated();
	}

	@Override
	public String getPasswordHash() {
		return this.passwordHash;
	}

	@Override
	public void setPasswordHash(final String passwordHash) {
		this.passwordHash = passwordHash;
		this.changeListener.notifyUsersUpdated();
	}

	@Override
	public UserStats getUserStats() {
		return this.userStats;
	}

	public void setUserStats(final UserStats userStats) {
		this.userStats = userStats;
		this.changeListener.notifyUsersUpdated();
	}

	@Override
	public UserRanking getUserRanking() {
		return this.userRanking;
	}

	public void setUserRanking(final UserRanking userRanking) {
		this.userRanking = userRanking;
		this.changeListener.notifyUsersUpdated();
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	public void setLevel(final int level) {
		this.level = level;
		this.changeListener.notifyUsersUpdated();
	}

	@Override
	public int getExperience() {
		return this.experience;
	}

	@Override
	public void addExperience(final int amount) {
		this.experience += amount;
		while (this.experience >= Math.pow(200, this.level + 1)) {
			this.level++;
		}
		this.changeListener.notifyUsersUpdated();
	}

	@Override
	public void addWin(final boolean ranked) {
		this.userStats.setGamesWon(this.userStats.getGamesWon() + 1);
		this.userStats.setGamesPlayed(this.userStats.getGamesPlayed() + 1);
		if (ranked) {
			this.userRanking.setRankedGamesPlayed(this.userRanking.getRankedGamesPlayed() + 1);
			this.userRanking.setRankedGamesWon(this.userRanking.getRankedGamesWon() + 1);
		}
		addExperience(225);
		// add experience will call the notifyUsersUpdated for us, for now
	}

	@Override
	public void addLoss(final boolean ranked) {
		this.userStats.setGamesLost(this.userStats.getGamesLost() + 1);
		this.userStats.setGamesPlayed(this.userStats.getGamesPlayed() + 1);
		if (ranked) {
			this.userRanking.setRankedGamesPlayed(this.userRanking.getRankedGamesPlayed() + 1);
			this.userRanking.setRankedGamesLost(this.userRanking.getRankedGamesLost() + 1);
		}
		addExperience(125);
		// add experience will call the notifyUsersUpdated for us, for now
	}

	public List<String> getFriendUsernames() {
		return this.friendUsernames;
	}

	public void addFriend(final String friendName) {
		this.friendUsernames.add(friendName);
		this.changeListener.notifyUsersUpdated();
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getHash() {
		return this.userHash;
	}
}

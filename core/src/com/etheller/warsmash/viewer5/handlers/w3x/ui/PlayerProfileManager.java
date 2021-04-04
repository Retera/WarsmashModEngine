package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PlayerProfileManager {
	private static final String CURRENT_PROFILE = "CurrentProfile";
	private static final String PROFILE_COUNT = "ProfileCount";
	private final Preferences preferences;
	private final List<PlayerProfile> profiles;
	private String currentProfile;

	public static PlayerProfileManager loadFromGdx() {
		final Preferences preferences = Gdx.app.getPreferences("WarsmashWC3Engine");
		final int profileCount = preferences.getInteger(PROFILE_COUNT);
		final List<PlayerProfile> profiles = new ArrayList<>();
		for (int i = 0; i < profileCount; i++) {
			final String name = preferences.getString("Profile" + i + "_Name");
			profiles.add(new PlayerProfile(name));
		}
		final String currentProfile = preferences.getString(CURRENT_PROFILE, "WorldEdit");
		if (profiles.isEmpty()) {
			final PlayerProfile worldEditDefaultProfile = new PlayerProfile("WorldEdit");
			saveProfile(preferences, profiles.size(), worldEditDefaultProfile);
			profiles.add(worldEditDefaultProfile);
			preferences.putInteger(PROFILE_COUNT, profiles.size());
			preferences.flush();
		}
		return new PlayerProfileManager(preferences, profiles, currentProfile);
	}

	public PlayerProfileManager(final Preferences preferences, final List<PlayerProfile> profiles,
			final String currentProfile) {
		this.preferences = preferences;
		this.profiles = profiles;
		this.currentProfile = currentProfile;
	}

	public List<PlayerProfile> getProfiles() {
		return this.profiles;
	}

	public PlayerProfile addProfile(final String name) {
		final PlayerProfile playerProfile = new PlayerProfile(name);
		saveProfile(this.preferences, this.profiles.size(), playerProfile);
		this.profiles.add(playerProfile);
		this.preferences.putInteger(PROFILE_COUNT, this.profiles.size());
		this.preferences.flush();
		return playerProfile;
	}

	public void setCurrentProfile(final String currentProfile) {
		this.currentProfile = currentProfile;
		this.preferences.putString(CURRENT_PROFILE, this.currentProfile);
		this.preferences.flush();
	}

	public String getCurrentProfile() {
		return this.currentProfile;
	}

	public void saveAll() {
		final int size = this.profiles.size();
		this.preferences.putInteger(PROFILE_COUNT, size);
		this.preferences.putString(CURRENT_PROFILE, this.currentProfile);
		for (int i = 0; i < size; i++) {
			final PlayerProfile playerProfile = this.profiles.get(i);
			saveProfile(this.preferences, i, playerProfile);
		}
		this.preferences.flush();
	}

	private static void saveProfile(final Preferences preferences, final int i, final PlayerProfile playerProfile) {
		preferences.putString("Profile" + i + "_Name", playerProfile.getName());
	}

	public boolean hasProfile(final String text) {
		for (final PlayerProfile profile : this.profiles) {
			if (profile.getName().equals(text)) {
				return true;
			}
		}
		return false;
	}

	public void removeProfile(final PlayerProfile profileToRemove) {
		this.profiles.remove(profileToRemove);
		saveAll();
	}
}

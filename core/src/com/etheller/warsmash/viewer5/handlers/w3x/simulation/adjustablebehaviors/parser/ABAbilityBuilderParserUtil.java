package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAbilityBuilderGsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class ABAbilityBuilderParserUtil {
	public static void loadAbilityBuilderFiles(final AbilityBuilderFileListener listener) {
		loadAbilityBuilderFiles(listener, "abilityBehaviors");
	}
	public static void loadAbilityBuilderFiles(final AbilityBuilderFileListener listener, String folder) {
		final Gson gson = ABAbilityBuilderGsonBuilder.create();
		try {
			final File abilityBehaviorsDir = new File(folder);
			final File[] abilityBehaviorFiles = abilityBehaviorsDir.listFiles();
			if (abilityBehaviorFiles != null) {
				for (final File abilityBehaviorFile : abilityBehaviorFiles) {
					loadAbilityBuilderFile(gson, abilityBehaviorFile, listener);
				}
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadAbilityBuilderFile(final Gson gson, final File abilityBehaviorFile,
			final AbilityBuilderFileListener listener) throws FileNotFoundException {
		if (abilityBehaviorFile.isDirectory()) {
			final File[] abilityBehaviorFiles = abilityBehaviorFile.listFiles();
			if (abilityBehaviorFiles != null) {
				for (final File subAbilityBehaviorFile : abilityBehaviorFiles) {
					loadAbilityBuilderFile(gson, subAbilityBehaviorFile, listener);
				}
			}
		}
		try {
			final ABAbilityBuilderFile behaviors = gson.fromJson(new FileReader(abilityBehaviorFile),
					ABAbilityBuilderFile.class);
			for (final ABAbilityBuilderParser behavior : behaviors.getAbilityList()) {
				listener.callback(behavior);
			}
		}
		catch (final JsonParseException e) {
			System.err.println("Failed to load Ability Builder config file: " + abilityBehaviorFile.getName());
			e.printStackTrace();
		}
		catch (final IllegalArgumentException e) {
			System.err.println("Failed to load Ability Builder config file: " + abilityBehaviorFile.getName());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println("Failed to find Ability Builder config file: " + abilityBehaviorFile.getName());
			e.printStackTrace();
		}
	}

	public static interface AbilityBuilderFileListener {
		void callback(ABAbilityBuilderParser behavior);
	}
}

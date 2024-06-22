package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.AbilityBuilderGsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class AbilityBuilderParserUtil {
	public static void loadAbilityBuilderFiles(final AbilityBuilderFileListener listener) {
		final Gson gson = AbilityBuilderGsonBuilder.create();
		try {
			final File abilityBehaviorsDir = new File("abilityBehaviors");
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
			final AbilityBuilderFile behaviors = gson.fromJson(new FileReader(abilityBehaviorFile),
					AbilityBuilderFile.class);
			for (final AbilityBuilderParser behavior : behaviors.getAbilityList()) {
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
		}
	}

	public static interface AbilityBuilderFileListener {
		void callback(AbilityBuilderParser behavior);
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.AbilityBuilderGsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class AbilityBuilderParserUtil {
	public static void loadAbilityBuilderFiles(AbilityBuilderFileListener listener) {
		Gson gson = AbilityBuilderGsonBuilder.create();
		try {
			File abilityBehaviorsDir = new File("abilityBehaviors");
			File[] abilityBehaviorFiles = abilityBehaviorsDir.listFiles();
			if (abilityBehaviorFiles != null) {
				for (File abilityBehaviorFile : abilityBehaviorFiles) {
					loadAbilityBuilderFile(gson, abilityBehaviorFile, listener);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadAbilityBuilderFile(Gson gson, File abilityBehaviorFile, AbilityBuilderFileListener listener)
			throws FileNotFoundException {
		try {
			AbilityBuilderFile behaviors = gson.fromJson(new FileReader(abilityBehaviorFile), AbilityBuilderFile.class);
			for (AbilityBuilderParser behavior : behaviors.getAbilityList()) {
				listener.callback(behavior);
			}
		} catch (JsonParseException e) {
			System.err.println("Failed to load Ability Builder config file: " + abilityBehaviorFile.getName());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("Failed to load Ability Builder config file: " + abilityBehaviorFile.getName());
			e.printStackTrace();
		}
	}

	public static interface AbilityBuilderFileListener {
		void callback(AbilityBuilderParser behavior);
	}
}

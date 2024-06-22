package com.etheller.warsmash.desktop.editor.abilitybuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.AbilityBuilderGsonBuilder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderDupe;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParserUtil;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderType;
import com.google.gson.Gson;

public class AbilityBuilderJassBrainstorm {

	public static void main(final String[] args) {
		final Gson gson = AbilityBuilderGsonBuilder.create();
		final File neHeroAbilsFile = new File("abilityBehaviors/nightElfHeroUnitActives.json");
		try {
			AbilityBuilderParserUtil.loadAbilityBuilderFile(gson, neHeroAbilsFile, behavior -> {
				if (behavior.getType() == AbilityBuilderType.TEMPLATE) {
					for (final AbilityBuilderDupe dupe : behavior.getIds()) {
						System.out.println("//template: " + dupe.getId());
//					idsListModel.addElement(new AbilityBuilderConfiguration(behavior, dupe));
					}
				}
				else {
					for (final AbilityBuilderDupe dupe : behavior.getIds()) {
						final AbilityBuilderConfiguration abilityBuilderConfiguration = new AbilityBuilderConfiguration(
								behavior, dupe);
						generateJassForConf(System.out, dupe, abilityBuilderConfiguration);

//					idsListModel.addElement(new AbilityBuilderConfiguration(behavior, dupe));
					}
				}
			});
		}
		catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void generateFunctions(final PrintStream out, final StringBuilder initCode,
			final List<ABAction> actions, final String functionName) {
		if ((actions != null) && !actions.isEmpty()) {
			out.println("function " + functionName + " takes nothing returns nothing");

			for (final ABAction action : actions) {

			}

			out.println("endfunction");
			out.println();

			final String funcSuffix = functionName.substring(functionName.lastIndexOf("On") + 2);
			initCode.append("    call AddABConf" + funcSuffix + "Action(abc, function " + functionName + ")\n");
		}
	}

	private static void generateJassForConf(final PrintStream out, final AbilityBuilderDupe dupe,
			final AbilityBuilderConfiguration abilityBuilderConfiguration) {

		String abilityName = null;
		final StringBuilder initCode = new StringBuilder();
		initCode.append("    local abilitybuilderconfiguration abc = CreateAbilityBuilderConfiguration()\n");
		{
			final String castId = abilityBuilderConfiguration.getCastId();
			if (castId != null) {
				initCode.append("    call SetABConfCastId(abc, \"" + castId + "\")\n");
				if (abilityName == null) {
					abilityName = castId;
				}
			}
		}
		{
			final String castId = abilityBuilderConfiguration.getUncastId();
			if (castId != null) {
				initCode.append("    call SetABConfUncastId(abc, \"" + castId + "\")\n");
				if (abilityName == null) {
					abilityName = castId;
				}
			}
		}
		{
			final String castId = abilityBuilderConfiguration.getAutoCastOnId();
			if (castId != null) {
				initCode.append("    call SetABConfAutoCastOnId(abc, \"" + castId + "\")\n");
				if (abilityName == null) {
					abilityName = castId;
				}
			}
		}
		{
			final String castId = abilityBuilderConfiguration.getAutoCastOffId();
			if (castId != null) {
				initCode.append("    call SetABConfAutoCastOffId(abc, \"" + castId + "\")\n");
				if (abilityName == null) {
					abilityName = castId;
				}
			}
		}
		final AutocastType autoCastType = abilityBuilderConfiguration.getAutoCastType();
		if (autoCastType != null) {
			initCode.append("    call SetABConfAutoCastType(abc, AUTOCAST_TYPE_" + autoCastType.name() + ")\n");
		}
		final AbilityBuilderType type = abilityBuilderConfiguration.getType();
		if (type != null) {
			initCode.append("    call SetABConfType(abc, AB_CONF_TYPE_" + type.name() + ")\n");
		}

		if (abilityName != null) {
			abilityName = Character.toUpperCase(abilityName.charAt(0)) + abilityName.substring(1);
		}

//		private List<ABAction> onAddAbility;
//		private List<ABAction> onAddDisabledAbility;
//		private List<ABAction> onRemoveAbility;
//		private List<ABAction> onRemoveDisabledAbility;
//
//		private List<ABAction> onDeathPreCast;
//		private List<ABAction> onCancelPreCast;
//		private List<ABAction> onOrderIssued;
//		private List<ABAction> onActivate;
//		private List<ABAction> onDeactivate;
//
//		private List<ABAction> onLevelChange;
//
//		private List<ABAction> onBeginCasting;
//		private List<ABAction> onEndCasting;
//		private List<ABAction> onChannelTick;
//		private List<ABAction> onEndChannel;
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnAddAbility(), abilityName + "_OnAddAbility");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnAddDisabledAbility(),
				abilityName + "_OnAddDisabledAbility");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnRemoveAbility(),
				abilityName + "_OnRemoveAbility");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnRemoveDisabledAbility(),
				abilityName + "_OnRemoveDisabledAbility");

		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnDeathPreCast(),
				abilityName + "_OnDeathPreCast");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnCancelPreCast(),
				abilityName + "_OnCancelPreCast");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnOrderIssued(),
				abilityName + "_OnOrderIssued");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnActivate(), abilityName + "_OnActivate");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnDeactivate(), abilityName + "_OnDeactivate");

		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnLevelChange(),
				abilityName + "_OnLevelChange");

		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnBeginCasting(),
				abilityName + "_OnBeginCasting");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnEndCasting(), abilityName + "_OnEndCasting");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnChannelTick(),
				abilityName + "_OnChannelTick");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnEndChannel(), abilityName + "_OnEndChannel");

		initCode.append("    call RegisterABConf('" + dupe.getId() + "', abc)\n");

		out.println("function main takes nothing returns nothing");
		out.print(initCode.toString());
		out.println("endfunction");
	}

}

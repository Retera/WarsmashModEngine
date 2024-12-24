package com.etheller.warsmash.desktop.editor.abilitybuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorImpl1;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.AbilityBuilderGsonBuilder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderDupe;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParser;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParserUtil;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParserUtil.AbilityBuilderFileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderType;
import com.google.gson.Gson;

public class AbilityBuilderJassBrainstorm {

	public static void main(final String[] args) {
		final Gson gson = AbilityBuilderGsonBuilder.create();
		AbilityBuilderParserUtil.loadAbilityBuilderFiles(new AbilityBuilderFileListener() {
			@Override
			public void callback(final AbilityBuilderParser behavior) {
				System.out.println(behavior);
				convertTheThing(behavior);
			}
		});
	}

	private static void convertTheThing(final AbilityBuilderParser behavior) {
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
				try {
					generateJassForConf(dupe, abilityBuilderConfiguration);
				}
				catch (final Throwable exc) {
					exc.printStackTrace();
				}

//					idsListModel.addElement(new AbilityBuilderConfiguration(behavior, dupe));
			}
		}
	}

	private static void generateFunctions(final JassTextGenerator out, final StringBuilder initCode,
			final List<ABAction> actions, final String functionName) {
		if ((actions != null) && !actions.isEmpty()) {

			final String finalFuncName = out.createAnonymousFunction(actions, functionName);

			final String funcSuffix = functionName.substring(functionName.lastIndexOf("On") + 2);
			initCode.append("    call AddABConf" + funcSuffix + "Action(abc, function " + finalFuncName + ")\n");
		}
	}

	private static void generateJassForConf(final AbilityBuilderDupe dupe,
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
		final JassTextGeneratorImpl1 out = new JassTextGeneratorImpl1(abilityName);
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnAddAbility(), "OnAddAbility");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnAddDisabledAbility(), "OnAddDisabledAbility");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnRemoveAbility(), "OnRemoveAbility");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnRemoveDisabledAbility(),
				"OnRemoveDisabledAbility");

		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnDeathPreCast(), "OnDeathPreCast");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnCancelPreCast(), "OnCancelPreCast");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnOrderIssued(), "OnOrderIssued");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnActivate(), "OnActivate");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnDeactivate(), "OnDeactivate");

		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnLevelChange(), "OnLevelChange");

		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnBeginCasting(), "OnBeginCasting");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnEndCasting(), "OnEndCasting");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnChannelTick(), "OnChannelTick");
		generateFunctions(out, initCode, abilityBuilderConfiguration.getOnEndChannel(), "OnEndChannel");

		try (PrintWriter outStream = new PrintWriter(new File("GeneratedJass/" + abilityName + ".j"))) {
			outStream.println("//==============================================================");
			outStream.println("//                           " + abilityName);
			outStream.println("//==============================================================");
			outStream.println("// Generated by Ability Builder JSON -> JASS");
			outStream.println("// converted on " + new Date().toString());
			outStream.println("//");

			out.finish(outStream);

			initCode.append("    call RegisterABConf('" + dupe.getId() + "', abc)\n");

			outStream.println("function main takes nothing returns nothing");
			outStream.print(initCode.toString());
			outStream.println("endfunction");
		}
		catch (final FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}

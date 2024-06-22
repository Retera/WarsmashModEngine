package com.etheller.warsmash.parsers.jass;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public interface JassTextGenerator {
	void prependFunction(StringBuilder functionContents);

	public String getUserData(String key, JassTextGeneratorType type);

	public String getCastId();

	String getCaster();

	String getAbility();

	public String setUserData(String key, JassTextGeneratorType type, String value);

	/**
	 * Creates a jass function containing the given actions
	 *
	 * @param actions
	 * @return name for anonymously generated function
	 */
	public String createAnonymousFunction(List<ABAction> actions, String nameSuggestion);

	/**
	 * Probably only works on "takes nothing returns nothing" function, but is not a
	 * code func. Probably implemented by returning "arg()" for a given "arg".
	 *
	 * @param function name of anonymously generated function (see
	 *                 "createAnonymousFunction")
	 * @return a call expression that calls the given function
	 */
	public String callAnonymousFunctionByName(String functionName);

	public String functionPointerByName(String functionName);

	public String createAndCallFunction(List<ABAction> actions);

	class Util {
		public static void indent(final int indent, final StringBuilder sb) {
			for (int i = 0; i < (indent * 4); i++) {
				sb.append(' ');
			}
		}

		public static String getUserData(final String key) {
			return "GetSpellUserData(GetSpellAbility())." + key;
		}

		public static String setUserData(final String key, final String value) {
			return "set GetSpellUserData(GetSpellAbility())." + key + " = " + value;
		}
	}
}

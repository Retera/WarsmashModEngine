package com.etheller.warsmash.parsers.jass;

import java.util.List;

public interface JassTextGenerator {
	// void prependFunction(StringBuilder functionContents);

	public String getUserData(String key, JassTextGeneratorType type);

	public String getUserDataExpr(String keyExpr, JassTextGeneratorType type);

	public String getCastId();

	String getCaster();

	String getAbility();

	String getTriggerLocalStore();

	public String setUserData(String key, JassTextGeneratorType type, String value);

	public String setUserDataExpr(String keyExpr, JassTextGeneratorType type, String value);

	String declareLocal(String type, String name);

	/**
	 * Creates a jass function containing the given actions
	 *
	 * @param actions
	 * @return name for anonymously generated function
	 */
	public String createAnonymousFunction(List<? extends JassTextGeneratorStmt> actions, String nameSuggestion);

	public String createAnonymousBooleanFunction(List<? extends JassTextGeneratorExpr> actions, String nameSuggestion);

	/**
	 * Probably only works on "takes nothing returns nothing" function, but is not a
	 * code func. Probably implemented by returning "arg()" for a given "arg".
	 *
	 * @param function name of anonymously generated function (see
	 *                 "createAnonymousFunction")
	 * @return a call expression that calls the given function
	 */
	// public String callAnonymousFunctionByName(String functionName);

	public String functionPointerByName(String functionName);

	void println(String line);

	class Util {
		public static void indent(final int indent, final StringBuilder sb) {
			for (int i = 0, l = indent * 4; i < l; i++) {
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

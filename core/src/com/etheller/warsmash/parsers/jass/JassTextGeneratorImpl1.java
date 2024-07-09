package com.etheller.warsmash.parsers.jass;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class JassTextGeneratorImpl1 implements JassTextGenerator {
	private final String abilityName;
	private int localIndex = 0;
	private int nextUniqueFuncId = 0;
	private LinkedList<String> lines = new LinkedList<>();

	public JassTextGeneratorImpl1(final String abilityName) {
		this.abilityName = abilityName;
	}

	@Override
	public String getUserData(final String key, final JassTextGeneratorType type) {
		return "GetLocalStore" + type.toString() + "(" + getTriggerLocalStore() + ", \"" + key + "\")";
	}

	@Override
	public String getUserDataExpr(final String keyExpr, final JassTextGeneratorType type) {
		return "GetLocalStore" + type.toString() + "(" + getTriggerLocalStore() + ", " + keyExpr + ")";
	}

	@Override
	public String getCastId() {
		return "castId";// "GetTriggerCastId()";
	}

	@Override
	public String getCaster() {
		return "caster"; // "GetSpellAbilityUnit()";
	}

	@Override
	public String getAbility() {
		return getUserDataExpr("AB_LOCAL_STORE_KEY_ABILITY", JassTextGeneratorType.AbilityHandle);
	}

	@Override
	public String getTriggerLocalStore() {
		return "myLocalStore";// "GetTriggerLocalStore()";
	}

	@Override
	public String setUserData(final String key, final JassTextGeneratorType type, final String value) {
		return "SetLocalStore" + type.toString() + "(" + getTriggerLocalStore() + ", \"" + key + "\", " + value + ")";
	}

	@Override
	public String setUserDataExpr(final String keyExpr, final JassTextGeneratorType type, final String value) {
		return "SetLocalStore" + type.toString() + "(" + getTriggerLocalStore() + ", " + keyExpr + ", " + value + ")";
	}

	@Override
	public String declareLocal(String type, String name) {
		this.lines.add(this.localIndex++, "    local " + type + " " + name);
		return name;
	}

	@Override
	public void println(final String line) {
		this.lines.add(line);
	}

	@Override
	public String createAnonymousFunction(final List<? extends JassTextGeneratorStmt> actions,
			final String nameSuggestion) {
		if (actions == null) {
			return null;
		}
		final LinkedList<String> localLines = new LinkedList<>();
		final String functionName = this.abilityName + "_" + nameSuggestion + "_Func"
				+ String.format("%3s", this.nextUniqueFuncId++).replace(" ", "0");
		localLines.add("function " + functionName + " takes nothing returns nothing");
		localLines.add("    local unit caster = GetSpellAbilityUnit()");
		localLines.add("    local localstore myLocalStore = GetTriggerLocalStore()");
		localLines.add("    local integer castId = GetTriggerCastId()");

		final LinkedList<String> prevLines = this.lines;
		this.lines = localLines;
		for (final JassTextGeneratorStmt action : actions) {
			action.generateJassEquivalent(1, this);
		}
		this.lines = prevLines;

		localLines.add("endfunction");
		localLines.add("");
		this.lines.addAll(0, localLines);
		return functionName;
	}

	@Override
	public String createAnonymousBooleanFunction(final List<? extends JassTextGeneratorExpr> conditions,
			final String nameSuggestion) {
		final LinkedList<String> localLines = new LinkedList<>();
		final String functionName = this.abilityName + "_" + nameSuggestion + "_Func"
				+ String.format("%3s", this.nextUniqueFuncId++).replace(" ", "0");
		localLines.add("function " + functionName + " takes nothing returns nothing");
		localLines.add("    local localstore myLocalStore = GetTriggerLocalStore()");

		final LinkedList<String> prevLines = this.lines;
		this.lines = localLines;
		for (final JassTextGeneratorExpr condition : conditions) {
			this.lines.add("    if not " + condition.generateJassEquivalent(this) + " then");
			this.lines.add("        return false");
			this.lines.add("    endif");
		}
		this.lines = prevLines;

		localLines.add("endfunction");
		localLines.add("");
		this.lines.addAll(0, localLines);
		return functionName;
	}

	@Override
	public String functionPointerByName(final String functionName) {
		if (functionName == null) {
			return "null";
		}
		return "function " + functionName;
	}

	public void finish(final PrintWriter out) {
		for (final String line : this.lines) {
			out.println(line);
		}
	}

}

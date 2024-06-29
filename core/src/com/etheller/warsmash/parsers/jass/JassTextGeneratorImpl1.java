package com.etheller.warsmash.parsers.jass;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class JassTextGeneratorImpl1 implements JassTextGenerator {
	private int nextUniqueFuncId = 0;
	private LinkedList<String> lines = new LinkedList<>();

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
		return "GetTriggerCastId()";
	}

	@Override
	public String getCaster() {
		return "GetTriggerUnit()";
	}

	@Override
	public String getAbility() {
		return getUserData("AB_LOCAL_STORE_KEY_ABILITY", JassTextGeneratorType.Ability);
	}

	@Override
	public String getTriggerLocalStore() {
		return "GetTriggerLocalStore()";
	}

	@Override
	public String setUserData(final String key, final JassTextGeneratorType type, final String value) {
		return "SetLocalStore" + type.toString() + "(" + getTriggerLocalStore() + ", " + key + ", " + value + ")";
	}

	@Override
	public String createAnonymousFunction(final List<? extends JassTextGeneratorExpr> actions,
			final String nameSuggestion) {
		if (actions == null) {
			return null;
		}
		final LinkedList<String> localLines = new LinkedList<>();
		final String functionName = nameSuggestion + "_Func"
				+ String.format("%3s", this.nextUniqueFuncId++).replace(" ", "0");
		this.lines.add("function " + functionName + " takes nothing returns nothing");

		final LinkedList<String> prevLines = this.lines;
		this.lines = localLines;
		for (final JassTextGeneratorExpr action : actions) {
			this.lines.add("    call " + action.generateJassEquivalent(this));
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
		final String functionName = nameSuggestion + "_Func"
				+ String.format("%3s", this.nextUniqueFuncId++).replace(" ", "0");
		this.lines.add("function " + functionName + " takes nothing returns nothing");

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

	public void finish(final PrintStream out) {
		for (final String line : this.lines) {
			out.println(line);
		}
	}

}

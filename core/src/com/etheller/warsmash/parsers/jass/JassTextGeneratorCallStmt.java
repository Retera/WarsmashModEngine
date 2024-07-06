package com.etheller.warsmash.parsers.jass;

public interface JassTextGeneratorCallStmt extends JassTextGeneratorStmt {
	String generateJassEquivalent(JassTextGenerator jassTextGenerator);

	@Override
	default void generateJassEquivalent(final int indent, final JassTextGenerator jassTextGenerator) {
		final StringBuilder sb = new StringBuilder();
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("call ");
		sb.append(generateJassEquivalent(jassTextGenerator));
		jassTextGenerator.println(sb.toString());
	}
}

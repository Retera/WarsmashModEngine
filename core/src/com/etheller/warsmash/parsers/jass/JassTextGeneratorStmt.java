package com.etheller.warsmash.parsers.jass;

public interface JassTextGeneratorStmt {
	default void generateJassEquivalent(final int indent, final JassTextGenerator jassTextGenerator) {
		throw new RuntimeException();
	}
}

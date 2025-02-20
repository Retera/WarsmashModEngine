package com.etheller.warsmash.parsers.jass;

public interface JassTextGeneratorExpr {
	default String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		throw new RuntimeException();
	}
}

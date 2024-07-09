package com.etheller.warsmash.parsers.jass;

public interface JassTextGeneratorExpr {
	default String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		throw new IllegalStateException();
	}
}

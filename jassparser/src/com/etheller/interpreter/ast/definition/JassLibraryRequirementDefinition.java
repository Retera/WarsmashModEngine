package com.etheller.interpreter.ast.definition;

public class JassLibraryRequirementDefinition {
	private final String requirement;
	private final boolean optional;

	public JassLibraryRequirementDefinition(final String requirement, final boolean optional) {
		this.requirement = requirement;
		this.optional = optional;
	}

	public String getRequirement() {
		return this.requirement;
	}

	public boolean isOptional() {
		return this.optional;
	}
}

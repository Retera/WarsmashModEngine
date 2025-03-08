package com.etheller.interpreter.ast.definition;

public class JassImplementModuleDefinition {
	private final String moduleName;
	private final boolean optional;

	public JassImplementModuleDefinition(final String moduleName, final boolean optional) {
		this.moduleName = moduleName;
		this.optional = optional;
	}

	public String getModuleName() {
		return this.moduleName;
	}

	public boolean isOptional() {
		return this.optional;
	}
}

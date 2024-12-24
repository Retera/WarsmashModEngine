package com.etheller.interpreter.ast.value;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.definition.JassFunctionDefinitionBlock;
import com.etheller.interpreter.ast.struct.JassStructMemberType;

public class JassStructStatements {
	private final List<JassStructMemberType> memberTypes = new ArrayList<>();
	private final List<JassFunctionDefinitionBlock> methods = new ArrayList<>();

	public void add(final JassStructMemberType memberType) {
		this.memberTypes.add(memberType);
	}

	public void add(final JassFunctionDefinitionBlock method) {
		this.methods.add(method);
	}

	public List<JassStructMemberType> getMemberTypes() {
		return this.memberTypes;
	}

	public List<JassFunctionDefinitionBlock> getMethods() {
		return this.methods;
	}
}

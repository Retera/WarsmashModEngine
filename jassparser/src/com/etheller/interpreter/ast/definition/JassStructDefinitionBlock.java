package com.etheller.interpreter.ast.definition;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.struct.JassStructMemberTypeDefinition;
import com.etheller.interpreter.ast.type.JassTypeToken;
import com.etheller.interpreter.ast.util.JassProgram;

public class JassStructDefinitionBlock implements JassDefinitionBlock {
	private final String structName;
	private final JassTypeToken structSuperTypeToken;
	private final List<JassStructMemberTypeDefinition> memberTypeDefinitions = new ArrayList<>();
	private final List<JassMethodDefinitionBlock> methodDefinitions = new ArrayList<>();

	public JassStructDefinitionBlock(final String structName, final JassTypeToken structSuperTypeToken) {
		this.structName = structName;
		this.structSuperTypeToken = structSuperTypeToken;
	}

	public void add(final JassStructMemberTypeDefinition memberType) {
		this.memberTypeDefinitions.add(memberType);
	}

	public void add(final JassMethodDefinitionBlock methodDefinition) {
		this.methodDefinitions.add(methodDefinition);
	}

	@Override
	public void define(final String mangledNameScope, final JassProgram jassProgram) {
		jassProgram.globalScope.defineStruct(this.structName, this.structSuperTypeToken, this.memberTypeDefinitions,
				this.methodDefinitions, mangledNameScope);
	}

}

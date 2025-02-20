package com.etheller.interpreter.ast.definition;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.struct.JassStructMemberTypeDefinition;
import com.etheller.interpreter.ast.util.JassProgram;

public class JassModuleDefinitionBlock implements JassDefinitionBlock, JassStructLikeDefinitionBlock {
	private final EnumSet<JassQualifier> qualifiers;
	private final String name;
	private final List<JassStructMemberTypeDefinition> memberTypeDefinitions = new ArrayList<>();
	private final List<JassImplementModuleDefinition> implementModuleDefinitions = new ArrayList<>();
	private final List<JassMethodDefinitionBlock> methodDefinitions = new ArrayList<>();

	public JassModuleDefinitionBlock(final EnumSet<JassQualifier> qualifiers, final String structName) {
		this.qualifiers = qualifiers;
		this.name = structName;
	}

	@Override
	public void add(final JassStructMemberTypeDefinition memberType) {
		this.memberTypeDefinitions.add(memberType);
	}

	@Override
	public void add(final JassMethodDefinitionBlock methodDefinition) {
		this.methodDefinitions.add(methodDefinition);
	}

	@Override
	public void add(final JassImplementModuleDefinition implementModuleDefinition) {
		this.implementModuleDefinitions.add(implementModuleDefinition);
	}

	@Override
	public void define(final Scope scope, final JassProgram jassProgram) {
		scope.defineModule(this);
	}

	public EnumSet<JassQualifier> getQualifiers() {
		return this.qualifiers;
	}

	public String getName() {
		return this.name;
	}

	public List<JassStructMemberTypeDefinition> getMemberTypeDefinitions() {
		return this.memberTypeDefinitions;
	}

	public List<JassMethodDefinitionBlock> getMethodDefinitions() {
		return this.methodDefinitions;
	}

	public List<JassImplementModuleDefinition> getImplementModuleDefinitions() {
		return this.implementModuleDefinitions;
	}

}

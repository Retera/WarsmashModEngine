package com.etheller.interpreter.ast.definition;

import com.etheller.interpreter.ast.struct.JassStructMemberTypeDefinition;

public interface JassStructLikeDefinitionBlock {
	public void add(final JassStructMemberTypeDefinition memberType);

	public void add(final JassMethodDefinitionBlock methodDefinition);

	public void add(final JassImplementModuleDefinition implementModuleDefinition);
}

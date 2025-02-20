package com.etheller.interpreter.ast.scope;

import java.util.EnumSet;
import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.definition.JassImplementModuleDefinition;
import com.etheller.interpreter.ast.definition.JassMethodDefinitionBlock;
import com.etheller.interpreter.ast.definition.JassModuleDefinitionBlock;
import com.etheller.interpreter.ast.function.NativeJassFunction;
import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.scope.LibraryScopeTree.ScopeTreeHandler;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.struct.JassStructMemberTypeDefinition;
import com.etheller.interpreter.ast.type.JassTypeToken;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;

public class StructScope implements Scope {
	private final Scope parent;
	private final String structName;

	public StructScope(final Scope parent, final String structName) {
		this.parent = parent;
		this.structName = structName;
	}

	@Override
	public JassType parseArrayType(final String id) {
		if (GlobalScope.KEYWORD_THISTYPE.equals(id)) {
			return this.parent.parseArrayType(this.structName);
		}
		return this.parent.parseArrayType(id);
	}

	@Override
	public JassType parseType(final String id) {
		if (GlobalScope.KEYWORD_THISTYPE.equals(id)) {
			return this.parent.parseType(this.structName);
		}
		return this.parent.parseType(id);
	}

	@Override
	public int getGlobalId(final String identifier) {
		if (GlobalScope.KEYWORD_THISTYPE.equals(identifier)) {
			return this.parent.getGlobalId(this.structName);
		}
		return this.parent.getGlobalId(identifier);
	}

	@Override
	public void createGlobalArray(final EnumSet<JassQualifier> qualifiers, final String identifier,
			final JassType type) {
		this.parent.createGlobalArray(qualifiers, identifier, type);
	}

	@Override
	public void createGlobal(final EnumSet<JassQualifier> qualifiers, final String identifier, final JassType type) {
		this.parent.createGlobal(qualifiers, identifier, type);
	}

	@Override
	public void createGlobal(final EnumSet<JassQualifier> qualifiers, final String identifier, final JassType type,
			final JassValue value) {
		this.parent.createGlobal(qualifiers, identifier, type, value);
	}

	@Override
	public GlobalScopeAssignable getAssignableGlobal(final String identifier) {
		if (GlobalScope.KEYWORD_THISTYPE.equals(identifier)) {
			return this.parent.getAssignableGlobal(this.structName);
		}
		return this.parent.getAssignableGlobal(identifier);
	}

	@Override
	public UserJassFunction getFunctionDefinitionByName(final String functionName) {
		return this.parent.getFunctionDefinitionByName(functionName);
	}

	@Override
	public GlobalScopeAssignable getAssignableGlobalById(final int globalId) {
		return this.parent.getAssignableGlobalById(globalId);
	}

	@Override
	public Integer getUserFunctionInstructionPtr(final String functionName) {
		return this.parent.getUserFunctionInstructionPtr(functionName);
	}

	@Override
	public Integer getNativeId(final String functionName) {
		return this.parent.getNativeId(functionName);
	}

	@Override
	public NativeJassFunction getNative(final String functionName) {
		return this.parent.getNative(functionName);
	}

	@Override
	public JassException createException(final String message, final Exception cause) {
		return this.parent.createException(message, cause);
	}

	@Override
	public void defineFunction(final int lineNo, final String sourceFile, final String name,
			final UserJassFunction function, final Scope scope) {
		this.parent.defineFunction(lineNo, sourceFile, name, function, scope);
	}

	@Override
	public int defineMethod(final int lineNo, final String sourceFile, final String name, final UserJassFunction method,
			final StructJassType structJassType, final Scope scope) {
		return this.parent.defineMethod(lineNo, sourceFile, name, method, structJassType, scope);
	}

	@Override
	public void defineGlobals(final int lineNo, final String file, final List<JassStatement> globalStatements,
			final Scope scope) {
		this.parent.defineGlobals(lineNo, file, globalStatements, scope);
	}

	@Override
	public Scope createNestedScope(final String namespace, final boolean library) {
		return this.parent.createNestedScope(namespace, library);
	}

	@Override
	public void loadTypeDefinition(final String id, final String supertype) {
		this.parent.loadTypeDefinition(id, supertype);
	}

	@Override
	public void defineStruct(final EnumSet<JassQualifier> qualifiers, final String structName,
			final JassTypeToken structSuperTypeToken, final List<JassStructMemberTypeDefinition> memberTypeDefinitions,
			final List<JassImplementModuleDefinition> implementModuleDefinitions,
			final List<JassMethodDefinitionBlock> methodDefinitions, final Scope scope) {
		this.parent.defineStruct(qualifiers, structName, structSuperTypeToken, memberTypeDefinitions,
				implementModuleDefinitions, methodDefinitions, scope);
	}

	@Override
	public void defineModule(final JassModuleDefinitionBlock jassModuleDefinitionBlock) {
		this.parent.defineModule(jassModuleDefinitionBlock);
	}

	@Override
	public void defineFunction(final int lineNo, final String sourceFile, final String name,
			final NativeJassFunction nativeJassFunction) {
		this.parent.defineFunction(lineNo, sourceFile, name, nativeJassFunction);
	}

	@Override
	public JassValue getPreprocessorConstant(final String identifier) {
		return this.parent.getPreprocessorConstant(identifier);
	}

	@Override
	public <T> T forEachPossibleResolvedIdentifier(final String identifier, final ScopeTreeHandler<T> handler) {
		return this.parent.forEachPossibleResolvedIdentifier(identifier, handler);
	}

}

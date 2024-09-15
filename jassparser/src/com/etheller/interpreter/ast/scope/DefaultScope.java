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

public class DefaultScope implements Scope {
	private final GlobalScope globalScope;

	public DefaultScope(final GlobalScope globalScope) {
		this.globalScope = globalScope;
	}

	@Override
	public GlobalScopeAssignable getAssignableGlobalById(final int globalId) {
		return this.globalScope.getAssignableGlobalById(globalId);
	}

	@Override
	public int getGlobalId(final String name) {
		return this.globalScope.getGlobalId(name);
	}

	@Override
	public GlobalScopeAssignable getAssignableGlobal(final String name) {
		return this.globalScope.getAssignableGlobal(name);
	}

	@Override
	public void defineFunction(final int lineNo, final String sourceFile, final String name,
			final NativeJassFunction function) {
		this.globalScope.defineFunction(lineNo, sourceFile, name, function);
	}

	@Override
	public int defineMethod(final int lineNo, final String sourceFile, final String name,
			final UserJassFunction function, final StructJassType type, final Scope scope) {
		return this.globalScope.defineMethod(lineNo, sourceFile, name, function, type, scope);
	}

	@Override
	public void defineFunction(final int lineNo, final String sourceFile, final String name,
			final UserJassFunction function, final Scope scope) {
		this.globalScope.defineFunction(lineNo, sourceFile, name, function, scope);
	}

	@Override
	public void defineGlobals(final int lineNo, final String sourceFile, final List<JassStatement> globalStatements,
			final Scope scope) {
		this.globalScope.defineGlobals(lineNo, sourceFile, globalStatements, scope);
	}

	@Override
	public void defineStruct(final EnumSet<JassQualifier> qualifiers, final String structName,
			final JassTypeToken structSuperTypeToken, final List<JassStructMemberTypeDefinition> memberTypeDefinitions,
			final List<JassImplementModuleDefinition> implementModuleDefinitions,
			final List<JassMethodDefinitionBlock> methodDefinitions, final Scope scope) {
		this.globalScope.defineStruct(qualifiers, structName, structSuperTypeToken, memberTypeDefinitions,
				implementModuleDefinitions, methodDefinitions, scope);
	}

	@Override
	public void defineModule(final JassModuleDefinitionBlock jassModuleDefinitionBlock) {
		this.globalScope.defineModule(jassModuleDefinitionBlock.getName(), jassModuleDefinitionBlock);
	}

	@Override
	public UserJassFunction getFunctionDefinitionByName(final String name) {
		return this.globalScope.getFunctionDefinitionByName(name);
	}

	@Override
	public Integer getUserFunctionInstructionPtr(final String name) {
		return this.globalScope.getUserFunctionInstructionPtr(name);
	}

	@Override
	public Integer getNativeId(final String name) {
		return this.globalScope.getNativeId(name);
	}

	@Override
	public NativeJassFunction getNative(final String functionName) {
		return this.globalScope.getNativeById(this.globalScope.getNativeId(functionName));
	}

	@Override
	public JassType parseType(final String text) {
		return this.globalScope.parseType(text);
	}

	@Override
	public JassType parseArrayType(final String primitiveTypeName) {
		return this.globalScope.parseArrayType(primitiveTypeName);
	}

	@Override
	public void loadTypeDefinition(final String type, final String supertype) {
		this.globalScope.loadTypeDefinition(type, supertype);
	}

	@Override
	public void createGlobalArray(final EnumSet<JassQualifier> qualifiers, final String identifier,
			final JassType type) {
		if (!qualifiers.isEmpty() && !((qualifiers.size() == 1) && qualifiers.contains(JassQualifier.CONSTANT))) {
			throw new IllegalArgumentException("Invalid qualifiers for global array: " + qualifiers.toString());
		}
		this.globalScope.createGlobalArray(identifier, type);
	}

	@Override
	public void createGlobal(final EnumSet<JassQualifier> qualifiers, final String identifier, final JassType type) {
		if (!qualifiers.isEmpty() && !((qualifiers.size() == 1) && qualifiers.contains(JassQualifier.CONSTANT))) {
			throw new IllegalArgumentException("Invalid qualifiers for global array: " + qualifiers.toString());
		}
		this.globalScope.createGlobal(identifier, type);
	}

	@Override
	public void createGlobal(final EnumSet<JassQualifier> qualifiers, final String identifier, final JassType type,
			final JassValue value) {
		if (!qualifiers.isEmpty() && !((qualifiers.size() == 1) && qualifiers.contains(JassQualifier.CONSTANT))) {
			throw new IllegalArgumentException("Invalid qualifiers for global array: " + qualifiers.toString());
		}
		this.globalScope.createGlobal(identifier, type, value);
	}

	@Override
	public JassException createException(final String message, final Exception cause) {
		return new JassException(this.globalScope, message, cause);
	}

	@Override
	public JassValue getPreprocessorConstant(final String identifier) {
		return null;
	}

	@Override
	public Scope createNestedScope(final String namespace, final boolean library) {
		return new ScopedScope(new LibraryScopeTree().descend(namespace, library), this.globalScope);
	}

	@Override
	public <T> T forEachPossibleResolvedIdentifier(final String identifier, final ScopeTreeHandler<T> handler) {
		return handler.identifier(identifier);
	}

}

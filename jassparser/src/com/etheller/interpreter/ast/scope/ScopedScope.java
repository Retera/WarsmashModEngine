package com.etheller.interpreter.ast.scope;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.StructJassType;

public class ScopedScope implements Scope {
	private final GlobalIdGetter globalIdGetter = new GlobalIdGetter();

	private final LibraryScopeTree libraryScopeTree;
	private final GlobalScope globalScope;

	private final Map<String, JassValue> keyToPreprocessorConstant = new HashMap<>();

	public ScopedScope(final LibraryScopeTree libraryScopeTree, final GlobalScope globalScope) {
		this.libraryScopeTree = libraryScopeTree;
		this.globalScope = globalScope;

		if (!libraryScopeTree.isEmpty()) {
			this.keyToPreprocessorConstant.put(GlobalScope.KEYWORD_SCOPE_PREFIX,
					StringJassValue.of(libraryScopeTree.getQualifiedNamePublic("")));
			this.keyToPreprocessorConstant.put(GlobalScope.KEYWORD_SCOPE_PRIVATE,
					StringJassValue.of(libraryScopeTree.getQualifiedNamePrivate("")));
		}
	}

	@Override
	public JassType parseArrayType(final String id) {
		return this.globalScope.parseArrayType(id, this.libraryScopeTree);
	}

	@Override
	public JassType parseType(final String id) {
		return this.globalScope.parseType(id, this.libraryScopeTree);
	}

	@Override
	public int getGlobalId(final String identifier) {
		this.libraryScopeTree.forEachPossibleResolvedIdentifier(identifier, this.globalIdGetter.reset());
		return this.globalIdGetter.id;
	}

	@Override
	public void createGlobalArray(final EnumSet<JassQualifier> qualifiers, final String identifier,
			final JassType type) {
		final String qualifiedIdentifier = this.libraryScopeTree.getQualifiedIdentifier(identifier, qualifiers);
		this.globalScope.createGlobalArray(qualifiedIdentifier, type);
	}

	@Override
	public void createGlobal(final EnumSet<JassQualifier> qualifiers, final String identifier, final JassType type) {
		final String qualifiedIdentifier = this.libraryScopeTree.getQualifiedIdentifier(identifier, qualifiers);
		this.globalScope.createGlobal(qualifiedIdentifier, type);
	}

	@Override
	public GlobalScopeAssignable getAssignableGlobal(final String identifier) {
		return this.libraryScopeTree.forEachPossibleResolvedIdentifier(identifier, (possibleIdentifier) -> {
			return this.globalScope.getAssignableGlobal(possibleIdentifier);
		});
	}

	@Override
	public UserJassFunction getFunctionDefinitionByName(final String functionName) {
		return this.libraryScopeTree.forEachPossibleResolvedIdentifier(functionName, (possibleIdentifier) -> {
			return this.globalScope.getFunctionDefinitionByName(possibleIdentifier);
		});
	}

	@Override
	public GlobalScopeAssignable getAssignableGlobalById(final int globalId) {
		return this.globalScope.getAssignableGlobalById(globalId);
	}

	@Override
	public Integer getUserFunctionInstructionPtr(final String functionName) {
		return this.libraryScopeTree.forEachPossibleResolvedIdentifier(functionName, (possibleIdentifier) -> {
			return this.globalScope.getUserFunctionInstructionPtr(possibleIdentifier);
		});
	}

	@Override
	public Integer getNativeId(final String functionName) {
		return this.globalScope.getNativeId(functionName);
	}

	@Override
	public NativeJassFunction getNative(final String functionName) {
		final Integer nativeId = this.globalScope.getNativeId(functionName);
		if (nativeId == null) {
			throw new IllegalArgumentException("No such native: " + functionName);
		}
		return this.globalScope.getNativeById(nativeId);
	}

	@Override
	public JassException createException(final String message, final Exception cause) {
		return new JassException(this.globalScope, message, cause);
	}

	@Override
	public void createGlobal(final EnumSet<JassQualifier> qualifiers, final String identifier, final JassType type,
			final JassValue value) {
		final String qualifiedIdentifier = this.libraryScopeTree.getQualifiedIdentifier(identifier, qualifiers);
		this.globalScope.createGlobal(qualifiedIdentifier, type, value);
	}

	@Override
	public void defineFunction(final int lineNo, final String sourceFile, String name, final UserJassFunction function,
			final Scope scope) {
		name = this.libraryScopeTree.getQualifiedIdentifier(name, function.getQualifiers());
		this.globalScope.defineFunction(lineNo, sourceFile, name, function, scope);
	}

	@Override
	public int defineMethod(final int lineNo, final String sourceFile, final String name, final UserJassFunction method,
			final StructJassType structJassType, final Scope scope) {
		return this.globalScope.defineMethod(lineNo, sourceFile, name, method, structJassType, scope);
	}

	@Override
	public void defineGlobals(final int lineNo, final String file, final List<JassStatement> globalStatements,
			final Scope scope) {
		this.globalScope.defineGlobals(lineNo, file, globalStatements, scope);
	}

	@Override
	public Scope createNestedScope(final String namespace, final boolean library) {
		return new ScopedScope(this.libraryScopeTree.descend(namespace, library), this.globalScope);
	}

	@Override
	public void loadTypeDefinition(final String id, final String supertype) {
		this.globalScope.loadTypeDefinition(id, supertype);
	}

	@Override
	public void defineStruct(final EnumSet<JassQualifier> qualifiers, String structName,
			final JassTypeToken structSuperTypeToken, final List<JassStructMemberTypeDefinition> memberTypeDefinitions,
			final List<JassImplementModuleDefinition> implementModuleDefinitions,
			final List<JassMethodDefinitionBlock> methodDefinitions, final Scope scope) {
		structName = this.libraryScopeTree.getQualifiedIdentifier(structName, qualifiers);
		this.globalScope.defineStruct(qualifiers, structName, structSuperTypeToken, memberTypeDefinitions,
				implementModuleDefinitions, methodDefinitions, scope);
	}

	@Override
	public void defineModule(final JassModuleDefinitionBlock jassModuleDefinitionBlock) {
		String moduleName = jassModuleDefinitionBlock.getName();
		moduleName = this.libraryScopeTree.getQualifiedIdentifier(moduleName,
				jassModuleDefinitionBlock.getQualifiers());
		this.globalScope.defineModule(moduleName, jassModuleDefinitionBlock);

	}

	@Override
	public void defineFunction(final int lineNo, final String sourceFile, final String name,
			final NativeJassFunction nativeJassFunction) {
		this.globalScope.defineFunction(lineNo, sourceFile, name, nativeJassFunction);
	}

	@Override
	public JassValue getPreprocessorConstant(final String identifier) {
		return this.keyToPreprocessorConstant.get(identifier);
	}

	@Override
	public <T> T forEachPossibleResolvedIdentifier(final String identifier, final ScopeTreeHandler<T> handler) {
		return this.libraryScopeTree.forEachPossibleResolvedIdentifier(identifier, handler);
	}

	private final class GlobalIdGetter implements ScopeTreeHandler<Boolean> {
		private int id;

		public GlobalIdGetter reset() {
			this.id = -1;
			return this;
		}

		@Override
		public Boolean identifier(final String possibleIdentifier) {
			this.id = ScopedScope.this.globalScope.getGlobalId(possibleIdentifier);
			if (this.id != -1) {
				return Boolean.TRUE;
			}
			return null;
		}
	}
}

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

public interface Scope {

	int getGlobalId(String identifier);

	void createGlobalArray(EnumSet<JassQualifier> qualifiers, String identifier, JassType type);

	void createGlobal(EnumSet<JassQualifier> qualifiers, String identifier, JassType type);

	void createGlobal(EnumSet<JassQualifier> qualifiers, String identifier, JassType type, JassValue value);

	GlobalScopeAssignable getAssignableGlobal(String identifier);

	UserJassFunction getFunctionDefinitionByName(String functionName);

	GlobalScopeAssignable getAssignableGlobalById(int globalId);

	Integer getUserFunctionInstructionPtr(String functionName);

	Integer getNativeId(String functionName);

	NativeJassFunction getNative(String functionName);

	JassException createException(String message, Exception cause);

	void defineFunction(int lineNo, String sourceFile, String name, UserJassFunction function, Scope scope);

	int defineMethod(int lineNo, String sourceFile, String name, UserJassFunction method, StructJassType structJassType,
			Scope scope);

	void defineGlobals(int lineNo, String file, List<JassStatement> globalStatements, Scope scope);

	Scope createNestedScope(String namespace, boolean library);

	void loadTypeDefinition(String id, String supertype);

	void defineStruct(EnumSet<JassQualifier> qualifiers, String structName, JassTypeToken structSuperTypeToken,
			List<JassStructMemberTypeDefinition> memberTypeDefinitions,
			List<JassImplementModuleDefinition> implementModuleDefinitions,
			List<JassMethodDefinitionBlock> methodDefinitions, Scope scope);

	void defineModule(JassModuleDefinitionBlock jassModuleDefinitionBlock);

	JassType parseArrayType(String id);

	JassType parseType(String id);

	void defineFunction(int lineNo, String sourceFile, String name, NativeJassFunction nativeJassFunction);

	JassValue getPreprocessorConstant(String identifier);

	<T> T forEachPossibleResolvedIdentifier(final String identifier, final ScopeTreeHandler<T> handler);

}

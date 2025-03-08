package com.etheller.interpreter.ast.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.definition.JassCodeDefinitionBlock;
import com.etheller.interpreter.ast.definition.JassMethodDefinitionBlock;
import com.etheller.interpreter.ast.definition.JassParameterDefinition;
import com.etheller.interpreter.ast.expression.AllocateAsNewTypeExpression;
import com.etheller.interpreter.ast.expression.ExtendHandleExpression;
import com.etheller.interpreter.ast.expression.FunctionCallJassExpression;
import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.expression.JassNewExpression;
import com.etheller.interpreter.ast.expression.MethodCallJassExpression;
import com.etheller.interpreter.ast.expression.ReferenceJassExpression;
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.function.NativeJassFunction;
import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.statement.JassCallExpressionStatement;
import com.etheller.interpreter.ast.statement.JassLocalDefinitionStatement;
import com.etheller.interpreter.ast.statement.JassReturnStatement;
import com.etheller.interpreter.ast.statement.JassSetMemberStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.struct.JassStructMemberType;
import com.etheller.interpreter.ast.type.LiteralJassTypeToken;
import com.etheller.interpreter.ast.type.NothingJassTypeToken;
import com.etheller.interpreter.ast.value.visitor.HandleJassTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.StructJassTypeVisitor;

public class StructJassType implements JassType, StructJassTypeInterface {
	private static final String ALLOCATE = "allocate";
	private static final String CREATE = "create";
	private static final String DESTROY = "destroy";
	private static final String ON_DESTROY = "onDestroy";
	private static final String DEALLOCATE = "deallocate";
	private final JassType superType;
	private final String name;
	private final List<JassStructMemberType> memberTypes = new ArrayList<>();
	private final List<Integer> methodTable = new ArrayList<>();
	private final Map<String, Integer> methodNameToTableIndex = new HashMap<>();
	// for use during setup
	private final List<JassMethodDefinitionBlock> methods = new ArrayList<>();
	private final List<JassStatement> defaultMemberInitializerStatements = new ArrayList<>();
	private final Map<String, JassMethodDefinitionBlock> nameToMethod = new HashMap<>();
	private final Map<String, JassStructMemberType> nameToMember = new HashMap<>();

	public StructJassType(final JassType superType, final String name) {
		this.superType = superType;
		this.name = name;
	}

	private void addDefaultAllocateMethod(final Scope globalScope) {
		final JassNewExpression creationExpression = new JassNewExpression(this);
		final List<JassStatement> newStatements = generateDefaultAllocateStatements(creationExpression);
		add(new JassMethodDefinitionBlock(0, "<init>", EnumSet.of(JassQualifier.STATIC, JassQualifier.PRIVATE),
				ALLOCATE, newStatements, Collections.emptyList(), new LiteralJassTypeToken(this)));
	}

	private void addDefaultDeallocateMethod(final Scope globalScope) {
		final List<JassStatement> newStatements = new ArrayList<>();
		appendOnDestroyCallIfAvailable(newStatements);
		add(new JassMethodDefinitionBlock(0, "<init>", EnumSet.of(JassQualifier.PRIVATE), DEALLOCATE, newStatements,
				Collections.emptyList(), NothingJassTypeToken.INSTANCE));
	}

	private void add(final JassMethodDefinitionBlock methodBlock) {
		this.methods.add(methodBlock);
		this.nameToMethod.put(methodBlock.getName(), methodBlock);
		Integer tableIndex = this.methodNameToTableIndex.get(methodBlock.getName());
		if (tableIndex == null) {
			// this method not is an override, needs its own index
			tableIndex = this.methodTable.size();
			this.methodNameToTableIndex.put(methodBlock.getName(), tableIndex);
			this.methodTable.add(-1); // to be set later
		}
	}

	public void buildMethodTable(final Scope globalScope, final List<JassMethodDefinitionBlock> methodDefinitions,
			final List<JassStructMemberType> memberTypes) {
		final StructJassType superStructType = this.superType.visit(StructJassTypeVisitor.getInstance());

		if (superStructType != null) {
			this.memberTypes.addAll(superStructType.getMemberTypes());
		}

		for (final JassStructMemberType memberType : memberTypes) {
			add(memberType);
		}

		if (superStructType != null) {
			this.methodNameToTableIndex.putAll(superStructType.methodNameToTableIndex);
			this.methodTable.addAll(superStructType.methodTable);
			final JassCodeDefinitionBlock superCreateMethod = superStructType.tryGetMethodByName(CREATE);
			if (superCreateMethod != null) {
				final List<JassExpression> passThroughArguments = new ArrayList<>();
				for (final JassParameterDefinition parameter : superCreateMethod.getParameterDefinitions()) {
					passThroughArguments.add(new ReferenceJassExpression(parameter.getIdentifier()));
				}
				final AllocateAsNewTypeExpression creationExpression = new AllocateAsNewTypeExpression(
						new MethodCallJassExpression(new ReferenceJassExpression(superStructType.getName()), CREATE,
								passThroughArguments),
						this);
				final List<JassStatement> newStatements = generateDefaultAllocateStatements(creationExpression);
				add(new JassMethodDefinitionBlock(0, "<init>", EnumSet.of(JassQualifier.STATIC, JassQualifier.PRIVATE),
						ALLOCATE, newStatements, superCreateMethod.getParameterDefinitions(),
						new LiteralJassTypeToken(this)));
			}
			else {
				addDefaultAllocateMethod(globalScope);
			}
		}
		else {
			final HandleJassType superHandleType = this.superType.visit(HandleJassTypeVisitor.getInstance());
			if (superHandleType != null) {
				final HandleJassTypeConstructor constructorNative = superHandleType.getConstructorNative();
				if (constructorNative != null) {
					final List<JassExpression> passThroughArguments = new ArrayList<>();

					final NativeJassFunction ctorNative = globalScope.getNative(constructorNative.getName());
					final List<JassParameter> parameters = ctorNative.getParameters();
					for (final JassParameter parameter : parameters) {
						passThroughArguments.add(new ReferenceJassExpression(parameter.getIdentifier()));
					}
					final ExtendHandleExpression creationExpression = new ExtendHandleExpression(
							new FunctionCallJassExpression(constructorNative.getName(), passThroughArguments), this);
					final List<JassStatement> newStatements = generateDefaultAllocateStatements(creationExpression);
					add(new JassMethodDefinitionBlock(0, "<init>",
							EnumSet.of(JassQualifier.STATIC, JassQualifier.PRIVATE), ALLOCATE, newStatements,
							JassParameterDefinition.unresolve(parameters), new LiteralJassTypeToken(this)));
				}
				else {
					addDefaultAllocateMethod(globalScope);
				}
			}
			else {
				addDefaultAllocateMethod(globalScope);
			}
		}

		for (final JassMethodDefinitionBlock methodDefinition : methodDefinitions) {
			add(methodDefinition);
		}

		if (superStructType != null) {
			final JassMethodDefinitionBlock superDestroyMethod = superStructType.tryGetMethodByName(DESTROY);
			if (superDestroyMethod != null) {
				final List<JassStatement> newStatements = new ArrayList<>();
				final List<JassExpression> passThroughArguments = new ArrayList<>();
				final EnumSet<JassQualifier> qualifiers = EnumSet.of(JassQualifier.PRIVATE);
				if (superDestroyMethod.getQualifiers().contains(JassQualifier.STATIC)) {
					qualifiers.add(JassQualifier.STATIC);
				}
				else {
					passThroughArguments.add(new ReferenceJassExpression(GlobalScope.KEYWORD_THIS));
				}
				for (final JassParameterDefinition parameter : superDestroyMethod.getParameterDefinitions()) {
					passThroughArguments.add(new ReferenceJassExpression(parameter.getIdentifier()));
				}
				appendOnDestroyCallIfAvailable(newStatements);
				newStatements.add(new JassCallExpressionStatement(new MethodCallJassExpression(
						new ReferenceJassExpression(superStructType.getName()), DESTROY, passThroughArguments)));

				add(new JassMethodDefinitionBlock(0, "<init>", qualifiers, DEALLOCATE, newStatements,
						superDestroyMethod.getParameterDefinitions(), superDestroyMethod.getReturnType()));
			}
			else {
				addDefaultDeallocateMethod(globalScope);
			}
		}
		else {
			final HandleJassType superHandleType = this.superType.visit(HandleJassTypeVisitor.getInstance());
			if (superHandleType != null) {
				final HandleJassTypeConstructor constructorNative = superHandleType.getDestructorNative();
				if (constructorNative != null) {
					final List<JassExpression> passThroughArguments = new ArrayList<>();

					final NativeJassFunction ctorNative = globalScope.getNative(constructorNative.getName());
					final List<JassParameter> parameters = ctorNative.getParameters();
					passThroughArguments.add(new ReferenceJassExpression(GlobalScope.KEYWORD_THIS));
					for (int i = 1, l = parameters.size(); i < l; i++) {
						passThroughArguments.add(new ReferenceJassExpression(parameters.get(i).getIdentifier()));
					}
					final List<JassStatement> newStatements = new ArrayList<>();
					appendOnDestroyCallIfAvailable(newStatements);
					newStatements.add(new JassCallExpressionStatement(
							new FunctionCallJassExpression(constructorNative.getName(), passThroughArguments)));

					add(new JassMethodDefinitionBlock(0, "<init>", EnumSet.of(JassQualifier.PRIVATE), DEALLOCATE,
							newStatements, JassParameterDefinition.unresolve(parameters),
							new LiteralJassTypeToken(this)));
				}
				else {
					addDefaultDeallocateMethod(globalScope);
				}
			}
			else {
				addDefaultDeallocateMethod(globalScope);
			}
		}

		final boolean userDefinedCreate = tryGetDeclaredMethodByName(CREATE) != null;
		final boolean userDefinedDestroy = tryGetDeclaredMethodByName(DESTROY) != null;
		if (!userDefinedCreate) {
			final Integer createIndex = this.methodNameToTableIndex.get(CREATE);
			if (createIndex == null) {
				final JassMethodDefinitionBlock existingAllocateMethod = tryGetDeclaredMethodByName(ALLOCATE);
				add(new JassMethodDefinitionBlock(0, "<init>", EnumSet.of(JassQualifier.STATIC), CREATE,
						Collections.<JassStatement>emptyList(), existingAllocateMethod.getParameterDefinitions(),
						new LiteralJassTypeToken(this)));
			}
		}
		if (!userDefinedDestroy) {
			final Integer createIndex = this.methodNameToTableIndex.get(DESTROY);
			if (createIndex == null) {
				add(new JassMethodDefinitionBlock(0, "<init>", EnumSet.noneOf(JassQualifier.class), DESTROY,
						Collections.<JassStatement>emptyList(), Collections.emptyList(),
						NothingJassTypeToken.INSTANCE));
			}
		}
		for (final JassMethodDefinitionBlock methodBlock : this.methods) {
			final String methodName = methodBlock.getName();
			final Integer tableIndex = this.methodNameToTableIndex.get(methodName);
			if (CREATE.equals(methodName) && !userDefinedCreate) {
				continue;
			}
			else if (DESTROY.equals(methodName) && !userDefinedDestroy) {
				continue;
			}
			else {
				final int methodInstructionPtr = globalScope.defineMethod(methodBlock.getLineNo(),
						methodBlock.getSourceFile(), methodName, methodBlock.createCode(globalScope, this), this,
						globalScope);
				this.methodTable.set(tableIndex, methodInstructionPtr);
				if (!userDefinedCreate && ALLOCATE.equals(methodName)) {
					this.methodTable.set(this.methodNameToTableIndex.get(CREATE), methodInstructionPtr);
				}
				else if (!userDefinedDestroy && DEALLOCATE.equals(methodName)) {
					this.methodTable.set(this.methodNameToTableIndex.get(DESTROY), methodInstructionPtr);
				}
			}
		}
		for (int i = 0; i < this.methodTable.size(); i++) {
			if (this.methodTable.get(i) == -1) {
				throw new IllegalStateException("x: " + i);
			}
		}
	}

	private List<JassStatement> generateDefaultAllocateStatements(final JassExpression creationExpression) {
		final List<JassStatement> newStatements = new ArrayList<>();
		if (this.defaultMemberInitializerStatements.isEmpty()) {
			newStatements.add(new JassReturnStatement(creationExpression));
		}
		else {
			newStatements.add(new JassLocalDefinitionStatement(GlobalScope.KEYWORD_THIS, new LiteralJassTypeToken(this),
					creationExpression));
			newStatements.addAll(this.defaultMemberInitializerStatements);
			newStatements.add(new JassReturnStatement(new ReferenceJassExpression(GlobalScope.KEYWORD_THIS)));
		}
		return newStatements;
	}

	private void appendOnDestroyCallIfAvailable(final List<JassStatement> newStatements) {
		final JassMethodDefinitionBlock onDestroy = tryGetDeclaredMethodByName(ON_DESTROY);
		if (onDestroy != null) {
			newStatements.add(new JassCallExpressionStatement(new MethodCallJassExpression(
					new ReferenceJassExpression(GlobalScope.KEYWORD_THIS), ON_DESTROY, Collections.emptyList())));
		}
	}

	public List<Integer> getMethodTable() {
		return this.methodTable;
	}

	public Integer getMethodTableIndex(final String methodName) {
		return this.methodNameToTableIndex.get(methodName);
	}

	private void add(final JassStructMemberType memberType) {
		this.memberTypes.add(memberType);
		this.nameToMember.put(memberType.getId(), memberType);
		final JassExpression defaultValueExpression = memberType.getDefaultValueExpression();
		if (defaultValueExpression != null) {
			this.defaultMemberInitializerStatements.add(new JassSetMemberStatement(
					new ReferenceJassExpression(GlobalScope.KEYWORD_THIS), memberType.getId(), defaultValueExpression));
		}
	}

	public List<JassStructMemberType> getMemberTypes() {
		return this.memberTypes;
	}

	public List<JassMethodDefinitionBlock> getMethods() {
		return this.methods;
	}

	@Override
	public <TYPE> TYPE visit(final JassTypeVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public JassType getSuperType() {
		return this.superType;
	}

	@Override
	public boolean isAssignableFrom(final JassType value) {
		return value.visit(StructAssignabilityTypeVisitor.INSTANCE.reset(this));
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public StructJassValue getNullValue() {
		return null;
	}

	private JassStructMemberType tryGetDeclaredMemberByName(final String name) {
		return this.nameToMember.get(name);
	}

	@Override
	public JassStructMemberType getMemberByName(final String name) {
		final JassStructMemberType member = tryGetMemberByName(name);
		if (member == null) {
			throw new IllegalArgumentException("Type '" + this.name + "' has no member '" + name + "'");
		}
		return member;
	}

	@Override
	public JassStructMemberType tryGetMemberByName(final String name) {
		final JassStructMemberType declaredMember = tryGetDeclaredMemberByName(name);
		if (declaredMember != null) {
			return declaredMember;
		}
		if (this.superType != null) {
			final StructJassType structSuperType = this.superType.visit(StructJassTypeVisitor.getInstance());
			if (structSuperType != null) {
				return structSuperType.tryGetMemberByName(name);
			}
		}
		return null;
	}

	@Override
	public int getMemberIndexInefficientlyByName(final String name) {
		for (int index = 0; index < this.memberTypes.size(); index++) {
			if (this.memberTypes.get(index).getId().equals(name)) {
				return index;
			}
		}
		throw new IllegalArgumentException("Type '" + this.name + "' has no member '" + name + "'");
	}

	@Override
	public int tryGetMemberIndexInefficientlyByName(final String name) {
		for (int index = 0; index < this.memberTypes.size(); index++) {
			if (this.memberTypes.get(index).getId().equals(name)) {
				return index;
			}
		}
		return -1;
	}

	// NOTE: the below methods were changed to hash maps for performance, but in
	// general do not use them outside of "pseudocompile" load time... These are not
	// for dynamic calling other than maybe executeFunc

	public JassMethodDefinitionBlock getMethodByName(final String name) {
		final JassMethodDefinitionBlock method = tryGetMethodByName(name);
		if (method == null) {
			throw new IllegalArgumentException("Type '" + this.name + "' has no method '" + name + "'");
		}
		return method;
	}

	public JassMethodDefinitionBlock tryGetMethodByName(final String name) {
		final JassMethodDefinitionBlock declaredMethod = tryGetDeclaredMethodByName(name);
		if (declaredMethod != null) {
			return declaredMethod;
		}
		if (this.superType != null) {
			final StructJassType structSuperType = this.superType.visit(StructJassTypeVisitor.getInstance());
			if (structSuperType != null) {
				return structSuperType.tryGetMethodByName(name);
			}
		}
		return null;
	}

	private JassMethodDefinitionBlock tryGetDeclaredMethodByName(final String name) {
		return this.nameToMethod.get(name);
	}

	public boolean canAccessPrivateMethodsOf(final StructJassTypeInterface structJassType) {
		return this == structJassType;
	}
}

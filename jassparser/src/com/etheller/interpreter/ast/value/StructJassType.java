package com.etheller.interpreter.ast.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.etheller.interpreter.ast.function.AbstractJassFunction;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.statement.JassReturnStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.struct.JassStructMemberType;
import com.etheller.interpreter.ast.type.LiteralJassTypeToken;
import com.etheller.interpreter.ast.value.visitor.HandleJassTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.StructJassTypeVisitor;

public class StructJassType implements JassType {
	private static final String ALLOCATE = "allocate";
	private static final String CREATE = "create";
	private final JassType superType;
	private final String name;
	private final List<JassStructMemberType> memberTypes = new ArrayList<>();
	private final List<JassMethodDefinitionBlock> methods = new ArrayList<>();
	private final List<Integer> methodTable = new ArrayList<>();
	private final Map<String, Integer> methodNameToTableIndex = new HashMap<>();

	public StructJassType(final JassType superType, final String name) {
		this.superType = superType;
		this.name = name;
	}

	private void addDefaultAllocateMethod(final GlobalScope globalScope) {
		add(new JassMethodDefinitionBlock(0, "<init>", ALLOCATE,
				Arrays.<JassStatement>asList(new JassReturnStatement(new JassNewExpression(this))),
				Collections.emptyList(), new LiteralJassTypeToken(this), true));
	}

	private void add(final JassMethodDefinitionBlock methodBlock) {
		this.methods.add(methodBlock);
		Integer tableIndex = this.methodNameToTableIndex.get(methodBlock.getName());
		if (tableIndex == null) {
			// this method not is an override, needs its own index
			tableIndex = this.methodTable.size();
			this.methodNameToTableIndex.put(methodBlock.getName(), tableIndex);
			this.methodTable.add(-1); // to be set later
		}
	}

	public void buildMethodTable(final GlobalScope globalScope, final List<JassMethodDefinitionBlock> methodDefinitions,
			final String mangledNameScope) {
		final StructJassType superStructType = this.superType.visit(StructJassTypeVisitor.getInstance());
		if (superStructType != null) {
			this.memberTypes.addAll(superStructType.getMemberTypes());

			this.methodNameToTableIndex.putAll(superStructType.methodNameToTableIndex);
			this.methodTable.addAll(superStructType.methodTable);
			final JassCodeDefinitionBlock superCreateMethod = superStructType.tryGetMethodInefficientlyByName(CREATE);
			if (superCreateMethod != null) {
				final List<JassStatement> newStatements = new ArrayList<>();
				final List<JassExpression> passThroughArguments = new ArrayList<>();
				for (final JassParameterDefinition parameter : superCreateMethod.getParameterDefinitions()) {
					passThroughArguments.add(new ReferenceJassExpression(parameter.getIdentifier()));
				}
				newStatements.add(new JassReturnStatement(new AllocateAsNewTypeExpression(new MethodCallJassExpression(
						new ReferenceJassExpression(superStructType.getName()), CREATE, passThroughArguments), this)));
				add(new JassMethodDefinitionBlock(0, "<init>", ALLOCATE, newStatements,
						superCreateMethod.getParameterDefinitions(), new LiteralJassTypeToken(this), true));
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
					final JassFunction ctorNative = constructorNative.getNativeCode();
					if (ctorNative instanceof AbstractJassFunction) {
						final List<JassParameter> parameters = ((AbstractJassFunction) ctorNative).getParameters();
						for (final JassParameter parameter : parameters) {
							passThroughArguments.add(new ReferenceJassExpression(parameter.getIdentifier()));
						}
					}
					final List<JassStatement> newStatements = new ArrayList<>();
					newStatements.add(new JassReturnStatement(new ExtendHandleExpression(
							new FunctionCallJassExpression(constructorNative.getName(), passThroughArguments), this)));
					add(new JassMethodDefinitionBlock(0, "<init>", ALLOCATE, newStatements,
							Collections.emptyList() /* TODO args */, new LiteralJassTypeToken(this), true));
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

		for (final JassMethodDefinitionBlock methodBlock : this.methods) {
			final Integer tableIndex = this.methodNameToTableIndex.get(methodBlock.getName());
			final int methodInstructionPtr = globalScope.defineMethod(methodBlock.getLineNo(),
					methodBlock.getSourceFile(), methodBlock.getName(), methodBlock.createCode(globalScope, this), this,
					mangledNameScope);
			this.methodTable.set(tableIndex, methodInstructionPtr);
		}
		for (int i = 0; i < this.methodTable.size(); i++) {
			if (this.methodTable.get(i) == -1) {
				throw new IllegalStateException("x: " + i);
			}
		}
	}

	public List<Integer> getMethodTable() {
		return this.methodTable;
	}

	public Integer getMethodTableIndex(final String methodName) {
		return this.methodNameToTableIndex.get(methodName);
	}

	public void add(final JassStructMemberType memberType) {
		this.memberTypes.add(memberType);
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

	public JassStructMemberType getMemberInefficientlyByName(final String name) {
		for (final JassStructMemberType type : this.memberTypes) {
			if (type.getId().equals(name)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Type '" + name + "' has no member '" + name + "'");
	}

	public int getMemberIndexInefficientlyByName(final String name) {
		for (int index = 0; index < this.memberTypes.size(); index++) {
			if (this.memberTypes.get(index).getId().equals(name)) {
				return index;
			}
		}
		throw new IllegalArgumentException("Type '" + name + "' has no member '" + name + "'");
	}

	public int tryGetMemberIndexInefficientlyByName(final String name) {
		for (int index = 0; index < this.memberTypes.size(); index++) {
			if (this.memberTypes.get(index).getId().equals(name)) {
				return index;
			}
		}
		return -1;
	}

	public JassCodeDefinitionBlock getMethodInefficientlyByName(final String name) {
		final JassCodeDefinitionBlock method = tryGetMethodInefficientlyByName(name);
		if (method == null) {
			throw new IllegalArgumentException("Type '" + name + "' has no method '" + name + "'");
		}
		return method;
	}

	public JassCodeDefinitionBlock tryGetMethodInefficientlyByName(final String name) {
		for (final JassCodeDefinitionBlock method : this.methods) {
			if (method.getName().equals(name)) {
				return method;
			}
		}
		if (this.superType != null) {
			final StructJassType structSuperType = this.superType.visit(StructJassTypeVisitor.getInstance());
			if (structSuperType != null) {
				return structSuperType.tryGetMethodInefficientlyByName(name);
			}
		}
		return null;
	}
}

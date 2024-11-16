package com.etheller.interpreter.ast.execution.instruction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.debug.DebuggingJassStatement;
import com.etheller.interpreter.ast.definition.JassCodeDefinitionBlock;
import com.etheller.interpreter.ast.expression.AllocateAsNewTypeExpression;
import com.etheller.interpreter.ast.expression.ArithmeticJassExpression;
import com.etheller.interpreter.ast.expression.ArithmeticSign;
import com.etheller.interpreter.ast.expression.ArrayRefJassExpression;
import com.etheller.interpreter.ast.expression.ExtendHandleExpression;
import com.etheller.interpreter.ast.expression.FunctionCallJassExpression;
import com.etheller.interpreter.ast.expression.FunctionReferenceJassExpression;
import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.expression.JassExpressionVisitor;
import com.etheller.interpreter.ast.expression.JassNewExpression;
import com.etheller.interpreter.ast.expression.LiteralJassExpression;
import com.etheller.interpreter.ast.expression.MemberJassExpression;
import com.etheller.interpreter.ast.expression.MethodCallJassExpression;
import com.etheller.interpreter.ast.expression.MethodReferenceJassExpression;
import com.etheller.interpreter.ast.expression.NegateJassExpression;
import com.etheller.interpreter.ast.expression.NotJassExpression;
import com.etheller.interpreter.ast.expression.ParentlessMethodCallJassExpression;
import com.etheller.interpreter.ast.expression.ReferenceJassExpression;
import com.etheller.interpreter.ast.expression.TypeCastJassExpression;
import com.etheller.interpreter.ast.expression.visitor.JassTypeExpressionVisitor;
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.GlobalScopeAssignable;
import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.statement.JassArrayedAssignmentStatement;
import com.etheller.interpreter.ast.statement.JassCallExpressionStatement;
import com.etheller.interpreter.ast.statement.JassCallStatement;
import com.etheller.interpreter.ast.statement.JassDoNothingStatement;
import com.etheller.interpreter.ast.statement.JassExitWhenStatement;
import com.etheller.interpreter.ast.statement.JassGlobalDefinitionStatement;
import com.etheller.interpreter.ast.statement.JassGlobalStatement;
import com.etheller.interpreter.ast.statement.JassIfElseIfStatement;
import com.etheller.interpreter.ast.statement.JassIfElseStatement;
import com.etheller.interpreter.ast.statement.JassIfStatement;
import com.etheller.interpreter.ast.statement.JassLocalDefinitionStatement;
import com.etheller.interpreter.ast.statement.JassLocalStatement;
import com.etheller.interpreter.ast.statement.JassLoopStatement;
import com.etheller.interpreter.ast.statement.JassReturnNothingStatement;
import com.etheller.interpreter.ast.statement.JassReturnStatement;
import com.etheller.interpreter.ast.statement.JassSetMemberStatement;
import com.etheller.interpreter.ast.statement.JassSetStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.statement.JassStatementVisitor;
import com.etheller.interpreter.ast.statement.JassThrowStatement;
import com.etheller.interpreter.ast.struct.JassStructMemberType;
import com.etheller.interpreter.ast.util.JassSettings;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassTypeInterface;
import com.etheller.interpreter.ast.value.visitor.ArrayPrimitiveTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.ArrayTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.StaticStructTypeJassTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.StaticStructTypeJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.StructJassTypeVisitor;
import com.etheller.interpreter.ast.value.visitor.cast.TypeCastConverterGettingJassTypeVisitor;

public class InstructionAppendingJassStatementVisitor
		implements JassStatementVisitor<Void>, JassExpressionVisitor<Void> {
	private static final boolean CHECK_TYPES = JassSettings.CHECK_TYPES;
	private static final PushLiteralInstruction PUSH_NOTHING = new PushLiteralInstruction(
			JassReturnNothingStatement.RETURN_NOTHING_NOTICE);
	private final List<JassInstruction> instructions;
	private final Scope scope;
	private Map<String, Integer> nameToLocalId = new HashMap<>();
	private Map<String, JassType> nameToLocalType = new HashMap<>();
	private int nextLocalId;
	private final ArrayDeque<LoopImpl> loopStartInstructionPtrs = new ArrayDeque<>();
	private StructJassType enclosingStructType;

	public InstructionAppendingJassStatementVisitor(final List<JassInstruction> instructions, final Scope scope,
			final List<JassParameter> parameters) {
		this.instructions = instructions;
		this.scope = scope;
		this.nextLocalId = 0;
		for (final JassParameter parameter : parameters) {
			this.nameToLocalId.put(parameter.getIdentifier(), this.nextLocalId++);
			this.nameToLocalType.put(parameter.getIdentifier(), parameter.getType());
		}

		final GlobalScopeAssignable thistypeGlobal = scope.getAssignableGlobal(GlobalScope.KEYWORD_THISTYPE);
		if (thistypeGlobal != null) {
			final JassValue thistypeValue = thistypeGlobal.getValue();
			if (thistypeValue != null) {
				final StaticStructTypeJassValue staticStructType = thistypeValue
						.visit(StaticStructTypeJassValueVisitor.getInstance());
				if (staticStructType != null) {
					final StructJassType staticType = staticStructType.getStaticType();
					this.enclosingStructType = staticType;
				}
			}
		}
	}

	public int getLocalId(final String name) {
		final Integer localId = this.nameToLocalId.get(name);
		if (localId == null) {
			return -1;
		}
		return localId;
	}

	@Override
	public Void visit(final JassArrayedAssignmentStatement statement) {
		insertExpressionInstructions(statement.getIndexExpression());
		insertExpressionInstructions(statement.getExpression());
		final String identifier = statement.getIdentifier();
		final int localId = getLocalId(identifier);
		if (localId != -1) {
			this.instructions.add(new LocalArrayAssignmentInstruction(localId));
		}
		else {
			final int globalId = this.scope.getGlobalId(identifier);
			if (globalId != -1) {
				this.instructions.add(new GlobalArrayAssignmentInstruction(globalId));
			}
		}
		return null;
	}

	@Override
	public Void visit(final JassCallStatement statement) {
		final String functionName = statement.getFunctionName();
		final List<JassExpression> arguments = statement.getArguments();
		insertFunctionCallInstructions(functionName, arguments);
		this.instructions.add(PopInstruction.INSTANCE);
		return null;
	}

	@Override
	public Void visit(final JassCallExpressionStatement statement) {
		insertExpressionInstructions(statement.getExpression());
		this.instructions.add(PopInstruction.INSTANCE);
		return null;
	}

	@Override
	public Void visit(final JassDoNothingStatement statement) {
		this.instructions.add(DoNothingInstruction.INSTANCE);
		return null;
	}

	@Override
	public Void visit(final JassExitWhenStatement statement) {
		insertExpressionInstructions(statement.getExpression());
		final int conditionBranchInstructionPtr = this.instructions.size();
		this.loopStartInstructionPtrs.peek().exitWhenInstPtrs.add(conditionBranchInstructionPtr);
		this.instructions.add(null);
		return null;
	}

	@Override
	public Void visit(final JassIfElseIfStatement statement) {
		insertExpressionInstructions(statement.getCondition());
		final int branchInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		final Locals locals = saveLocals();
		for (final JassStatement thenStatement : statement.getThenStatements()) {
			thenStatement.accept(this);
		}
		loadLocals(locals);
		final int branchEndInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		final int elseStatementsStart = this.instructions.size();
		this.instructions.set(branchInstructionPtr, new InvertedConditionalBranchInstruction(elseStatementsStart));
		statement.getElseifTail().accept(this);
		this.instructions.set(branchEndInstructionPtr, new BranchInstruction(this.instructions.size()));
		return null;
	}

	@Override
	public Void visit(final JassIfElseStatement statement) {
		insertExpressionInstructions(statement.getCondition());
		final int branchInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		Locals locals = saveLocals();
		for (final JassStatement thenStatement : statement.getThenStatements()) {
			thenStatement.accept(this);
		}
		loadLocals(locals);
		final int branchEndInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		final int elseStatementsStart = this.instructions.size();
		this.instructions.set(branchInstructionPtr, new InvertedConditionalBranchInstruction(elseStatementsStart));
		locals = saveLocals();
		for (final JassStatement thenStatement : statement.getElseStatements()) {
			thenStatement.accept(this);
		}
		loadLocals(locals);
		this.instructions.set(branchEndInstructionPtr, new BranchInstruction(this.instructions.size()));
		return null;
	}

	@Override
	public Void visit(final JassIfStatement statement) {
		insertExpressionInstructions(statement.getCondition());
		final int branchInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		final Locals locals = saveLocals();
		for (final JassStatement thenStatement : statement.getThenStatements()) {
			thenStatement.accept(this);
		}
		loadLocals(locals);
		this.instructions.set(branchInstructionPtr, new InvertedConditionalBranchInstruction(this.instructions.size()));
		return null;
	}

	@Override
	public Void visit(final JassLocalDefinitionStatement statement) {
		final String identifier = statement.getIdentifier();
		this.nameToLocalId.put(identifier, this.nextLocalId++);
		this.nameToLocalType.put(identifier, statement.getType().resolve(this.scope));
		insertExpressionInstructions(statement.getExpression());
		if (CHECK_TYPES) {
			this.instructions.add(new TypeCheckInstruction(statement.getType().resolve(this.scope)));
		}
		return null;
	}

	@Override
	public Void visit(final JassLocalStatement statement) {
		final String identifier = statement.getIdentifier();
		final JassType type = statement.getType().resolve(this.scope);
		this.nameToLocalId.put(identifier, this.nextLocalId++);
		this.nameToLocalType.put(identifier, type);
		final ArrayJassType arrayType = type.visit(ArrayTypeVisitor.getInstance());
		if (arrayType != null) {
			this.instructions.add(new DeclareLocalArrayInstruction(arrayType));
		}
		else {
			this.instructions.add(new PushLiteralInstruction(type.getNullValue()));
		}
		return null;
	}

	@Override
	public Void visit(final JassGlobalStatement statement) {
		final String identifier = statement.getIdentifier();
		final JassType type = statement.getType().resolve(this.scope);
		final JassType arrayPrimType = type.visit(ArrayPrimitiveTypeVisitor.getInstance());
		if (arrayPrimType != null) {
			this.scope.createGlobalArray(statement.getQualifiers(), identifier, type);
		}
		else {
			this.scope.createGlobal(statement.getQualifiers(), identifier, type);
		}
		return null;
	}

	@Override
	public Void visit(final JassGlobalDefinitionStatement statement) {
		final String identifier = statement.getIdentifier();
		final JassType type = statement.getType().resolve(this.scope);
		final JassType arrayPrimType = type.visit(ArrayPrimitiveTypeVisitor.getInstance());
		if (arrayPrimType != null) {
			this.scope.createGlobalArray(statement.getQualifiers(), identifier, type);
		}
		else {
			this.scope.createGlobal(statement.getQualifiers(), identifier, type);
		}
		insertExpressionInstructions(statement.getExpression());
		final int globalId = this.scope.getGlobalId(identifier);
		if (globalId != -1) {
			this.instructions.add(new GlobalAssignmentInstruction(globalId));
		}
		return null;
	}

	@Override
	public Void visit(final JassLoopStatement statement) {
		final int loopStart = this.instructions.size();
		this.loopStartInstructionPtrs.push(new LoopImpl());
		for (final JassStatement loopBodySubStatement : statement.getStatements()) {
			loopBodySubStatement.accept(this);
		}
		this.instructions.add(new BranchInstruction(loopStart));
		final int loopEndInstructionPtr = this.instructions.size();
		final ConditionalBranchInstruction conditionalLoopEndJump = new ConditionalBranchInstruction(
				loopEndInstructionPtr);
		for (final int conditionalBranchInstructionPtr : this.loopStartInstructionPtrs.pop().exitWhenInstPtrs) {
			this.instructions.set(conditionalBranchInstructionPtr, conditionalLoopEndJump);
		}
		return null;
	}

	@Override
	public Void visit(final JassReturnNothingStatement statement) {
		this.instructions.add(PUSH_NOTHING);
		this.instructions.add(ReturnInstruction.INSTANCE);
		return null;
	}

	@Override
	public Void visit(final JassReturnStatement statement) {
		insertExpressionInstructions(statement.getExpression());
		this.instructions.add(ReturnInstruction.INSTANCE);
		return null;
	}

	@Override
	public Void visit(final JassSetStatement statement) {
		final String identifier = statement.getIdentifier();
		insertExpressionInstructions(statement.getExpression());
		final int localId = getLocalId(identifier);
		if (localId != -1) {
			if (CHECK_TYPES) {
				final JassType type = this.nameToLocalType.get(identifier);
				this.instructions.add(new TypeCheckInstruction(type));
			}
			this.instructions.add(new LocalAssignmentInstruction(localId));
		}
		else {
			final Integer thisStruct = this.nameToLocalId.get(GlobalScope.KEYNAME_THIS);
			final JassType thisStructType = this.nameToLocalType.get(GlobalScope.KEYNAME_THIS);
			boolean found = false;
			if (thisStruct != null) {
				final StructJassType structJassType = thisStructType.visit(StructJassTypeVisitor.getInstance());
				final JassStructMemberType memberType = structJassType.tryGetMemberByName(identifier);
				if (memberType != null) {
					checkMemberAccess(identifier, structJassType, memberType);

					final int memberIndex = structJassType.tryGetMemberIndexInefficientlyByName(identifier);
					if (memberIndex != -1) {
						this.instructions.add(new LocalReferenceInstruction(thisStruct));
						this.instructions.add(new SetStructMemberInstruction(memberIndex));
						found = true;
					}
				}
			}
			if (!found) {
				if (this.enclosingStructType != null) {
					final int staticStructValueGlobalId = this.scope.getGlobalId(this.enclosingStructType.getName());
					final GlobalScopeAssignable globalById = this.scope
							.getAssignableGlobalById(staticStructValueGlobalId);
					final StaticStructTypeJassValue structJassType = globalById.getValue()
							.visit(StaticStructTypeJassValueVisitor.getInstance());

					final JassStructMemberType memberType = structJassType.tryGetMemberByName(identifier);
					if (memberType != null) {
						checkMemberAccess(identifier, structJassType, memberType);

						final int memberIndex = structJassType.tryGetMemberIndexInefficientlyByName(identifier);
						if (memberIndex != -1) {
							this.instructions.add(new GlobalReferenceInstruction(staticStructValueGlobalId));
							this.instructions.add(new SetStructMemberInstruction(memberIndex));
							found = true;
						}
					}
				}
			}
			if (!found) {
				final int globalId = this.scope.getGlobalId(identifier);
				if (globalId != -1) {
					if (CHECK_TYPES) {
						final JassType type = this.scope.getAssignableGlobalById(globalId).getType();
						this.instructions.add(new TypeCheckInstruction(type));
					}
					this.instructions.add(new GlobalAssignmentInstruction(globalId));
				}
			}

		}
		return null;
	}

	@Override
	public Void visit(final JassSetMemberStatement statement) {
		final String identifier = statement.getIdentifier();
		final JassExpression structExpression = statement.getStructExpression();
		insertExpressionInstructions(statement.getExpression());
		insertExpressionInstructions(structExpression);

		final JassType expressionLookupType = structExpression.accept(JassTypeExpressionVisitor.getInstance()
				.reset(this.scope, this.nameToLocalType, this.enclosingStructType));
		final StructJassType structJassType = expressionLookupType.visit(StructJassTypeVisitor.getInstance());
		StructJassTypeInterface typeInterface;
		if (structJassType == null) {
			typeInterface = expressionLookupType.visit(StaticStructTypeJassTypeVisitor.getInstance());
		}
		else {
			typeInterface = structJassType;
		}
		final JassStructMemberType memberType = typeInterface.getMemberByName(identifier);
		checkMemberAccess(identifier, typeInterface, memberType);

		final int memberIndex = typeInterface.getMemberIndexInefficientlyByName(identifier);
		this.instructions.add(new SetStructMemberInstruction(memberIndex));

		return null;
	}

	@Override
	public Void visit(final DebuggingJassStatement statement) {
		this.instructions.add(new SetDebugLineNoInstruction(statement.getLineNo()));
		try {
			statement.getDelegate().accept(this);
		}
		catch (final Exception exc) {
			throw new IllegalStateException("pseudocompile fail beneath line no: " + statement.getLineNo(), exc);
		}
		return null;
	}

	// Expressions

	private void insertExpressionInstructions(final JassExpression expression) {
		expression.accept(this);
	}

	private void insertReferenceExpressionInstructions(final String identifier) {
		int localId = getLocalId(identifier);
		if ((localId == -1) && GlobalScope.KEYWORD_THIS.equals(identifier)) {
			localId = getLocalId(GlobalScope.KEYNAME_THIS);
		}
		if (localId != -1) {
			this.instructions.add(new LocalReferenceInstruction(localId));
		}
		else {
			final Integer thisStruct = this.nameToLocalId.get(GlobalScope.KEYNAME_THIS);
			final JassType thisStructType = this.nameToLocalType.get(GlobalScope.KEYNAME_THIS);
			boolean found = false;
			if (thisStruct != null) {
				final StructJassType structJassType = thisStructType.visit(StructJassTypeVisitor.getInstance());
				final JassStructMemberType memberType = structJassType.tryGetMemberByName(identifier);
				if (memberType != null) {
					checkMemberAccess(identifier, structJassType, memberType);

					final int memberIndex = structJassType.tryGetMemberIndexInefficientlyByName(identifier);
					if (memberIndex != -1) {
						this.instructions.add(new LocalReferenceInstruction(thisStruct));
						this.instructions.add(new StructMemberReferenceInstruction(memberIndex));
						found = true;
					}
				}
			}
			if (!found) {
				if (this.enclosingStructType != null) {
					final int staticStructValueGlobalId = this.scope.getGlobalId(this.enclosingStructType.getName());
					final GlobalScopeAssignable globalById = this.scope
							.getAssignableGlobalById(staticStructValueGlobalId);
					final StaticStructTypeJassValue structJassType = globalById.getValue()
							.visit(StaticStructTypeJassValueVisitor.getInstance());

					final JassStructMemberType memberType = structJassType.tryGetMemberByName(identifier);
					if (memberType != null) {
						checkMemberAccess(identifier, structJassType, memberType);

						final int memberIndex = structJassType.tryGetMemberIndexInefficientlyByName(identifier);
						if (memberIndex != -1) {
							this.instructions.add(new GlobalReferenceInstruction(staticStructValueGlobalId));
							this.instructions.add(new StructMemberReferenceInstruction(memberIndex));
							found = true;
						}
					}
				}
			}
			if (!found) {
				final int globalId = this.scope.getGlobalId(identifier);
				if (globalId != -1) {
					this.instructions.add(new GlobalReferenceInstruction(globalId));
				}
				else {
					final JassValue constantValue = this.scope.getPreprocessorConstant(identifier);
					if (constantValue != null) {
						this.instructions.add(new PushLiteralInstruction(constantValue));
					}
					else {
						throw new IllegalArgumentException("No such identifier: " + identifier);
					}
				}
			}
		}
	}

	private void checkMemberAccess(final String identifier, final StructJassTypeInterface structJassType,
			final JassStructMemberType memberType) {
		if (memberType.getQualifiers().contains(JassQualifier.PRIVATE)) {
			// ensure that caller is the same as enclosing type
			if ((this.enclosingStructType == null)
					|| !this.enclosingStructType.canAccessPrivateMethodsOf(structJassType)) {
				throw this.scope.createException(
						"Attempted to access private member when it is not allowed: " + identifier,
						new IllegalArgumentException());
			}
		}
	}

	@Override
	public Void visit(final ArithmeticJassExpression expression) {
		final JassExpression leftExpression = expression.getLeftExpression();
		final JassExpression rightExpression = expression.getRightExpression();
		final ArithmeticSign arithmeticSign = expression.getArithmeticSign();
		insertExpressionInstructions(leftExpression);
		final JassValue shortCircuitValue = arithmeticSign.getShortCircuitValue();
		final boolean shortCircuitAllowed = shortCircuitValue != null;
		int conditionalBranchInstructionPtr = -1;
		if (shortCircuitAllowed) {
			// look for short circuit
			this.instructions.add(new PeekInstruction());
			conditionalBranchInstructionPtr = this.instructions.size();
			this.instructions.add(null);
		}
		insertExpressionInstructions(rightExpression);
		this.instructions.add(new ArithmeticInstruction(arithmeticSign));
		if (shortCircuitAllowed) {
			if (shortCircuitValue == BooleanJassValue.FALSE) {
				this.instructions.set(conditionalBranchInstructionPtr,
						new InvertedConditionalBranchInstruction(this.instructions.size()));
			}
			else {
				this.instructions.set(conditionalBranchInstructionPtr,
						new ConditionalBranchInstruction(this.instructions.size()));
			}
		}
		return null;
	}

	@Override
	public Void visit(final ArrayRefJassExpression expression) {
		insertReferenceExpressionInstructions(expression.getIdentifier());
		insertExpressionInstructions(expression.getIndexExpression());
		this.instructions.add(new ArrayReferenceInstruction());
		return null;
	}

	@Override
	public Void visit(final ParentlessMethodCallJassExpression expression) {
		final String functionName = expression.getFunctionName();
		final List<JassExpression> arguments = expression.getArguments();
		performParentlessMethodCall(functionName, arguments);
		return null;
	}

	public void performParentlessMethodCall(final String functionName, final List<JassExpression> arguments) {
		final int argumentCount = arguments.size();
		if (this.enclosingStructType != null) {
			final JassCodeDefinitionBlock methodDefinition = this.enclosingStructType.tryGetMethodByName(functionName);
			if (methodDefinition == null) {
				insertTypeCastInstructionsOrFail(functionName, arguments, argumentCount);
			}
			else if (methodDefinition.getQualifiers().contains(JassQualifier.STATIC)) {
				// In some struct code, if they call `.doThing()` and `doThing()` is a static
				// method,
				// then we call it statically even if we were ourselves a non-static method
				insertCompileTimeStaticMethodCallInstructions(functionName, arguments, argumentCount,
						this.enclosingStructType);
			}
			else {
				final Integer thisStruct = this.nameToLocalId.get(GlobalScope.KEYNAME_THIS);
				final JassType thisStructType = this.nameToLocalType.get(GlobalScope.KEYNAME_THIS);
				StructJassType structJassType;
				if ((thisStruct != null)
						&& ((structJassType = thisStructType.visit(StructJassTypeVisitor.getInstance())) != null)) {
					this.instructions.add(new LocalReferenceInstruction(thisStruct));
					addVirtualMethodCallInstructions(functionName, arguments, argumentCount, structJassType,
							methodDefinition);
				}
				else {
					throw this.scope.createException(
							"Attempt to call nonstatic method without 'this' context: " + functionName,
							new IllegalArgumentException());
				}
			}
		}
		else {
			insertTypeCastInstructionsOrFail(functionName, arguments, argumentCount);
		}
	}

	private void insertTypeCastInstructionsOrFail(final String functionName, final List<JassExpression> arguments,
			final int argumentCount) {
		if (argumentCount == 1) {
			try {
				final JassType functionNameAsType = this.scope.parseType(functionName);
				final JassExpression jassExpression = arguments.get(0);
				insertTypeCastInstructions(functionNameAsType, jassExpression);
			}
			catch (final Exception exc) {
				throw this.scope.createException("Undefined function: " + functionName, exc);
			}
		}
		else {
			throw this.scope.createException("Undefined function: " + functionName, new IllegalArgumentException());
		}
	}

	@Override
	public Void visit(final MethodCallJassExpression expression) {
		final JassExpression structExpression = expression.getStructExpression();
		final String functionName = expression.getFunctionName();
		final List<JassExpression> arguments = expression.getArguments();
		final int argumentCount = arguments.size();
		final JassType expressionLookupType = structExpression.accept(JassTypeExpressionVisitor.getInstance()
				.reset(this.scope, this.nameToLocalType, this.enclosingStructType));
		final StructJassType structJassType = expressionLookupType.visit(StructJassTypeVisitor.getInstance());
		if (structJassType == null) {
			final StaticStructTypeJassValue staticStruct = expressionLookupType
					.visit(StaticStructTypeJassTypeVisitor.getInstance());
			final StructJassType staticType = staticStruct.getStaticType();
			insertCompileTimeStaticMethodCallInstructions(functionName, arguments, argumentCount, staticType);
		}
		else {
			final JassCodeDefinitionBlock methodDefinition = structJassType.getMethodByName(functionName);
			if (methodDefinition.getQualifiers().contains(JassQualifier.STATIC)) {
				insertCompileTimeStaticMethodCallInstructions(functionName, arguments, argumentCount, structJassType);
			}
			else {
				insertExpressionInstructions(structExpression);
				addVirtualMethodCallInstructions(functionName, arguments, argumentCount, structJassType,
						methodDefinition);
			}
		}
		return null;
	}

	private void insertCompileTimeStaticMethodCallInstructions(final String functionName,
			final List<JassExpression> arguments, final int argumentCount, final StructJassType staticType) {
		final JassCodeDefinitionBlock methodDefinition = staticType.getMethodByName(functionName);
		if (methodDefinition.getQualifiers().contains(JassQualifier.PRIVATE)) {
			// ensure that caller is the same as enclosing type
			if ((this.enclosingStructType == null) || !this.enclosingStructType.canAccessPrivateMethodsOf(staticType)) {
				throw this.scope.createException(
						"Attempted to call private method when it is not allowed: " + functionName,
						new IllegalArgumentException());
			}
		}
		final Integer tableIndex = staticType.getMethodTableIndex(functionName);
		if (tableIndex == null) {
			throw this.scope.createException("Undefined function: " + functionName, new IllegalArgumentException());
		}
		// TODO looking in method table here means that a static struct method call
		// cannot be called recursively, nor from above its declaration
		final Integer nonvirtualBranchInstructionPtr = staticType.getMethodTable().get(tableIndex);
		if (nonvirtualBranchInstructionPtr == -1) {
			throw this.scope.createException(
					"Call to a static method that was defined after us: " + functionName + " (move it before us!)",
					new IllegalArgumentException());
		}
		for (int i = 0; i < argumentCount; i++) {
			insertExpressionInstructions(arguments.get(i));
		}
		final int newStackFrameInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		this.instructions.add(new BranchInstruction(nonvirtualBranchInstructionPtr));
		this.instructions.set(newStackFrameInstructionPtr,
				new NewStackFrameInstruction(this.instructions.size(), argumentCount));
	}

	private void addVirtualMethodCallInstructions(final String functionName, final List<JassExpression> arguments,
			final int argumentCount, final StructJassType structJassType,
			final JassCodeDefinitionBlock methodDefinition) {
		if (methodDefinition.getQualifiers().contains(JassQualifier.PRIVATE)) {
			// ensure that caller is the same as enclosing type
			if ((this.enclosingStructType == null)
					|| !this.enclosingStructType.canAccessPrivateMethodsOf(structJassType)) {
				throw this.scope.createException(
						"Attempted to call private method when it is not allowed: " + functionName,
						new IllegalArgumentException());
			}
		}
		for (int i = 0; i < argumentCount; i++) {
			insertExpressionInstructions(arguments.get(i));
		}
		final Integer methodTableIndex = structJassType.getMethodTableIndex(functionName);
		if (methodTableIndex == null) {
			throw this.scope.createException("Undefined function: " + functionName, new IllegalArgumentException());
		}

		final int newStackFrameInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		this.instructions.add(new VirtualBranchInstruction(argumentCount, methodTableIndex));
		this.instructions.set(newStackFrameInstructionPtr,
				new NewStackFrameInstruction(this.instructions.size(), argumentCount + 1));
	}

	@Override
	public Void visit(final FunctionCallJassExpression expression) {
		final String functionName = expression.getFunctionName();
		final List<JassExpression> arguments = expression.getArguments();
		insertFunctionCallInstructions(functionName, arguments);
		return null;
	}

	public void insertFunctionCallInstructions(final String functionName, final List<JassExpression> arguments) {
		final Integer userFunctionInstructionPtr = this.scope.getUserFunctionInstructionPtr(functionName);
		if (userFunctionInstructionPtr != null) {
			insertFuncInstructions(arguments, new BranchInstruction(userFunctionInstructionPtr.intValue()));
		}
		else {
			final Integer nativeId = this.scope.getNativeId(functionName);
			if (nativeId != null) {
				insertFuncInstructions(arguments, new NativeInstruction(nativeId, arguments.size()));
			}
			else {
				performParentlessMethodCall(functionName, arguments);
			}
		}
	}

	private void insertFuncInstructions(final List<JassExpression> arguments, final JassInstruction branchInstruction) {
		for (int i = 0; i < arguments.size(); i++) {
			insertExpressionInstructions(arguments.get(i));
		}
		final int newStackFrameInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		this.instructions.add(branchInstruction);
		this.instructions.set(newStackFrameInstructionPtr,
				new NewStackFrameInstruction(this.instructions.size(), arguments.size()));
	}

	@Override
	public Void visit(final FunctionReferenceJassExpression expression) {
		final String identifier = expression.getIdentifier();
		final Integer userFunctionInstructionPtr = this.scope.getUserFunctionInstructionPtr(identifier);
		if (userFunctionInstructionPtr == null) {
			throw new RuntimeException("Unable to find function: " + identifier);
		}
		this.instructions.add(new PushLiteralInstruction(new CodeJassValue(userFunctionInstructionPtr)));
		return null;
	}

	@Override
	public Void visit(final MethodReferenceJassExpression expression) {
		final JassExpression structExpression = expression.getStructExpression();
		final String functionName = expression.getIdentifier();
		final JassType expressionLookupType = structExpression.accept(JassTypeExpressionVisitor.getInstance()
				.reset(this.scope, this.nameToLocalType, this.enclosingStructType));
		final StructJassType structJassType = expressionLookupType.visit(StructJassTypeVisitor.getInstance());
		if (structJassType == null) {
			final StaticStructTypeJassValue staticStruct = expressionLookupType
					.visit(StaticStructTypeJassTypeVisitor.getInstance());
			final StructJassType staticType = staticStruct.getStaticType();
			final Integer tableIndex = staticType.getMethodTableIndex(functionName);
			// TODO looking in method table here means that a static struct method call
			// cannot be called recursively, nor from above its declaration
			final Integer nonvirtualBranchInstructionPtr = staticType.getMethodTable().get(tableIndex);
			this.instructions.add(new PushLiteralInstruction(new CodeJassValue(nonvirtualBranchInstructionPtr)));
		}
		else {
			insertExpressionInstructions(structExpression);
			final Integer methodTableIndex = structJassType.getMethodTableIndex(functionName);
			this.instructions.add(new MethodReferenceInstruction(methodTableIndex));
		}
		return null;
	}

	@Override
	public Void visit(final LiteralJassExpression expression) {
		this.instructions.add(new PushLiteralInstruction(expression.getValue()));
		return null;
	}

	@Override
	public Void visit(final NegateJassExpression expression) {
		insertExpressionInstructions(expression.getExpression());
		this.instructions.add(new NegateInstruction());
		return null;
	}

	@Override
	public Void visit(final NotJassExpression expression) {
		insertExpressionInstructions(expression.getExpression());
		this.instructions.add(new NotInstruction());
		return null;
	}

	@Override
	public Void visit(final ReferenceJassExpression expression) {
		final String identifier = expression.getIdentifier();
		insertReferenceExpressionInstructions(identifier);
		return null;
	}

	@Override
	public Void visit(final MemberJassExpression expression) {
		insertExpressionInstructions(expression.getStructExpression());
		final JassType expressionLookupType = expression.getStructExpression().accept(JassTypeExpressionVisitor
				.getInstance().reset(this.scope, this.nameToLocalType, this.enclosingStructType));
		final StructJassType structJassType = expressionLookupType.visit(StructJassTypeVisitor.getInstance());
		StructJassTypeInterface typeInterface;
		if (structJassType == null) {
			typeInterface = expressionLookupType.visit(StaticStructTypeJassTypeVisitor.getInstance());
		}
		else {
			typeInterface = structJassType;
		}
		final String identifier = expression.getIdentifier();
		final JassStructMemberType memberType = typeInterface.getMemberByName(identifier);
		checkMemberAccess(identifier, typeInterface, memberType);
		final int memberIndex = typeInterface.getMemberIndexInefficientlyByName(identifier);
		this.instructions.add(new StructMemberReferenceInstruction(memberIndex));
		return null;
	}

	@Override
	public Void visit(final AllocateAsNewTypeExpression expression) {
		insertExpressionInstructions(expression.getOriginalValue());
		this.instructions.add(new AllocateStructAsNewTypeInstruction(expression.getType()));
		return null;
	}

	@Override
	public Void visit(final ExtendHandleExpression expression) {
		insertExpressionInstructions(expression.getOriginalValue());
		this.instructions.add(new ExtendHandleInstruction(expression.getType()));
		return null;
	}

	@Override
	public Void visit(final JassNewExpression expression) {
		this.instructions.add(new AllocateInstruction(expression.getType()));
		return null;
	}

	@Override
	public Void visit(final TypeCastJassExpression expression) {
		final JassType castToType = expression.getCastToType();
		final JassExpression valueExpression = expression.getValueExpression();
		insertTypeCastInstructions(castToType, valueExpression);
		return null;
	}

	@Override
	public Void visit(final JassThrowStatement statement) {
		this.instructions.add(new JassThrowInstruction(statement.getExceptionMessage()));
		return null;
	}

	private Locals saveLocals() {
		return new Locals(this.nameToLocalId, this.nameToLocalType, this.nextLocalId);
	}

	private void loadLocals(final Locals locals) {
		this.nameToLocalId = locals.nameToLocalId;
		this.nameToLocalType = locals.nameToLocalType;
		this.nextLocalId = locals.nextLocalId;
	}

	private void insertTypeCastInstructions(final JassType castToType, final JassExpression valueExpression) {
		final JassValueVisitor<JassValue> typeConverter = castToType
				.visit(TypeCastConverterGettingJassTypeVisitor.INSTANCE);
		if (typeConverter == null) {
			throw new IllegalArgumentException("Unable to build code for casting to type: " + castToType.getName());
//			this.instructions.add(new PushLiteralInstruction(castToType.getNullValue()));
		}
		insertExpressionInstructions(valueExpression);
		this.instructions.add(new TypeCastInstruction(typeConverter, castToType.getNullValue()));
	}

	private static final class LoopImpl {
		private final List<Integer> exitWhenInstPtrs = new ArrayList<>();
	}

	private static final class Locals {
		private final Map<String, Integer> nameToLocalId;
		private final Map<String, JassType> nameToLocalType;
		private final int nextLocalId;

		public Locals(final Map<String, Integer> nameToLocalId, final Map<String, JassType> nameToLocalType,
				final int nextLocalId) {
			this.nameToLocalId = new HashMap<String, Integer>(nameToLocalId);
			this.nameToLocalType = new HashMap<String, JassType>(nameToLocalType);
			this.nextLocalId = nextLocalId;
		}
	}
}

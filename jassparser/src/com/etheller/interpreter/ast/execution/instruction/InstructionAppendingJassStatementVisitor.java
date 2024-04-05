package com.etheller.interpreter.ast.execution.instruction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.debug.DebuggingJassStatement;
import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.expression.ArithmeticJassExpression;
import com.etheller.interpreter.ast.expression.ArrayRefJassExpression;
import com.etheller.interpreter.ast.expression.FunctionCallJassExpression;
import com.etheller.interpreter.ast.expression.FunctionReferenceJassExpression;
import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.expression.JassExpressionVisitor;
import com.etheller.interpreter.ast.expression.LiteralJassExpression;
import com.etheller.interpreter.ast.expression.NegateJassExpression;
import com.etheller.interpreter.ast.expression.NotJassExpression;
import com.etheller.interpreter.ast.expression.ReferenceJassExpression;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.statement.JassArrayedAssignmentStatement;
import com.etheller.interpreter.ast.statement.JassCallStatement;
import com.etheller.interpreter.ast.statement.JassDoNothingStatement;
import com.etheller.interpreter.ast.statement.JassExitWhenStatement;
import com.etheller.interpreter.ast.statement.JassIfElseIfStatement;
import com.etheller.interpreter.ast.statement.JassIfElseStatement;
import com.etheller.interpreter.ast.statement.JassIfStatement;
import com.etheller.interpreter.ast.statement.JassLocalDefinitionStatement;
import com.etheller.interpreter.ast.statement.JassLocalStatement;
import com.etheller.interpreter.ast.statement.JassLoopStatement;
import com.etheller.interpreter.ast.statement.JassReturnNothingStatement;
import com.etheller.interpreter.ast.statement.JassReturnStatement;
import com.etheller.interpreter.ast.statement.JassSetStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.statement.JassStatementVisitor;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.visitor.ArrayTypeVisitor;

public class InstructionAppendingJassStatementVisitor
		implements JassStatementVisitor<Void>, JassExpressionVisitor<Void> {
	private static final PushLiteralInstruction PUSH_NOTHING = new PushLiteralInstruction(
			JassReturnNothingStatement.RETURN_NOTHING_NOTICE);
	private final List<JassInstruction> instructions;
	private final GlobalScope globalScope;
	private final Map<String, Integer> nameToLocalId = new HashMap<>();
	private int nextLocalId;
	private final ArrayDeque<LoopImpl> loopStartInstructionPtrs = new ArrayDeque<>();

	public InstructionAppendingJassStatementVisitor(final List<JassInstruction> instructions,
			final GlobalScope globalScope, final List<JassParameter> parameters) {
		this.instructions = instructions;
		this.globalScope = globalScope;
		this.nextLocalId = 0;
		for (final JassParameter parameter : parameters) {
			this.nameToLocalId.put(parameter.getIdentifier(), this.nextLocalId++);
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
			final int globalId = this.globalScope.getGlobalId(identifier);
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
		for (final JassStatement thenStatement : statement.getThenStatements()) {
			thenStatement.accept(this);
		}
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
		for (final JassStatement thenStatement : statement.getThenStatements()) {
			thenStatement.accept(this);
		}
		final int branchEndInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		final int elseStatementsStart = this.instructions.size();
		this.instructions.set(branchInstructionPtr, new InvertedConditionalBranchInstruction(elseStatementsStart));
		for (final JassStatement thenStatement : statement.getElseStatements()) {
			thenStatement.accept(this);
		}
		this.instructions.set(branchEndInstructionPtr, new BranchInstruction(this.instructions.size()));
		return null;
	}

	@Override
	public Void visit(final JassIfStatement statement) {
		insertExpressionInstructions(statement.getCondition());
		final int branchInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		for (final JassStatement thenStatement : statement.getThenStatements()) {
			thenStatement.accept(this);
		}
		this.instructions.set(branchInstructionPtr, new InvertedConditionalBranchInstruction(this.instructions.size()));
		return null;
	}

	@Override
	public Void visit(final JassLocalDefinitionStatement statement) {
		final String identifier = statement.getIdentifier();
		this.nameToLocalId.put(identifier, this.nextLocalId++);
		insertExpressionInstructions(statement.getExpression());
		return null;
	}

	@Override
	public Void visit(final JassLocalStatement statement) {
		final String identifier = statement.getIdentifier();
		final JassType type = statement.getType();
		this.nameToLocalId.put(identifier, this.nextLocalId++);
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
			this.instructions.add(new LocalAssignmentInstruction(localId));
		}
		else {
			final int globalId = this.globalScope.getGlobalId(identifier);
			if (globalId != -1) {
				this.instructions.add(new GlobalAssignmentInstruction(globalId));
			}
		}
		return null;
	}

	@Override
	public Void visit(final DebuggingJassStatement statement) {
		this.instructions.add(new SetDebugLineNoInstruction(statement.getLineNo()));
		statement.getDelegate().accept(this);
		return null;
	}

	// Expressions

	private void insertExpressionInstructions(final JassExpression expression) {
		expression.accept(this);
	}

	private void insertReferenceExpressionInstructions(final String identifier) {
		final int localId = getLocalId(identifier);
		if (localId != -1) {
			this.instructions.add(new LocalReferenceInstruction(localId));
		}
		else {
			final int globalId = this.globalScope.getGlobalId(identifier);
			if (globalId != -1) {
				this.instructions.add(new GlobalReferenceInstruction(globalId));
			}
			else {
				throw new IllegalArgumentException("No such identifier: " + identifier);
			}
		}
	}

	@Override
	public Void visit(final ArithmeticJassExpression expression) {
		insertExpressionInstructions(expression.getLeftExpression());
		insertExpressionInstructions(expression.getRightExpression());
		this.instructions.add(new ArithmeticInstruction(expression.getArithmeticSign()));
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
	public Void visit(final FunctionCallJassExpression expression) {
		final String functionName = expression.getFunctionName();
		final List<JassExpression> arguments = expression.getArguments();
		insertFunctionCallInstructions(functionName, arguments);
		return null;
	}

	public void insertFunctionCallInstructions(final String functionName, final List<JassExpression> arguments) {
		for (int i = 0; i < arguments.size(); i++) {
			insertExpressionInstructions(arguments.get(i));
		}
		final int newStackFrameInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		final Integer userFunctionInstructionPtr = this.globalScope.getUserFunctionInstructionPtr(functionName);
		if (userFunctionInstructionPtr != null) {
			this.instructions.add(new BranchInstruction(userFunctionInstructionPtr.intValue()));
		}
		else {
			final Integer nativeId = this.globalScope.getNativeId(functionName);
			if (nativeId != null) {
				this.instructions.add(new NativeInstruction(nativeId, arguments.size()));
			}
			else {
				throw new JassException(this.globalScope, "Undefined function: " + functionName,
						new RuntimeException());
			}
		}
		this.instructions.set(newStackFrameInstructionPtr,
				new NewStackFrameInstruction(this.instructions.size(), arguments.size()));
	}

	@Override
	public Void visit(final FunctionReferenceJassExpression expression) {
		final String identifier = expression.getIdentifier();
		final JassFunction functionByName = this.globalScope.getFunctionByName(identifier);
		final Integer userFunctionInstructionPtr = this.globalScope.getUserFunctionInstructionPtr(identifier);
		if ((functionByName == null) || (userFunctionInstructionPtr == null)) {
			throw new RuntimeException("Unable to find function: " + identifier);
		}
		this.instructions
				.add(new PushLiteralInstruction(new CodeJassValue(functionByName, userFunctionInstructionPtr)));
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

	private static final class LoopImpl {
		private final List<Integer> exitWhenInstPtrs = new ArrayList<>();
	}
}

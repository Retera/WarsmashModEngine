package com.etheller.interpreter.ast.execution.instruction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.expression.ArithmeticSign;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.statement.JassReturnNothingStatement;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.visitor.ArrayTypeVisitor;

public class InstructionWriter {
	private static final PushLiteralInstruction PUSH_NOTHING = new PushLiteralInstruction(
			JassReturnNothingStatement.RETURN_NOTHING_NOTICE);
	private final List<JassInstruction> instructions;
	private final GlobalScope globalScope;
	private final Map<String, Integer> nameToLocalId = new HashMap<>();
	private int nextLocalId;
	private final ArrayDeque<LoopImpl> loopStartInstructionPtrs = new ArrayDeque<>();
	private final ArrayDeque<IfImpl> ifBlocks = new ArrayDeque<>();

	public InstructionWriter(final List<JassInstruction> instructions, final GlobalScope globalScope,
			final List<JassParameter> parameters) {
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

	public void arrayedAssignmentStatement(final String identifier) {
		// insertExpressionInstructions(statement.getIndexExpression());
		// insertExpressionInstructions(statement.getExpression());
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
	}

	public void callStatement(final String functionName, final int argumentCount) {
		call(functionName, argumentCount);
		popInstruction();
	}

	public void popInstruction() {
		this.instructions.add(PopInstruction.INSTANCE);
	}

	public void doNothingStatement() {
		this.instructions.add(DoNothingInstruction.INSTANCE);
	}

	public void exitWhenStatement() {
//		insertExpressionInstructions(statement.getExpression());
		final int conditionBranchInstructionPtr = this.instructions.size();
		this.loopStartInstructionPtrs.peek().exitWhenInstPtrs.add(conditionBranchInstructionPtr);
		this.instructions.add(null);
	}

	public void beginIf() {
		final IfImpl ifBlock = new IfImpl();
		this.ifBlocks.push(ifBlock);
		ifBlock.branchInstructionPtr = this.instructions.size();
		this.instructions.add(null);
	}

	public void beginElse() {
		final IfImpl ifBlock = this.ifBlocks.peek();
		ifBlock.branchEndInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		final int elseStatementsStart = this.instructions.size();
		this.instructions.set(ifBlock.branchInstructionPtr,
				new InvertedConditionalBranchInstruction(elseStatementsStart));
	}

	public void endElse() {
		final IfImpl ifBlock = this.ifBlocks.pop();
		this.instructions.set(ifBlock.branchEndInstructionPtr, new BranchInstruction(this.instructions.size()));
	}

	public void endIf() {
		final IfImpl ifBlock = this.ifBlocks.pop();
		this.instructions.set(ifBlock.branchInstructionPtr,
				new InvertedConditionalBranchInstruction(this.instructions.size()));
	}

	public void pushDefaultValue(final JassType type) {
		final ArrayJassType arrayType = type.visit(ArrayTypeVisitor.getInstance());
		if (arrayType != null) {
			this.instructions.add(new DeclareLocalArrayInstruction(arrayType));
		}
		else {
			this.instructions.add(new PushLiteralInstruction(type.getNullValue()));
		}
	}

	public void declareLocal(final String identifier) {
		this.nameToLocalId.put(identifier, this.nextLocalId++);
	}

	public void loop() {
		final int loopStart = this.instructions.size();
		this.loopStartInstructionPtrs.push(new LoopImpl(loopStart));
	}

	public void endloop() {
		final LoopImpl loopImpl = this.loopStartInstructionPtrs.pop();
		this.instructions.add(new BranchInstruction(loopImpl.loopStart));
		final int loopEndInstructionPtr = this.instructions.size();
		final ConditionalBranchInstruction conditionalLoopEndJump = new ConditionalBranchInstruction(
				loopEndInstructionPtr);
		for (final int conditionalBranchInstructionPtr : loopImpl.exitWhenInstPtrs) {
			this.instructions.set(conditionalBranchInstructionPtr, conditionalLoopEndJump);
		}
	}

	public void returnNothingInstruction() {
		this.instructions.add(PUSH_NOTHING);
		returnInstruction();
	}

	public void returnInstruction() {
		this.instructions.add(ReturnInstruction.INSTANCE);
	}

	public void set(final String identifier) {
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
	}

	public void setGlobal(final String identifier) {
		final int globalId = this.globalScope.getGlobalId(identifier);
		if (globalId != -1) {
			this.instructions.add(new GlobalAssignmentInstruction(globalId));
		}
	}

	public void setLineNo(final int lineNo) {
		this.instructions.add(new SetDebugLineNoInstruction(lineNo));
	}

	// Expressions

	public void referenceExpression(final String identifier) {
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

	public void arithmetic(final ArithmeticSign sign) {
		this.instructions.add(new ArithmeticInstruction(sign));
	}

	public void arrayReferenceInstruction() {
		this.instructions.add(new ArrayReferenceInstruction());
	}

	public void callExpression(final String functionName, final int argumentCount) {
		call(functionName, argumentCount);
	}

	public void call(final String functionName, final int argumentCount) {
		final int newStackFrameInstructionPtr = this.instructions.size();
		this.instructions.add(null);
		final Integer userFunctionInstructionPtr = this.globalScope.getUserFunctionInstructionPtr(functionName);
		if (userFunctionInstructionPtr != null) {
			this.instructions.add(new BranchInstruction(userFunctionInstructionPtr.intValue()));
		}
		else {
			final Integer nativeId = this.globalScope.getNativeId(functionName);
			if (nativeId != null) {
				this.instructions.add(new NativeInstruction(nativeId, argumentCount));
			}
			else {
				throw new JassException(this.globalScope, "Undefined function: " + functionName,
						new RuntimeException());
			}
		}
		this.instructions.set(newStackFrameInstructionPtr,
				new NewStackFrameInstruction(this.instructions.size(), argumentCount));
	}

	public void functionReference(final String identifier) {
		final JassFunction functionByName = this.globalScope.getFunctionByName(identifier);
		final Integer userFunctionInstructionPtr = this.globalScope.getUserFunctionInstructionPtr(identifier);
		if ((functionByName == null) || (userFunctionInstructionPtr == null)) {
			throw new RuntimeException("Unable to find function: " + identifier);
		}
		this.instructions
				.add(new PushLiteralInstruction(new CodeJassValue(functionByName, userFunctionInstructionPtr)));
	}

	public void literal(final JassValue value) {
		this.instructions.add(new PushLiteralInstruction(value));
	}

	public void stringLiteral(final String value) {
		literal(StringJassValue.of(value));
	}

	public void integerLiteral(final int x) {
		literal(IntegerJassValue.of(x));
	}

	public void realLiteral(final double x) {
		literal(RealJassValue.of(x));
	}

	public void booleanLiteral(final boolean x) {
		literal(BooleanJassValue.of(x));
	}

	public void negateInstruction() {
		this.instructions.add(new NegateInstruction());
	}

	public void notInstruction() {
		this.instructions.add(new NotInstruction());
	}

	public void endFunction() {
		this.instructions.add(new PushLiteralInstruction(JassType.NOTHING.getNullValue()));
		this.instructions.add(ReturnInstruction.INSTANCE);
	}

	public void endGlobals() {
		this.instructions.add(new PushLiteralInstruction(JassType.NOTHING.getNullValue()));
		this.instructions.add(ReturnInstruction.INSTANCE);
	}

	private static final class LoopImpl {
		private final int loopStart;

		public LoopImpl(final int loopStart) {
			this.loopStart = loopStart;
		}

		private final List<Integer> exitWhenInstPtrs = new ArrayList<>();
	}

	private static final class IfImpl {
		private int branchInstructionPtr;
		private int branchEndInstructionPtr;
	}
}

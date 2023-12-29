package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.expression.ArithmeticSign;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.ArithmeticJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.ArithmeticLeftHandNullJassValueVisitor;

public class ArithmeticInstruction implements JassInstruction {
	private final ArithmeticSign arithmeticSign;

	public ArithmeticInstruction(final ArithmeticSign arithmeticSign) {
		this.arithmeticSign = arithmeticSign;
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue rightValue = thread.stackFrame.pop();
		final JassValue leftValue = thread.stackFrame.pop();
		JassValue result;
		try {
			if (leftValue == null) {
				if (rightValue == null) {
					result = this.arithmeticSign.apply((String) null, (String) null);
				}
				else {
					result = rightValue
							.visit(ArithmeticLeftHandNullJassValueVisitor.INSTANCE.reset(this.arithmeticSign));
				}
			}
			else {
				result = leftValue.visit(ArithmeticJassValueVisitor.INSTANCE.reset(rightValue, this.arithmeticSign));
			}
		}
		catch (final ArithmeticException exception) {
			exception.printStackTrace();
			result = IntegerJassValue.ZERO;
		}
		thread.stackFrame.push(result);
	}
}

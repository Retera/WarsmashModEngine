package com.etheller.interpreter.ast.debug;

import java.util.List;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;

public class DebuggingJassFunction implements JassFunction {
	private final int lineNo;
	private final String sourceFile;
	private final String functionName;
	private final JassFunction delegate;

	public DebuggingJassFunction(final int lineNo, final String sourceFile, final String functionName,
			final JassFunction delegate) {
		this.lineNo = lineNo;
		this.sourceFile = sourceFile;
		this.functionName = functionName;
		this.delegate = delegate;
	}

	@Override
	public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
			final TriggerExecutionScope triggerScope) {
		globalScope.pushJassStack(new JassStackElement(this.sourceFile, this.functionName, this.lineNo));
		globalScope.setLineNumber(this.lineNo);
		try {
			return this.delegate.call(arguments, globalScope, triggerScope);
		}
		finally {
			globalScope.popJassStack();
		}
	}

}

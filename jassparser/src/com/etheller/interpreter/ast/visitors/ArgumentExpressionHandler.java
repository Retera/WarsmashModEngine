package com.etheller.interpreter.ast.visitors;

public class ArgumentExpressionHandler {
	protected JassArgumentsVisitor argumentsVisitor;
	protected JassExpressionVisitor expressionVisitor;

	public void setJassArgumentsVisitor(final JassArgumentsVisitor jassArgumentsVisitor) {
		this.argumentsVisitor = jassArgumentsVisitor;
	}

	public void setJassExpressionVisitor(final JassExpressionVisitor jassExpressionVisitor) {
		this.expressionVisitor = jassExpressionVisitor;
	}

}

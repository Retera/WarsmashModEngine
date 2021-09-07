package com.etheller.interpreter.ast.visitors;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.EmptyArgumentContext;
import com.etheller.interpreter.JassParser.ListArgumentContext;
import com.etheller.interpreter.JassParser.SingleArgumentContext;
import com.etheller.interpreter.ast.expression.JassExpression;

public class JassArgumentsVisitor extends JassBaseVisitor<List<JassExpression>> {
	private final ArgumentExpressionHandler argumentExpressionHandler;

	public JassArgumentsVisitor(final ArgumentExpressionHandler argumentExpressionHandler) {
		this.argumentExpressionHandler = argumentExpressionHandler;
	}

	@Override
	public List<JassExpression> visitSingleArgument(final SingleArgumentContext ctx) {
		final List<JassExpression> list = new LinkedList<>();
		list.add(this.argumentExpressionHandler.expressionVisitor.visit(ctx.expression()));
		return list;
	}

	@Override
	public List<JassExpression> visitListArgument(final ListArgumentContext ctx) {
		final List<JassExpression> list = visit(ctx.argsList());
		list.add(0, this.argumentExpressionHandler.expressionVisitor.visit(ctx.expression()));
		return list;
	}

	@Override
	public List<JassExpression> visitEmptyArgument(final EmptyArgumentContext ctx) {
		return Collections.emptyList();
	}
}

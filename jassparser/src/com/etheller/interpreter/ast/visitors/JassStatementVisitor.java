package com.etheller.interpreter.ast.visitors;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.ArrayedAssignmentStatementContext;
import com.etheller.interpreter.JassParser.CallStatementContext;
import com.etheller.interpreter.JassParser.ReturnStatementContext;
import com.etheller.interpreter.JassParser.SetStatementContext;
import com.etheller.interpreter.ast.statement.JassArrayedAssignmentStatement;
import com.etheller.interpreter.ast.statement.JassCallStatement;
import com.etheller.interpreter.ast.statement.JassReturnStatement;
import com.etheller.interpreter.ast.statement.JassSetStatement;
import com.etheller.interpreter.ast.statement.JassStatement;

public class JassStatementVisitor extends JassBaseVisitor<JassStatement> {
	private final ArgumentExpressionHandler argumentExpressionHandler;

	public JassStatementVisitor(final ArgumentExpressionHandler argumentExpressionHandler) {
		this.argumentExpressionHandler = argumentExpressionHandler;
	}

	@Override
	public JassStatement visitCallStatement(final CallStatementContext ctx) {
		return new JassCallStatement(ctx.functionExpression().ID().getText(),
				argumentExpressionHandler.argumentsVisitor.visit(ctx.functionExpression().argsList()));
	}

	@Override
	public JassStatement visitSetStatement(final SetStatementContext ctx) {
		return new JassSetStatement(ctx.ID().getText(),
				argumentExpressionHandler.expressionVisitor.visit(ctx.expression()));
	}

	@Override
	public JassStatement visitReturnStatement(final ReturnStatementContext ctx) {
		return new JassReturnStatement(argumentExpressionHandler.expressionVisitor.visit(ctx.expression()));
	}

	@Override
	public JassStatement visitArrayedAssignmentStatement(final ArrayedAssignmentStatementContext ctx) {
		return new JassArrayedAssignmentStatement(ctx.ID().getText(),
				argumentExpressionHandler.expressionVisitor.visit(ctx.expression(0)),
				argumentExpressionHandler.expressionVisitor.visit(ctx.expression(1)));
	}
}

package com.etheller.interpreter.ast.visitors;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.ArrayedAssignmentStatementContext;
import com.etheller.interpreter.JassParser.CallStatementContext;
import com.etheller.interpreter.JassParser.IfElseIfStatementContext;
import com.etheller.interpreter.JassParser.IfElseStatementContext;
import com.etheller.interpreter.JassParser.ReturnStatementContext;
import com.etheller.interpreter.JassParser.SetStatementContext;
import com.etheller.interpreter.JassParser.SimpleIfStatementContext;
import com.etheller.interpreter.JassParser.StatementContext;
import com.etheller.interpreter.ast.statement.JassArrayedAssignmentStatement;
import com.etheller.interpreter.ast.statement.JassCallStatement;
import com.etheller.interpreter.ast.statement.JassIfElseIfStatement;
import com.etheller.interpreter.ast.statement.JassIfElseStatement;
import com.etheller.interpreter.ast.statement.JassIfStatement;
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
		return new JassCallStatement(ctx.getStart().getLine(), ctx.functionExpression().ID().getText(),
				this.argumentExpressionHandler.argumentsVisitor.visit(ctx.functionExpression().argsList()));
	}

	@Override
	public JassStatement visitSetStatement(final SetStatementContext ctx) {
		return new JassSetStatement(ctx.getStart().getLine(), ctx.ID().getText(),
				this.argumentExpressionHandler.expressionVisitor.visit(ctx.expression()));
	}

	@Override
	public JassStatement visitReturnStatement(final ReturnStatementContext ctx) {
		return new JassReturnStatement(ctx.getStart().getLine(),
				this.argumentExpressionHandler.expressionVisitor.visit(ctx.expression()));
	}

	@Override
	public JassStatement visitIfElseIfStatement(final IfElseIfStatementContext ctx) {
		final List<JassStatement> thenStatements = new ArrayList<>();
		for (final StatementContext statementCtx : ctx.statements().statement()) {
			thenStatements.add(visit(statementCtx));
		}
		final JassStatement elseIfTail = visit(ctx.ifStatementPartial());
		return new JassIfElseIfStatement(ctx.getStart().getLine(),
				this.argumentExpressionHandler.expressionVisitor.visit(ctx.expression()), thenStatements, elseIfTail);
	}

	@Override
	public JassStatement visitIfElseStatement(final IfElseStatementContext ctx) {
		final List<JassStatement> thenStatements = new ArrayList<>();
		for (final StatementContext statementCtx : ctx.statements(0).statement()) {
			thenStatements.add(visit(statementCtx));
		}
		final List<JassStatement> elseStatements = new ArrayList<>();
		for (final StatementContext statementCtx : ctx.statements(1).statement()) {
			elseStatements.add(visit(statementCtx));
		}
		return new JassIfElseStatement(ctx.getStart().getLine(),
				this.argumentExpressionHandler.expressionVisitor.visit(ctx.expression()), thenStatements,
				elseStatements);
	}

	@Override
	public JassStatement visitSimpleIfStatement(final SimpleIfStatementContext ctx) {
		final List<JassStatement> thenStatements = new ArrayList<>();
		for (final StatementContext statementCtx : ctx.statements().statement()) {
			thenStatements.add(visit(statementCtx));
		}
		return new JassIfStatement(ctx.getStart().getLine(),
				this.argumentExpressionHandler.expressionVisitor.visit(ctx.expression()), thenStatements);
	}

	@Override
	public JassStatement visitArrayedAssignmentStatement(final ArrayedAssignmentStatementContext ctx) {
		return new JassArrayedAssignmentStatement(ctx.getStart().getLine(), ctx.ID().getText(),
				this.argumentExpressionHandler.expressionVisitor.visit(ctx.expression(0)),
				this.argumentExpressionHandler.expressionVisitor.visit(ctx.expression(1)));
	}
}

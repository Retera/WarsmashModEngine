package com.etheller.interpreter.ast.visitors;

import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.ArgsListContext;
import com.etheller.interpreter.JassParser.ArrayReferenceExpressionContext;
import com.etheller.interpreter.JassParser.FalseExpressionContext;
import com.etheller.interpreter.JassParser.FunctionCallExpressionContext;
import com.etheller.interpreter.JassParser.FunctionReferenceExpressionContext;
import com.etheller.interpreter.JassParser.IntegerLiteralExpressionContext;
import com.etheller.interpreter.JassParser.NotExpressionContext;
import com.etheller.interpreter.JassParser.ParentheticalExpressionContext;
import com.etheller.interpreter.JassParser.ReferenceExpressionContext;
import com.etheller.interpreter.JassParser.StringLiteralExpressionContext;
import com.etheller.interpreter.JassParser.TrueExpressionContext;
import com.etheller.interpreter.ast.expression.ArrayRefJassExpression;
import com.etheller.interpreter.ast.expression.FunctionCallJassExpression;
import com.etheller.interpreter.ast.expression.FunctionReferenceJassExpression;
import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.expression.LiteralJassExpression;
import com.etheller.interpreter.ast.expression.NotJassExpression;
import com.etheller.interpreter.ast.expression.ReferenceJassExpression;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;

public class JassExpressionVisitor extends JassBaseVisitor<JassExpression> {
	private final ArgumentExpressionHandler argumentExpressionHandler;

	public JassExpressionVisitor(final ArgumentExpressionHandler argumentExpressionHandler) {
		this.argumentExpressionHandler = argumentExpressionHandler;
	}

	@Override
	public JassExpression visitReferenceExpression(final ReferenceExpressionContext ctx) {
		return new ReferenceJassExpression(ctx.ID().getText());
	}

	@Override
	public JassExpression visitParentheticalExpression(final ParentheticalExpressionContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public JassExpression visitStringLiteralExpression(final StringLiteralExpressionContext ctx) {
		final String stringLiteralText = ctx.STRING_LITERAL().getText();
		final String parsedString = stringLiteralText.substring(1, stringLiteralText.length() - 1).replace("\\\\",
				"\\");
		return new LiteralJassExpression(new StringJassValue(parsedString));
	}

	@Override
	public JassExpression visitIntegerLiteralExpression(final IntegerLiteralExpressionContext ctx) {
		return new LiteralJassExpression(new IntegerJassValue(Integer.parseInt(ctx.INTEGER().getText())));
	}

	@Override
	public JassExpression visitFunctionReferenceExpression(final FunctionReferenceExpressionContext ctx) {
		return new FunctionReferenceJassExpression(ctx.ID().getText());
	}

	@Override
	public JassExpression visitArrayReferenceExpression(final ArrayReferenceExpressionContext ctx) {
		return new ArrayRefJassExpression(ctx.ID().getText(), visit(ctx.expression()));
	}

	@Override
	public JassExpression visitFalseExpression(final FalseExpressionContext ctx) {
		return new LiteralJassExpression(BooleanJassValue.FALSE);
	}

	@Override
	public JassExpression visitTrueExpression(final TrueExpressionContext ctx) {
		return new LiteralJassExpression(BooleanJassValue.TRUE);
	}

	@Override
	public JassExpression visitNotExpression(final NotExpressionContext ctx) {
		return new NotJassExpression(visit(ctx.expression()));
	}

	@Override
	public JassExpression visitFunctionCallExpression(final FunctionCallExpressionContext ctx) {
		final ArgsListContext argsList = ctx.functionExpression().argsList();
		final List<JassExpression> arguments = argsList == null ? Collections.<JassExpression>emptyList()
				: this.argumentExpressionHandler.argumentsVisitor.visit(argsList);
		return new FunctionCallJassExpression(ctx.functionExpression().ID().getText(), arguments);
	}
}

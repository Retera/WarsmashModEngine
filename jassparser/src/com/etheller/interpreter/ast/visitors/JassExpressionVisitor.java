package com.etheller.interpreter.ast.visitors;

import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.AdditionExpressionContext;
import com.etheller.interpreter.JassParser.ArgsListContext;
import com.etheller.interpreter.JassParser.ArrayReferenceExpressionContext;
import com.etheller.interpreter.JassParser.BooleanAndExpressionContext;
import com.etheller.interpreter.JassParser.BooleanGreaterExpressionContext;
import com.etheller.interpreter.JassParser.BooleanGreaterOrEqualsExpressionContext;
import com.etheller.interpreter.JassParser.BooleanLessExpressionContext;
import com.etheller.interpreter.JassParser.BooleanLessOrEqualsExpressionContext;
import com.etheller.interpreter.JassParser.BooleanOrExpressionContext;
import com.etheller.interpreter.JassParser.DivisionExpressionContext;
import com.etheller.interpreter.JassParser.DollarHexIntegerLiteralExpressionContext;
import com.etheller.interpreter.JassParser.EqualsExpressionContext;
import com.etheller.interpreter.JassParser.FalseExpressionContext;
import com.etheller.interpreter.JassParser.FunctionCallExpressionContext;
import com.etheller.interpreter.JassParser.FunctionReferenceExpressionContext;
import com.etheller.interpreter.JassParser.HexIntegerLiteralExpressionContext;
import com.etheller.interpreter.JassParser.IntegerLiteralExpressionContext;
import com.etheller.interpreter.JassParser.MultiplicationExpressionContext;
import com.etheller.interpreter.JassParser.NegateExpressionContext;
import com.etheller.interpreter.JassParser.NotEqualsExpressionContext;
import com.etheller.interpreter.JassParser.NotExpressionContext;
import com.etheller.interpreter.JassParser.NullExpressionContext;
import com.etheller.interpreter.JassParser.ParentheticalExpressionContext;
import com.etheller.interpreter.JassParser.RawcodeLiteralExpressionContext;
import com.etheller.interpreter.JassParser.RealLiteralExpressionContext;
import com.etheller.interpreter.JassParser.ReferenceExpressionContext;
import com.etheller.interpreter.JassParser.StringLiteralExpressionContext;
import com.etheller.interpreter.JassParser.SubtrationExpressionContext;
import com.etheller.interpreter.JassParser.TrueExpressionContext;
import com.etheller.interpreter.ast.expression.ArithmeticJassExpression;
import com.etheller.interpreter.ast.expression.ArithmeticSigns;
import com.etheller.interpreter.ast.expression.ArrayRefJassExpression;
import com.etheller.interpreter.ast.expression.FunctionCallJassExpression;
import com.etheller.interpreter.ast.expression.FunctionReferenceJassExpression;
import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.expression.LiteralJassExpression;
import com.etheller.interpreter.ast.expression.NegateJassExpression;
import com.etheller.interpreter.ast.expression.NotJassExpression;
import com.etheller.interpreter.ast.expression.ReferenceJassExpression;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.warsmash.util.RawcodeUtils;

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
	public JassExpression visitHexIntegerLiteralExpression(final HexIntegerLiteralExpressionContext ctx) {
		return new LiteralJassExpression(
				new IntegerJassValue(Integer.parseInt(ctx.HEX_CONSTANT().getText().substring(2), 16)));
	}

	@Override
	public JassExpression visitDollarHexIntegerLiteralExpression(final DollarHexIntegerLiteralExpressionContext ctx) {
		return new LiteralJassExpression(
				new IntegerJassValue(Integer.parseInt(ctx.DOLLAR_HEX_CONSTANT().getText().substring(1), 16)));
	}

	@Override
	public JassExpression visitRawcodeLiteralExpression(final RawcodeLiteralExpressionContext ctx) {
		final String stringLiteralText = ctx.RAWCODE().getText();
		String parsedString = stringLiteralText.substring(1, stringLiteralText.length() - 1).replace("\\\\", "\\");
		while (parsedString.length() < 4) {
			parsedString += '\0';
		}
		return new LiteralJassExpression(new IntegerJassValue(RawcodeUtils.toInt(parsedString)));
	}

	@Override
	public JassExpression visitRealLiteralExpression(final RealLiteralExpressionContext ctx) {
		return new LiteralJassExpression(new RealJassValue(Double.parseDouble(ctx.REAL().getText())));
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
	public JassExpression visitNullExpression(final NullExpressionContext ctx) {
		return new LiteralJassExpression(null);
	}

	@Override
	public JassExpression visitEqualsExpression(final EqualsExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.boolEqualityExpression()), visit(ctx.boolComparisonExpression()),
				ArithmeticSigns.EQUALS);
	}

	@Override
	public JassExpression visitNotEqualsExpression(final NotEqualsExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.boolEqualityExpression()), visit(ctx.boolComparisonExpression()),
				ArithmeticSigns.NOT_EQUALS);
	}

	@Override
	public JassExpression visitNotExpression(final NotExpressionContext ctx) {
		return new NotJassExpression(visit(ctx.baseExpression()));
	}

	@Override
	public JassExpression visitNegateExpression(final NegateExpressionContext ctx) {
		return new NegateJassExpression(visit(ctx.baseExpression()));
	}

	@Override
	public JassExpression visitAdditionExpression(final AdditionExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.simpleArithmeticExpression()), visit(ctx.multDivExpression()),
				ArithmeticSigns.ADD);
	}

	@Override
	public JassExpression visitSubtrationExpression(final SubtrationExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.simpleArithmeticExpression()), visit(ctx.multDivExpression()),
				ArithmeticSigns.SUBTRACT);
	}

	@Override
	public JassExpression visitBooleanOrExpression(final BooleanOrExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.boolExpression()), visit(ctx.boolAndsExpression()),
				ArithmeticSigns.OR);
	}

	@Override
	public JassExpression visitBooleanAndExpression(final BooleanAndExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.boolAndsExpression()), visit(ctx.boolEqualityExpression()),
				ArithmeticSigns.AND);
	}

	@Override
	public JassExpression visitBooleanLessExpression(final BooleanLessExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.boolComparisonExpression()),
				visit(ctx.simpleArithmeticExpression()), ArithmeticSigns.LESS);
	}

	@Override
	public JassExpression visitBooleanGreaterExpression(final BooleanGreaterExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.boolComparisonExpression()),
				visit(ctx.simpleArithmeticExpression()), ArithmeticSigns.GREATER);
	}

	@Override
	public JassExpression visitBooleanGreaterOrEqualsExpression(final BooleanGreaterOrEqualsExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.boolComparisonExpression()),
				visit(ctx.simpleArithmeticExpression()), ArithmeticSigns.GREATER_OR_EQUALS);
	}

	@Override
	public JassExpression visitBooleanLessOrEqualsExpression(final BooleanLessOrEqualsExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.boolComparisonExpression()),
				visit(ctx.simpleArithmeticExpression()), ArithmeticSigns.LESS_OR_EQUALS);
	}

	@Override
	public JassExpression visitMultiplicationExpression(final MultiplicationExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.multDivExpression()), visit(ctx.baseExpression()),
				ArithmeticSigns.MULTIPLY);
	}

	@Override
	public JassExpression visitDivisionExpression(final DivisionExpressionContext ctx) {
		return new ArithmeticJassExpression(visit(ctx.multDivExpression()), visit(ctx.baseExpression()),
				ArithmeticSigns.DIVIDE);
	}

	@Override
	public JassExpression visitFunctionCallExpression(final FunctionCallExpressionContext ctx) {
		final ArgsListContext argsList = ctx.functionExpression().argsList();
		final List<JassExpression> arguments = argsList == null ? Collections.<JassExpression>emptyList()
				: this.argumentExpressionHandler.argumentsVisitor.visit(argsList);
		return new FunctionCallJassExpression(ctx.functionExpression().ID().getText(), arguments);
	}
}

package com.etheller.interpreter.ast.visitors;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.ListParameterContext;
import com.etheller.interpreter.JassParser.NothingParameterContext;
import com.etheller.interpreter.JassParser.SingleParameterContext;
import com.etheller.interpreter.ast.function.JassParameter;

public class JassParametersVisitor extends JassBaseVisitor<List<JassParameter>> {
	private final JassTypeVisitor typeVisitor;

	public JassParametersVisitor(final JassTypeVisitor typeVisitor) {
		this.typeVisitor = typeVisitor;
	}

	@Override
	public List<JassParameter> visitSingleParameter(final SingleParameterContext ctx) {
		final List<JassParameter> list = new LinkedList<>();
		list.add(new JassParameter(typeVisitor.visit(ctx.param().type()), ctx.param().ID().getText()));
		return list;
	}

	@Override
	public List<JassParameter> visitListParameter(final ListParameterContext ctx) {
		final List<JassParameter> list = visit(ctx.paramList());
		list.add(0, new JassParameter(typeVisitor.visit(ctx.param().type()), ctx.param().ID().getText()));
		return list;
	}

	@Override
	public List<JassParameter> visitNothingParameter(final NothingParameterContext ctx) {
		return Collections.EMPTY_LIST;
	}
}

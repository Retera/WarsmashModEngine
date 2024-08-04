package com.etheller.interpreter.ast.visitors;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.ListParameterContext;
import com.etheller.interpreter.JassParser.NothingParameterContext;
import com.etheller.interpreter.JassParser.SingleParameterContext;
import com.etheller.interpreter.ast.definition.JassParameterDefinition;
import com.etheller.interpreter.ast.scope.GlobalScope;

public class JassParametersVisitor extends JassBaseVisitor<List<JassParameterDefinition>> {
	private final JassTypeVisitor typeVisitor;
	private final GlobalScope globalScope;

	public JassParametersVisitor(final JassTypeVisitor typeVisitor, final GlobalScope globalScope) {
		this.typeVisitor = typeVisitor;
		this.globalScope = globalScope;
	}

	@Override
	public List<JassParameterDefinition> visitSingleParameter(final SingleParameterContext ctx) {
		final List<JassParameterDefinition> list = new LinkedList<>();
		list.add(new JassParameterDefinition(this.typeVisitor.visit(ctx.param().type()), ctx.param().ID().getText()));
		return list;
	}

	@Override
	public List<JassParameterDefinition> visitListParameter(final ListParameterContext ctx) {
		final List<JassParameterDefinition> list = visit(ctx.paramList());
		list.add(0,
				new JassParameterDefinition(this.typeVisitor.visit(ctx.param().type()), ctx.param().ID().getText()));
		return list;
	}

	@Override
	public List<JassParameterDefinition> visitNothingParameter(final NothingParameterContext ctx) {
		return Collections.EMPTY_LIST;
	}
}

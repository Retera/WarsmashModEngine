package com.etheller.interpreter.ast.visitors;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.ArrayTypeContext;
import com.etheller.interpreter.JassParser.BasicTypeContext;
import com.etheller.interpreter.JassParser.NothingTypeContext;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.value.JassType;

public class JassTypeVisitor extends JassBaseVisitor<JassType> {
	private final GlobalScope globals;

	public JassTypeVisitor(final GlobalScope globals) {
		this.globals = globals;
	}

	@Override
	public JassType visitArrayType(final ArrayTypeContext ctx) {
		return globals.parseArrayType(ctx.ID().getText());
	}

	@Override
	public JassType visitBasicType(final BasicTypeContext ctx) {
		return globals.parseType(ctx.ID().getText());
	}

	@Override
	public JassType visitNothingType(final NothingTypeContext ctx) {
		return JassType.NOTHING;
	}
}

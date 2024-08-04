package com.etheller.interpreter.ast.visitors;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.ArrayTypeContext;
import com.etheller.interpreter.JassParser.BasicTypeContext;
import com.etheller.interpreter.JassParser.NothingTypeContext;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.type.ArrayJassTypeToken;
import com.etheller.interpreter.ast.type.JassTypeToken;
import com.etheller.interpreter.ast.type.NothingJassTypeToken;
import com.etheller.interpreter.ast.type.PrimitiveJassTypeToken;

public class JassTypeVisitor extends JassBaseVisitor<JassTypeToken> {
	private final GlobalScope globals;

	public JassTypeVisitor(final GlobalScope globals) {
		this.globals = globals;
	}

	@Override
	public JassTypeToken visitArrayType(final ArrayTypeContext ctx) {
		return new ArrayJassTypeToken(ctx.ID().getText());
	}

	@Override
	public JassTypeToken visitBasicType(final BasicTypeContext ctx) {
		return new PrimitiveJassTypeToken(ctx.ID().getText());
	}

	@Override
	public JassTypeToken visitNothingType(final NothingTypeContext ctx) {
		return NothingJassTypeToken.INSTANCE;
	}
}

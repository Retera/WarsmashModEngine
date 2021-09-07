package com.etheller.interpreter.ast.visitors;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.BasicGlobalContext;
import com.etheller.interpreter.JassParser.DefinitionGlobalContext;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.visitor.ArrayPrimitiveTypeVisitor;

public class JassGlobalsVisitor extends JassBaseVisitor<Void> {
	private static final LocalScope EMPTY_LOCAL_SCOPE = new LocalScope();
	private final GlobalScope globals;
	private final JassTypeVisitor jassTypeVisitor;
	private final JassExpressionVisitor jassExpressionVisitor;

	public JassGlobalsVisitor(final GlobalScope globals, final JassTypeVisitor jassTypeVisitor,
			final JassExpressionVisitor jassExpressionVisitor) {
		this.globals = globals;
		this.jassTypeVisitor = jassTypeVisitor;
		this.jassExpressionVisitor = jassExpressionVisitor;
	}

	@Override
	public Void visitBasicGlobal(final BasicGlobalContext ctx) {
		final JassType type = this.jassTypeVisitor.visit(ctx.type());
		final JassType arrayPrimType = type.visit(ArrayPrimitiveTypeVisitor.getInstance());
		if (arrayPrimType != null) {
			this.globals.createGlobalArray(ctx.ID().getText(), type);
		}
		else {
			this.globals.createGlobal(ctx.ID().getText(), type);
		}
		return null;
	}

	@Override
	public Void visitDefinitionGlobal(final DefinitionGlobalContext ctx) {
		final JassType type = this.jassTypeVisitor.visit(ctx.type());
		final JassType arrayPrimType = type.visit(ArrayPrimitiveTypeVisitor.getInstance());
		try {
			if (arrayPrimType != null) {
				this.globals.createGlobalArray(ctx.ID().getText(), type);
			}
			else {
				this.globals.createGlobal(ctx.ID().getText(), type,
						this.jassExpressionVisitor.visit(ctx.assignTail().expression()).evaluate(this.globals,
								EMPTY_LOCAL_SCOPE, JassProgramVisitor.EMPTY_TRIGGER_SCOPE));
			}
		}
		catch (final Exception exc) {
			throw new RuntimeException(ctx.getText(), exc);
		}
		return null;
	}
}

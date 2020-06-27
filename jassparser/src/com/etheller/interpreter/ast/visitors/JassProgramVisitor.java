package com.etheller.interpreter.ast.visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.BlockContext;
import com.etheller.interpreter.JassParser.FunctionBlockContext;
import com.etheller.interpreter.JassParser.GlobalContext;
import com.etheller.interpreter.JassParser.ProgramContext;
import com.etheller.interpreter.JassParser.StatementContext;
import com.etheller.interpreter.JassParser.TypeDefinitionContext;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.function.JassNativeManager;
import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.statement.JassStatement;

public class JassProgramVisitor extends JassBaseVisitor<Void> {
	private final GlobalScope globals = new GlobalScope();
	private final JassNativeManager jassNativeManager = new JassNativeManager();
	private final JassTypeVisitor jassTypeVisitor = new JassTypeVisitor(this.globals);
	private final ArgumentExpressionHandler argumentExpressionHandler = new ArgumentExpressionHandler();
	private final JassExpressionVisitor jassExpressionVisitor = new JassExpressionVisitor(
			this.argumentExpressionHandler);
	private final JassArgumentsVisitor jassArgumentsVisitor = new JassArgumentsVisitor(this.argumentExpressionHandler);
	{
		this.argumentExpressionHandler.setJassArgumentsVisitor(this.jassArgumentsVisitor);
		this.argumentExpressionHandler.setJassExpressionVisitor(this.jassExpressionVisitor);
	}
	private final JassGlobalsVisitor jassGlobalsVisitor = new JassGlobalsVisitor(this.globals, this.jassTypeVisitor,
			this.jassExpressionVisitor);
	private final JassParametersVisitor jassParametersVisitor = new JassParametersVisitor(this.jassTypeVisitor);
	private final JassStatementVisitor jassStatementVisitor = new JassStatementVisitor(this.argumentExpressionHandler);

	@Override
	public Void visitBlock(final BlockContext ctx) {
		if (ctx.globalsBlock() != null) {
			for (final GlobalContext globalContext : ctx.globalsBlock().global()) {
				this.jassGlobalsVisitor.visit(globalContext);
			}
		}
		else if (ctx.nativeBlock() != null) {
			final String text = ctx.nativeBlock().ID().getText();
			System.out.println("Registering native: " + text);
			this.jassNativeManager.registerNativeCode(text,
					this.jassParametersVisitor.visit(ctx.nativeBlock().paramList()),
					this.jassTypeVisitor.visit(ctx.nativeBlock().type()), this.globals);
		}
		return null;
	}

	@Override
	public Void visitFunctionBlock(final FunctionBlockContext ctx) {
		final List<JassStatement> statements = new ArrayList<>();
		for (final StatementContext statementContext : ctx.statements().statement()) {
			statements.add(this.jassStatementVisitor.visit(statementContext));
		}
		final UserJassFunction userJassFunction = new UserJassFunction(statements,
				this.jassParametersVisitor.visit(ctx.paramList()), this.jassTypeVisitor.visit(ctx.type()));
		this.globals.defineFunction(ctx.ID().getText(), userJassFunction);
		return null;
	}

	@Override
	public Void visitProgram(final ProgramContext ctx) {
		for (final TypeDefinitionContext typeDefinitionContext : ctx.typeDefinitionBlock().typeDefinition()) {
			this.globals.loadTypeDefinition(typeDefinitionContext.ID(0).getText(),
					typeDefinitionContext.ID(1).getText());
		}
		for (final BlockContext blockContext : ctx.block()) {
			visit(blockContext);
		}
		for (final FunctionBlockContext functionBlockContext : ctx.functionBlock()) {
			final List<JassStatement> statements = new ArrayList<>();
			for (final StatementContext statementContext : functionBlockContext.statements().statement()) {
				statements.add(this.jassStatementVisitor.visit(statementContext));
			}
			final UserJassFunction userJassFunction = new UserJassFunction(statements,
					this.jassParametersVisitor.visit(functionBlockContext.paramList()),
					this.jassTypeVisitor.visit(functionBlockContext.type()));
			this.globals.defineFunction(functionBlockContext.ID().getText(), userJassFunction);
		}
		final JassFunction mainFunction = this.globals.getFunctionByName("main");
		if (mainFunction != null) {
			try {
				mainFunction.call(Collections.EMPTY_LIST, this.globals);
			}
			catch (final Exception exc) {
				throw new RuntimeException("Exception on Line " + GlobalScope.getLineNumber(), exc);
			}
		}
		return null;
	}

	public GlobalScope getGlobals() {
		return this.globals;
	}

	public JassNativeManager getJassNativeManager() {
		return this.jassNativeManager;
	}
}

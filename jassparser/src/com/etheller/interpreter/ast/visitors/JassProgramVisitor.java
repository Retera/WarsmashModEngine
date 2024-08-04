package com.etheller.interpreter.ast.visitors;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.JassBaseVisitor;
import com.etheller.interpreter.JassParser.BlockContext;
import com.etheller.interpreter.JassParser.FunctionBlockContext;
import com.etheller.interpreter.JassParser.GlobalContext;
import com.etheller.interpreter.JassParser.ProgramContext;
import com.etheller.interpreter.JassParser.StatementContext;
import com.etheller.interpreter.JassParser.TypeDeclarationContext;
import com.etheller.interpreter.ast.definition.JassParameterDefinition;
import com.etheller.interpreter.ast.function.JassNativeManager;
import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.util.JassSettings;

public class JassProgramVisitor extends JassBaseVisitor<Void> {
	public static final TriggerExecutionScope EMPTY_TRIGGER_SCOPE = new TriggerExecutionScope(null);
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
	private final JassParametersVisitor jassParametersVisitor = new JassParametersVisitor(this.jassTypeVisitor,
			this.globals);
	private final JassStatementVisitor jassStatementVisitor = new JassStatementVisitor(this.argumentExpressionHandler,
			this.jassTypeVisitor);
	private String jassFileName;

	@Override
	public Void visitBlock(final BlockContext ctx) {
		if (ctx.globalsBlock() != null) {
			for (final GlobalContext globalContext : ctx.globalsBlock().global()) {
				this.jassGlobalsVisitor.visit(globalContext);
			}
		}
		else if (ctx.nativeBlock() != null) {
			final String text = ctx.nativeBlock().ID().getText();
			if (JassSettings.LOG_FUNCTION_DEFINITIONS) {
				System.out.println("Registering native: " + text);
			}
			this.jassNativeManager.registerNativeCode(ctx.getStart().getLine(), this.jassFileName, text,
					this.jassParametersVisitor.visit(ctx.nativeBlock().paramList()),
					this.jassTypeVisitor.visit(ctx.nativeBlock().type()), this.globals);
		}
		else if (ctx.functionBlock() != null) {
			visit(ctx.functionBlock());
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
				JassParameterDefinition.resolve(this.jassParametersVisitor.visit(ctx.paramList()), this.globals),
				this.jassTypeVisitor.visit(ctx.type()).resolve(this.globals));
		this.globals.defineFunction(ctx.getStart().getLine(), this.jassFileName, ctx.ID().getText(), userJassFunction,
				"");
		if (JassSettings.LOG_FUNCTION_DEFINITIONS) {
			System.out.println("Defining jass user function: " + ctx.ID().getText());
		}
		return null;
	}

	@Override
	public Void visitProgram(final ProgramContext ctx) {
		for (final TypeDeclarationContext typeDefinitionContext : ctx.typeDeclarationBlock().typeDeclaration()) {
			this.globals.loadTypeDefinition(typeDefinitionContext.ID(0).getText(),
					typeDefinitionContext.ID(1).getText());
		}
		for (final BlockContext blockContext : ctx.block()) {
			visit(blockContext);
		}
		return null;
	}

	public GlobalScope getGlobals() {
		return this.globals;
	}

	public JassNativeManager getJassNativeManager() {
		return this.jassNativeManager;
	}

	public void setCurrentFileName(final String jassFile) {
		this.jassFileName = jassFile;
		this.jassStatementVisitor.setCurrentFileName(jassFile);
	}
}

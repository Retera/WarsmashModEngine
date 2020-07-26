// Generated from Jass.g4 by ANTLR 4.7

	package com.etheller.interpreter;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JassParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface JassVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link JassParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(JassParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#typeDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDefinition(JassParser.TypeDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BasicType}
	 * labeled alternative in {@link JassParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasicType(JassParser.BasicTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayType}
	 * labeled alternative in {@link JassParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayType(JassParser.ArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NothingType}
	 * labeled alternative in {@link JassParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNothingType(JassParser.NothingTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BasicGlobal}
	 * labeled alternative in {@link JassParser#global}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasicGlobal(JassParser.BasicGlobalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DefinitionGlobal}
	 * labeled alternative in {@link JassParser#global}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefinitionGlobal(JassParser.DefinitionGlobalContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#assignTail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignTail(JassParser.AssignTailContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReferenceExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReferenceExpression(JassParser.ReferenceExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringLiteralExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteralExpression(JassParser.StringLiteralExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntegerLiteralExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerLiteralExpression(JassParser.IntegerLiteralExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionReferenceExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionReferenceExpression(JassParser.FunctionReferenceExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NullExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullExpression(JassParser.NullExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TrueExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueExpression(JassParser.TrueExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FalseExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseExpression(JassParser.FalseExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayReferenceExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayReferenceExpression(JassParser.ArrayReferenceExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionCallExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallExpression(JassParser.FunctionCallExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParentheticalExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentheticalExpression(JassParser.ParentheticalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotExpression}
	 * labeled alternative in {@link JassParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpression(JassParser.NotExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#functionExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionExpression(JassParser.FunctionExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SingleArgument}
	 * labeled alternative in {@link JassParser#argsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleArgument(JassParser.SingleArgumentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ListArgument}
	 * labeled alternative in {@link JassParser#argsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListArgument(JassParser.ListArgumentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CallStatement}
	 * labeled alternative in {@link JassParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallStatement(JassParser.CallStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SetStatement}
	 * labeled alternative in {@link JassParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetStatement(JassParser.SetStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayedAssignmentStatement}
	 * labeled alternative in {@link JassParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayedAssignmentStatement(JassParser.ArrayedAssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnStatement}
	 * labeled alternative in {@link JassParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(JassParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link JassParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(JassParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SimpleIfStatement}
	 * labeled alternative in {@link JassParser#ifStatementPartial}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleIfStatement(JassParser.SimpleIfStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfElseStatement}
	 * labeled alternative in {@link JassParser#ifStatementPartial}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfElseStatement(JassParser.IfElseStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfElseIfStatement}
	 * labeled alternative in {@link JassParser#ifStatementPartial}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfElseIfStatement(JassParser.IfElseIfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(JassParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SingleParameter}
	 * labeled alternative in {@link JassParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleParameter(JassParser.SingleParameterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ListParameter}
	 * labeled alternative in {@link JassParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListParameter(JassParser.ListParameterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NothingParameter}
	 * labeled alternative in {@link JassParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNothingParameter(JassParser.NothingParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#globalsBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobalsBlock(JassParser.GlobalsBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#typeDefinitionBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDefinitionBlock(JassParser.TypeDefinitionBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#nativeBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNativeBlock(JassParser.NativeBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(JassParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#functionBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionBlock(JassParser.FunctionBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#statements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatements(JassParser.StatementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#newlines}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewlines(JassParser.NewlinesContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#newlines_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewlines_opt(JassParser.Newlines_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link JassParser#pnewlines}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPnewlines(JassParser.PnewlinesContext ctx);
}
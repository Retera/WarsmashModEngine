package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.debug.DebuggingJassStatement;

public interface JassStatementVisitor<TYPE> {
	TYPE visit(JassArrayedAssignmentStatement statement);

	TYPE visit(JassCallExpressionStatement statement);

	TYPE visit(JassCallStatement statement);

	TYPE visit(JassDoNothingStatement statement);

	TYPE visit(JassExitWhenStatement statement);

	TYPE visit(JassIfElseIfStatement statement);

	TYPE visit(JassIfElseStatement statement);

	TYPE visit(JassIfStatement statement);

	TYPE visit(JassLocalDefinitionStatement statement);

	TYPE visit(JassLocalStatement statement);

	TYPE visit(JassLoopStatement statement);

	TYPE visit(JassReturnNothingStatement statement);

	TYPE visit(JassReturnStatement statement);

	TYPE visit(JassSetStatement statement);

	TYPE visit(JassSetMemberStatement statement);

	TYPE visit(DebuggingJassStatement statement);

	TYPE visit(JassGlobalDefinitionStatement statement);

	TYPE visit(JassGlobalStatement statement);

	TYPE visit(JassThrowStatement statement);
}
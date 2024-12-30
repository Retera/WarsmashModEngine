package com.etheller.interpreter.ast.execution.instruction;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.debug.DebuggingJassStatement;
import com.etheller.interpreter.ast.statement.JassArrayedAssignmentStatement;
import com.etheller.interpreter.ast.statement.JassCallExpressionStatement;
import com.etheller.interpreter.ast.statement.JassCallStatement;
import com.etheller.interpreter.ast.statement.JassDoNothingStatement;
import com.etheller.interpreter.ast.statement.JassExitWhenStatement;
import com.etheller.interpreter.ast.statement.JassGlobalDefinitionStatement;
import com.etheller.interpreter.ast.statement.JassGlobalStatement;
import com.etheller.interpreter.ast.statement.JassIfElseIfStatement;
import com.etheller.interpreter.ast.statement.JassIfElseStatement;
import com.etheller.interpreter.ast.statement.JassIfStatement;
import com.etheller.interpreter.ast.statement.JassLocalDefinitionStatement;
import com.etheller.interpreter.ast.statement.JassLocalStatement;
import com.etheller.interpreter.ast.statement.JassLoopStatement;
import com.etheller.interpreter.ast.statement.JassReturnNothingStatement;
import com.etheller.interpreter.ast.statement.JassReturnStatement;
import com.etheller.interpreter.ast.statement.JassSetMemberStatement;
import com.etheller.interpreter.ast.statement.JassSetStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.statement.JassStatementVisitor;
import com.etheller.interpreter.ast.statement.JassThrowStatement;

public class LocalExtractingJassStatementVisitor implements JassStatementVisitor<JassStatement> {
	private final List<JassStatement> extractedLocals = new ArrayList<>();

	@Override
	public JassStatement visit(JassArrayedAssignmentStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(JassCallExpressionStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(JassCallStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(JassDoNothingStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(JassExitWhenStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(JassIfElseIfStatement statement) {
		final JassStatement newElseIfTail = statement.getElseifTail().accept(this);
		final List<JassStatement> newThenStatements = visitAll(statement.getThenStatements());
		if (newElseIfTail == null) {
			return new JassIfStatement(statement.getCondition(), newThenStatements);
		}
		return new JassIfElseIfStatement(statement.getCondition(), newThenStatements, newElseIfTail);
	}

	public List<JassStatement> visitAll(List<JassStatement> thenStatements) {
		final List<JassStatement> newStatements = new ArrayList<>();
		for (final JassStatement statement : thenStatements) {
			final JassStatement newStatement = statement.accept(this);
			if (newStatement != null) {
				newStatements.add(newStatement);
			}
		}
		return newStatements;
	}

	@Override
	public JassStatement visit(JassIfElseStatement statement) {
		return new JassIfElseStatement(statement.getCondition(), visitAll(statement.getThenStatements()),
				visitAll(statement.getElseStatements()));
	}

	@Override
	public JassStatement visit(JassIfStatement statement) {
		return new JassIfStatement(statement.getCondition(), visitAll(statement.getThenStatements()));
	}

	@Override
	public JassStatement visit(JassLocalDefinitionStatement statement) {
		this.extractedLocals.add(new JassLocalStatement(statement.getIdentifier(), statement.getType()));
		return new JassSetStatement(statement.getIdentifier(), statement.getExpression());
	}

	@Override
	public JassStatement visit(JassLocalStatement statement) {
		this.extractedLocals.add(statement);
		return null;
	}

	@Override
	public JassStatement visit(JassLoopStatement statement) {
		return new JassLoopStatement(visitAll(statement.getStatements()));
	}

	@Override
	public JassStatement visit(JassReturnNothingStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(JassReturnStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(JassSetStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(JassSetMemberStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(DebuggingJassStatement statement) {
		final JassStatement newStatement = statement.getDelegate().accept(this);
		return new DebuggingJassStatement(statement.getLineNo(), newStatement);
	}

	@Override
	public JassStatement visit(JassGlobalDefinitionStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(JassGlobalStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(JassThrowStatement statement) {
		return statement;
	}

	public List<JassStatement> getExtractedLocals() {
		return this.extractedLocals;
	}
}

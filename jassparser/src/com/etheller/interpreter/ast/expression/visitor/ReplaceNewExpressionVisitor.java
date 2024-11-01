package com.etheller.interpreter.ast.expression.visitor;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.debug.DebuggingJassStatement;
import com.etheller.interpreter.ast.expression.AllocateAsNewTypeExpression;
import com.etheller.interpreter.ast.expression.ArithmeticJassExpression;
import com.etheller.interpreter.ast.expression.ArrayRefJassExpression;
import com.etheller.interpreter.ast.expression.ExtendHandleExpression;
import com.etheller.interpreter.ast.expression.FunctionCallJassExpression;
import com.etheller.interpreter.ast.expression.FunctionReferenceJassExpression;
import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.expression.JassExpressionVisitor;
import com.etheller.interpreter.ast.expression.JassNewExpression;
import com.etheller.interpreter.ast.expression.LiteralJassExpression;
import com.etheller.interpreter.ast.expression.MemberJassExpression;
import com.etheller.interpreter.ast.expression.MethodCallJassExpression;
import com.etheller.interpreter.ast.expression.MethodReferenceJassExpression;
import com.etheller.interpreter.ast.expression.NegateJassExpression;
import com.etheller.interpreter.ast.expression.NotJassExpression;
import com.etheller.interpreter.ast.expression.ParentlessMethodCallJassExpression;
import com.etheller.interpreter.ast.expression.ReferenceJassExpression;
import com.etheller.interpreter.ast.expression.TypeCastJassExpression;
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
import com.etheller.interpreter.ast.value.StructJassType;

public class ReplaceNewExpressionVisitor
		implements JassExpressionVisitor<JassExpression>, JassStatementVisitor<JassStatement> {
	private final StructJassType newNewType;

	public ReplaceNewExpressionVisitor(final StructJassType newNewType) {
		this.newNewType = newNewType;
	}

	@Override
	public JassExpression visit(final ArithmeticJassExpression expression) {
		final JassExpression left = expression.getLeftExpression();
		final JassExpression newLeft = left.accept(this);
		final JassExpression right = expression.getRightExpression();
		final JassExpression newRight = right.accept(this);
		if ((left != newLeft) || (right != newRight)) {
			return new ArithmeticJassExpression(newLeft, newRight, expression.getArithmeticSign());
		}
		return expression;
	}

	@Override
	public JassExpression visit(final ArrayRefJassExpression expression) {
		final JassExpression indexExpression = expression.getIndexExpression();
		final JassExpression newIndexExpression = indexExpression.accept(this);
		if (indexExpression != newIndexExpression) {
			return new ArrayRefJassExpression(expression.getIdentifier(), newIndexExpression);
		}
		return expression;
	}

	@Override
	public JassExpression visit(final FunctionCallJassExpression expression) {
		final List<JassExpression> arguments = new ArrayList<>();
		boolean anyChanges = false;
		for (final JassExpression argument : expression.getArguments()) {
			final JassExpression newArgument = argument.accept(this);
			if (newArgument != argument) {
				anyChanges = true;
			}
			arguments.add(newArgument);
		}
		if (!anyChanges) {
			return expression;
		}
		return new FunctionCallJassExpression(expression.getFunctionName(), arguments);
	}

	@Override
	public JassExpression visit(final MethodCallJassExpression expression) {
		final JassExpression structExpression = expression.getStructExpression();
		final JassExpression newStructExpression = structExpression.accept(this);
		final List<JassExpression> arguments = new ArrayList<>();
		boolean anyArgumentChanges = false;
		for (final JassExpression argument : expression.getArguments()) {
			final JassExpression newArgument = argument.accept(this);
			if (newArgument != argument) {
				anyArgumentChanges = true;
			}
			arguments.add(newArgument);
		}
		if (!anyArgumentChanges && (newStructExpression == structExpression)) {
			return expression;
		}
		return new MethodCallJassExpression(newStructExpression, expression.getFunctionName(), arguments);
	}

	@Override
	public JassExpression visit(final ParentlessMethodCallJassExpression expression) {
		final List<JassExpression> arguments = new ArrayList<>();
		boolean anyArgumentChanges = false;
		for (final JassExpression argument : expression.getArguments()) {
			final JassExpression newArgument = argument.accept(this);
			if (newArgument != argument) {
				anyArgumentChanges = true;
			}
			arguments.add(newArgument);
		}
		if (!anyArgumentChanges) {
			return expression;
		}
		return new ParentlessMethodCallJassExpression(expression.getFunctionName(), arguments);
	}

	@Override
	public JassExpression visit(final FunctionReferenceJassExpression expression) {
		return expression;
	}

	@Override
	public JassExpression visit(final MethodReferenceJassExpression expression) {
		final JassExpression structExpression = expression.getStructExpression();
		final JassExpression newStructExpression = structExpression.accept(this);
		if (newStructExpression != structExpression) {
			return new MethodReferenceJassExpression(newStructExpression, expression.getIdentifier());
		}
		return expression;
	}

	@Override
	public JassExpression visit(final LiteralJassExpression expression) {
		return expression;
	}

	@Override
	public JassExpression visit(final NegateJassExpression expression) {
		final JassExpression negatedExpression = expression.getExpression();
		final JassExpression newNegatedExpression = negatedExpression.accept(this);
		if (negatedExpression != newNegatedExpression) {
			return new NegateJassExpression(newNegatedExpression);
		}
		return expression;
	}

	@Override
	public JassExpression visit(final NotJassExpression expression) {
		final JassExpression negatedExpression = expression.getExpression();
		final JassExpression newNegatedExpression = negatedExpression.accept(this);
		if (negatedExpression != newNegatedExpression) {
			return new NotJassExpression(newNegatedExpression);
		}
		return expression;
	}

	@Override
	public JassExpression visit(final ReferenceJassExpression expression) {
		return expression;
	}

	@Override
	public JassExpression visit(final MemberJassExpression expression) {
		final JassExpression structExpression = expression.getStructExpression();
		final JassExpression newStructExpression = structExpression.accept(this);
		if (structExpression != newStructExpression) {
			return new MemberJassExpression(newStructExpression, expression.getIdentifier());
		}
		return expression;
	}

	@Override
	public JassExpression visit(final JassNewExpression expression) {
		return new JassNewExpression(this.newNewType);
	}

	@Override
	public JassExpression visit(final AllocateAsNewTypeExpression expression) {
		return expression;
	}

	@Override
	public JassExpression visit(final ExtendHandleExpression expression) {
		return expression;
	}

	@Override
	public JassStatement visit(final JassArrayedAssignmentStatement statement) {
		final JassExpression indexExpression = statement.getIndexExpression();
		final JassExpression valueExpression = statement.getExpression();
		final JassExpression newIndexExpression = indexExpression.accept(this);
		final JassExpression newValueExpression = valueExpression.accept(this);
		if ((newIndexExpression != indexExpression) || (newValueExpression != valueExpression)) {
			return new JassArrayedAssignmentStatement(statement.getIdentifier(), newIndexExpression,
					newValueExpression);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassCallExpressionStatement statement) {
		final JassExpression expression = statement.getExpression();
		final JassExpression newExpression = expression.accept(this);
		if (newExpression != expression) {
			return new JassCallExpressionStatement(newExpression);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassCallStatement statement) {
		final List<JassExpression> arguments = new ArrayList<>();
		boolean anyChanges = false;
		for (final JassExpression argument : statement.getArguments()) {
			final JassExpression newArgument = argument.accept(this);
			if (newArgument != argument) {
				anyChanges = true;
			}
			arguments.add(newArgument);
		}
		if (!anyChanges) {
			return statement;
		}
		return new JassCallStatement(statement.getFunctionName(), arguments);
	}

	@Override
	public JassStatement visit(final JassDoNothingStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(final JassExitWhenStatement statement) {
		final JassExpression condition = statement.getExpression();
		final JassExpression newCondition = condition.accept(this);
		if (condition != newCondition) {
			return new JassExitWhenStatement(newCondition);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassIfElseIfStatement statement) {
		final JassExpression condition = statement.getCondition();
		final JassExpression newCondition = condition.accept(this);
		final List<JassStatement> thenStatements = statement.getThenStatements();
		final List<JassStatement> newThenStatements = acceptAll(thenStatements);
		final JassStatement elseifTail = statement.getElseifTail();
		final JassStatement newElseifTail = elseifTail.accept(this);
		if ((condition != newCondition) || (thenStatements != newThenStatements) || (elseifTail != newElseifTail)) {
			return new JassIfElseIfStatement(newCondition, newThenStatements, newElseifTail);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassIfElseStatement statement) {
		final JassExpression condition = statement.getCondition();
		final JassExpression newCondition = condition.accept(this);
		final List<JassStatement> thenStatements = statement.getThenStatements();
		final List<JassStatement> newThenStatements = acceptAll(thenStatements);
		final List<JassStatement> elseStatements = statement.getElseStatements();
		final List<JassStatement> newElseStatements = acceptAll(elseStatements);
		if ((condition != newCondition) || (thenStatements != newThenStatements)
				|| (elseStatements != newElseStatements)) {
			return new JassIfElseStatement(newCondition, newThenStatements, newElseStatements);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassIfStatement statement) {
		final JassExpression condition = statement.getCondition();
		final JassExpression newCondition = condition.accept(this);
		final List<JassStatement> thenStatements = statement.getThenStatements();
		final List<JassStatement> newThenStatements = acceptAll(thenStatements);
		if ((condition != newCondition) || (thenStatements != newThenStatements)) {
			return new JassIfStatement(newCondition, newThenStatements);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassLocalDefinitionStatement statement) {
		final JassExpression expression = statement.getExpression();
		final JassExpression newExpression = expression.accept(this);
		if (expression != newExpression) {
			return new JassLocalDefinitionStatement(statement.getIdentifier(), statement.getType(), newExpression);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassLocalStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(final JassLoopStatement statement) {
		final List<JassStatement> statements = statement.getStatements();
		final List<JassStatement> newStatements = acceptAll(statements);
		if (newStatements != statements) {
			return new JassLoopStatement(newStatements);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassReturnNothingStatement statement) {
		return statement;
	}

	@Override
	public JassStatement visit(final JassReturnStatement statement) {
		final JassExpression expression = statement.getExpression();
		final JassExpression newExpression = expression.accept(this);
		if (expression != newExpression) {
			return new JassReturnStatement(newExpression);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassSetStatement statement) {
		final JassExpression expression = statement.getExpression();
		final JassExpression newExpression = expression.accept(this);
		if (expression != newExpression) {
			return new JassSetStatement(statement.getIdentifier(), newExpression);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassSetMemberStatement statement) {
		final JassExpression structExpression = statement.getStructExpression();
		final JassExpression newStructExpression = structExpression.accept(this);
		final JassExpression expression = statement.getExpression();
		final JassExpression newExpression = expression.accept(this);
		if ((structExpression != newStructExpression) || (expression != newExpression)) {
			return new JassSetMemberStatement(newStructExpression, statement.getIdentifier(), newExpression);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final DebuggingJassStatement statement) {
		final JassStatement delegate = statement.getDelegate();
		final JassStatement newDelegate = delegate.accept(this);
		if (delegate != newDelegate) {
			return new DebuggingJassStatement(statement.getLineNo(), newDelegate);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassGlobalDefinitionStatement statement) {
		final JassExpression expression = statement.getExpression();
		final JassExpression newExpression = expression.accept(this);
		if (expression != newExpression) {
			return new JassGlobalDefinitionStatement(statement.getQualifiers(), statement.getIdentifier(),
					statement.getType(), newExpression);
		}
		return statement;
	}

	@Override
	public JassStatement visit(final JassGlobalStatement statement) {
		return statement;
	}

	@Override
	public JassExpression visit(final TypeCastJassExpression expression) {
		final JassExpression valueExpression = expression.getValueExpression();
		final JassExpression newValueExpression = valueExpression.accept(this);
		if (valueExpression != newValueExpression) {
			return new TypeCastJassExpression(newValueExpression, expression.getCastToType());
		}
		return expression;
	}

	public List<JassStatement> acceptAll(final List<JassStatement> statements) {
		final List<JassStatement> newStatements = new ArrayList<>();
		boolean anyChanges = false;
		for (final JassStatement statement : statements) {
			final JassStatement newStatement = statement.accept(this);
			if (newStatement != statement) {
				anyChanges = true;
			}
			newStatements.add(newStatement);
		}
		if (!anyChanges) {
			return statements;
		}
		return newStatements;
	}

	@Override
	public JassStatement visit(final JassThrowStatement statement) {
		return statement;
	}

}

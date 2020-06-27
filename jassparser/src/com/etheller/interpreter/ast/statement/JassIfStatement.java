package com.etheller.interpreter.ast.statement;

import java.util.List;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;

public class JassIfStatement implements JassStatement {
	private final int lineNo;
	private final JassExpression condition;
	private final List<JassStatement> thenStatements;

	public JassIfStatement(final int lineNo, final JassExpression condition, final List<JassStatement> thenStatements) {
		this.lineNo = lineNo;
		this.condition = condition;
		this.thenStatements = thenStatements;
	}

	public JassExpression getCondition() {
		return this.condition;
	}

	public List<JassStatement> getThenStatements() {
		return this.thenStatements;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope) {
		globalScope.setLineNumber(this.lineNo);
		if (this.condition.evaluate(globalScope, localScope).visit(BooleanJassValueVisitor.getInstance())) {
			for (final JassStatement statement : this.thenStatements) {
				final JassValue returnValue = statement.execute(globalScope, localScope);
				if (returnValue != null) {
					return returnValue;
				}
			}
		}
		return null;
	}

}

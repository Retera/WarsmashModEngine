package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.ArrayTypeVisitor;

public class JassLocalStatement implements JassStatement {
	private final String identifier;
	private final JassType type;

	public JassLocalStatement(final String identifier, final JassType type) {
		this.identifier = identifier;
		this.type = type;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		final Assignable local = localScope.createLocal(this.identifier, this.type);
		if (this.type == JassType.INTEGER) {
			local.setValue(IntegerJassValue.ZERO);
		}
		else {
			final ArrayJassType arrayType = this.type.visit(ArrayTypeVisitor.getInstance());
			if (arrayType != null) {
				local.setValue(new ArrayJassValue(arrayType));
			}
		}
		return null;
	}

}

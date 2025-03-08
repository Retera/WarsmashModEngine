package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StructJassValue;

public interface ArithmeticSign {
	JassValue apply(BooleanJassValue left, BooleanJassValue right);

	JassValue apply(RealJassValue left, RealJassValue right);

	JassValue apply(RealJassValue left, IntegerJassValue right);

	JassValue apply(IntegerJassValue left, RealJassValue right);

	JassValue apply(IntegerJassValue left, IntegerJassValue right);

	JassValue apply(String left, String right);

	JassValue apply(HandleJassValue left, HandleJassValue right);

	JassValue apply(CodeJassValue left, CodeJassValue right);

	JassValue apply(StructJassValue left, StructJassValue right);

	JassValue apply(StructJassValue left, HandleJassValue right);

	JassValue apply(HandleJassValue left, StructJassValue right);

	JassType predictType(JassType leftType, JassType rightType);

	JassValue getShortCircuitValue();
}

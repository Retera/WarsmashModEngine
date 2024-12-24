package com.etheller.interpreter.ast.value.visitor.cast;

import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.DummyJassValue;
import com.etheller.interpreter.ast.value.HandleJassValue;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.RealJassValue;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.StructJassValue;

public class TypeCastToStringJassValueVisitor implements JassValueVisitor<JassValue> {
	public static final TypeCastToStringJassValueVisitor INSTANCE = new TypeCastToStringJassValueVisitor();

	@Override
	public JassValue accept(final IntegerJassValue value) {
		return JassType.STRING.getNullValue();
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		return JassType.STRING.getNullValue();
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		return JassType.STRING.getNullValue();
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		return value;
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		return JassType.STRING.getNullValue();
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		return JassType.STRING.getNullValue();
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		return JassType.STRING.getNullValue();
	}

	@Override
	public JassValue accept(final DummyJassValue value) {
		return JassType.STRING.getNullValue();
	}

	@Override
	public JassValue accept(final StructJassValue value) {
		return JassType.STRING.getNullValue();
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		return JassType.STRING.getNullValue();
	}

}

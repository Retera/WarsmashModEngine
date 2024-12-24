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

public class TypeCastToCodeJassValueVisitor implements JassValueVisitor<JassValue> {
	public static final TypeCastToCodeJassValueVisitor INSTANCE = new TypeCastToCodeJassValueVisitor();

	@Override
	public JassValue accept(final IntegerJassValue value) {
		return new CodeJassValue(value.getValue());
	}

	@Override
	public JassValue accept(final RealJassValue value) {
		return JassType.CODE.getNullValue();
	}

	@Override
	public JassValue accept(final BooleanJassValue value) {
		return JassType.CODE.getNullValue();
	}

	@Override
	public JassValue accept(final StringJassValue value) {
		return JassType.CODE.getNullValue();
	}

	@Override
	public JassValue accept(final CodeJassValue value) {
		return value;
	}

	@Override
	public JassValue accept(final ArrayJassValue value) {
		return JassType.CODE.getNullValue();
	}

	@Override
	public JassValue accept(final HandleJassValue value) {
		return JassType.CODE.getNullValue();
	}

	@Override
	public JassValue accept(final DummyJassValue value) {
		return JassType.CODE.getNullValue();
	}

	@Override
	public JassValue accept(final StructJassValue value) {
		return JassType.CODE.getNullValue();
	}

	@Override
	public JassValue accept(final StaticStructTypeJassValue value) {
		return JassType.CODE.getNullValue();
	}

}

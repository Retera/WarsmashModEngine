package com.etheller.interpreter.ast.value.visitor.cast;

import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.HandleJassType;
import com.etheller.interpreter.ast.value.JassTypeVisitor;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.PrimitiveJassType;
import com.etheller.interpreter.ast.value.StaticStructTypeJassValue;
import com.etheller.interpreter.ast.value.StructJassType;

public class TypeCastConverterGettingJassTypeVisitor implements JassTypeVisitor<JassValueVisitor<JassValue>> {
	public static final TypeCastConverterGettingJassTypeVisitor INSTANCE = new TypeCastConverterGettingJassTypeVisitor();

	@Override
	public JassValueVisitor<JassValue> accept(final PrimitiveJassType primitiveType) {
		// NOTE: bad performance, the following should not be an "if" check for each
		// type,
		// should instead be a single switch/jump informed by type
		if (primitiveType == PrimitiveJassType.BOOLEAN) {
			return TypeCastToBooleanJassValueVisitor.INSTANCE;
		}
		else if (primitiveType == PrimitiveJassType.CODE) {
			return TypeCastToCodeJassValueVisitor.INSTANCE;
		}
		else if (primitiveType == PrimitiveJassType.INTEGER) {
			return TypeCastToIntegerJassValueVisitor.INSTANCE;
		}
		else if (primitiveType == PrimitiveJassType.REAL) {
			return TypeCastToRealJassValueVisitor.INSTANCE;
		}
		else if (primitiveType == PrimitiveJassType.STRING) {
			return TypeCastToStringJassValueVisitor.INSTANCE;
		}
		return null;
	}

	@Override
	public JassValueVisitor<JassValue> accept(final ArrayJassType arrayType) {
		return TypeCastToArrayJassValueVisitor.INSTANCE;
	}

	@Override
	public JassValueVisitor<JassValue> accept(final HandleJassType type) {
		return new TypeCastToHandleJassValueVisitor(type);
	}

	@Override
	public JassValueVisitor<JassValue> accept(final StructJassType type) {
		return new TypeCastToStructJassValueVisitor(type);
	}

	@Override
	public JassValueVisitor<JassValue> accept(final StaticStructTypeJassValue staticType) {
		return null;
	}

}

package com.etheller.interpreter.ast.util;

import com.etheller.interpreter.ast.value.StructJassValue;

public interface CExtensibleHandle {
	StructJassValue getStructValue();

	void setStructValue(StructJassValue value);
}

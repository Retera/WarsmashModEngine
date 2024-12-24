package com.etheller.interpreter.ast.util;

import com.etheller.interpreter.ast.value.StructJassValue;

public abstract class CExtensibleHandleAbstract implements CExtensibleHandle {
	private StructJassValue structJassValue;

	@Override
	public StructJassValue getStructValue() {
		return this.structJassValue;
	}

	@Override
	public void setStructValue(final StructJassValue structJassValue) {
		this.structJassValue = structJassValue;
	}

}

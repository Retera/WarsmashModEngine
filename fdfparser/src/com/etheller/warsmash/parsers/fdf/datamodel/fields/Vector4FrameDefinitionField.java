package com.etheller.warsmash.parsers.fdf.datamodel.fields;

import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;

public class Vector4FrameDefinitionField implements FrameDefinitionField {
	private final Vector4Definition value;

	public Vector4FrameDefinitionField(final Vector4Definition value) {
		this.value = value;
	}

	public Vector4Definition getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}
}

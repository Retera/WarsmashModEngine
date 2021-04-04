package com.etheller.warsmash.parsers.fdf.datamodel.fields;

import com.etheller.warsmash.parsers.fdf.datamodel.Vector2Definition;

public class Vector2FrameDefinitionField implements FrameDefinitionField {
	private final Vector2Definition value;

	public Vector2FrameDefinitionField(final Vector2Definition value) {
		this.value = value;
	}

	public Vector2Definition getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}
}

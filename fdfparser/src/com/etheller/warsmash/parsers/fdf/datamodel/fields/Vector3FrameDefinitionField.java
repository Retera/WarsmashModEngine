package com.etheller.warsmash.parsers.fdf.datamodel.fields;

import com.etheller.warsmash.parsers.fdf.datamodel.Vector3Definition;

public class Vector3FrameDefinitionField implements FrameDefinitionField {
	private final Vector3Definition value;

	public Vector3FrameDefinitionField(final Vector3Definition value) {
		this.value = value;
	}

	public Vector3Definition getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

}

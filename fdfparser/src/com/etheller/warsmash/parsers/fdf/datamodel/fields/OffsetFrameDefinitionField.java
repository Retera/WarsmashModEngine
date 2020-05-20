package com.etheller.warsmash.parsers.fdf.datamodel.fields;

import com.etheller.warsmash.parsers.fdf.datamodel.Offset;

public class OffsetFrameDefinitionField implements FrameDefinitionField {
	private final Offset value;

	public OffsetFrameDefinitionField(final Offset value) {
		this.value = value;
	}

	public Offset getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}
}

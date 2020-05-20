package com.etheller.warsmash.parsers.fdf.datamodel.fields;

import com.etheller.warsmash.parsers.fdf.datamodel.Insets;

public class InsetsFrameDefinitionField implements FrameDefinitionField {
	private final Insets value;

	public InsetsFrameDefinitionField(final Insets value) {
		this.value = value;
	}

	public Insets getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}
}

package com.etheller.warsmash.parsers.fdf.datamodel.fields;

import com.etheller.warsmash.parsers.fdf.datamodel.ColorDefinition;

public class ColorFrameDefinitionField implements FrameDefinitionField {
	private final ColorDefinition value;

	public ColorFrameDefinitionField(final ColorDefinition value) {
		this.value = value;
	}

	public ColorDefinition getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

}

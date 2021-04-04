package com.etheller.warsmash.parsers.fdf.datamodel.fields;

import com.etheller.warsmash.parsers.fdf.datamodel.FontDefinition;

public class FontFrameDefinitionField implements FrameDefinitionField {
	private final FontDefinition value;

	public FontFrameDefinitionField(final FontDefinition value) {
		this.value = value;
	}

	public FontDefinition getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

}

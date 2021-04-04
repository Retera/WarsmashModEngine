package com.etheller.warsmash.parsers.fdf.datamodel.fields;

public class StringFrameDefinitionField implements FrameDefinitionField {
	private final String value;

	public StringFrameDefinitionField(final String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}
}

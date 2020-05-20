package com.etheller.warsmash.parsers.fdf.datamodel.fields;

public class StringPairFrameDefinitionField implements FrameDefinitionField {
	private final String first;
	private final String second;

	public StringPairFrameDefinitionField(final String first, final String second) {
		this.first = first;
		this.second = second;
	}

	public String getFirst() {
		return this.first;
	}

	public String getSecond() {
		return this.second;
	}

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}
}

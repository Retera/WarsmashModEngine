package com.etheller.warsmash.parsers.fdf.datamodel.fields;

public interface FrameDefinitionField {
	<TYPE> TYPE visit(FrameDefinitionFieldVisitor<TYPE> visitor);
}

package com.etheller.warsmash.parsers.fdf.datamodel.fields;

public interface FrameDefinitionFieldVisitor<TYPE> {
	TYPE accept(StringFrameDefinitionField field);

	TYPE accept(FloatFrameDefinitionField field);

	TYPE accept(ColorFrameDefinitionField field);

	TYPE accept(InsetsFrameDefinitionField field);

	TYPE accept(OffsetFrameDefinitionField field);

	TYPE accept(FontFrameDefinitionField field);
}

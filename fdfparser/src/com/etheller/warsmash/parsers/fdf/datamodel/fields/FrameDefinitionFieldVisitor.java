package com.etheller.warsmash.parsers.fdf.datamodel.fields;

public interface FrameDefinitionFieldVisitor<TYPE> {
	TYPE accept(RepeatingFrameDefinitionField field);

	TYPE accept(StringFrameDefinitionField field);

	TYPE accept(StringPairFrameDefinitionField field);

	TYPE accept(FloatFrameDefinitionField field);

	TYPE accept(Vector3FrameDefinitionField field);

	TYPE accept(Vector4FrameDefinitionField field);

	TYPE accept(Vector2FrameDefinitionField field);

	TYPE accept(FontFrameDefinitionField field);

	TYPE accept(TextJustifyFrameDefinitionField field);

	TYPE accept(MenuItemFrameDefinitionField field);
}

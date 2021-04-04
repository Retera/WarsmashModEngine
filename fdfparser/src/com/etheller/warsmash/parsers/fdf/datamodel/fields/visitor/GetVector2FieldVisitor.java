package com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor;

import com.etheller.warsmash.parsers.fdf.datamodel.Vector2Definition;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FloatFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FontFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FrameDefinitionFieldVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringPairFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.TextJustifyFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector2FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector3FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector4FrameDefinitionField;

public class GetVector2FieldVisitor implements FrameDefinitionFieldVisitor<Vector2Definition> {
	public static GetVector2FieldVisitor INSTANCE = new GetVector2FieldVisitor();

	@Override
	public Vector2Definition accept(final StringFrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector2Definition accept(final StringPairFrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector2Definition accept(final FloatFrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector2Definition accept(final Vector3FrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector2Definition accept(final Vector2FrameDefinitionField field) {
		return field.getValue();
	}

	@Override
	public Vector2Definition accept(final Vector4FrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector2Definition accept(final FontFrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector2Definition accept(final TextJustifyFrameDefinitionField field) {
		return null;
	}

}

package com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor;

import com.etheller.warsmash.parsers.fdf.datamodel.Vector3Definition;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FloatFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FontFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FrameDefinitionFieldVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.MenuItemFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.RepeatingFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringPairFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.TextJustifyFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector2FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector3FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector4FrameDefinitionField;

public class GetVector3FieldVisitor implements FrameDefinitionFieldVisitor<Vector3Definition> {
	public static GetVector3FieldVisitor INSTANCE = new GetVector3FieldVisitor();

	@Override
	public Vector3Definition accept(final RepeatingFrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector3Definition accept(final StringFrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector3Definition accept(final StringPairFrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector3Definition accept(final FloatFrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector3Definition accept(final Vector3FrameDefinitionField field) {
		return field.getValue();
	}

	@Override
	public Vector3Definition accept(final Vector4FrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector3Definition accept(final Vector2FrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector3Definition accept(final FontFrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector3Definition accept(final TextJustifyFrameDefinitionField field) {
		return null;
	}

	@Override
	public Vector3Definition accept(final MenuItemFrameDefinitionField field) {
		return null;
	}

}

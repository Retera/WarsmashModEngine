package com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor;

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

public class GetStringPairFieldVisitor implements FrameDefinitionFieldVisitor<StringPairFrameDefinitionField> {
	public static GetStringPairFieldVisitor INSTANCE = new GetStringPairFieldVisitor();

	@Override
	public StringPairFrameDefinitionField accept(final RepeatingFrameDefinitionField field) {
		return null;
	}

	@Override
	public StringPairFrameDefinitionField accept(final StringFrameDefinitionField field) {
		return null;
	}

	@Override
	public StringPairFrameDefinitionField accept(final StringPairFrameDefinitionField field) {
		return field;
	}

	@Override
	public StringPairFrameDefinitionField accept(final FloatFrameDefinitionField field) {
		return null;
	}

	@Override
	public StringPairFrameDefinitionField accept(final Vector3FrameDefinitionField field) {
		return null;
	}

	@Override
	public StringPairFrameDefinitionField accept(final Vector4FrameDefinitionField field) {
		return null;
	}

	@Override
	public StringPairFrameDefinitionField accept(final Vector2FrameDefinitionField field) {
		return null;
	}

	@Override
	public StringPairFrameDefinitionField accept(final FontFrameDefinitionField field) {
		return null;
	}

	@Override
	public StringPairFrameDefinitionField accept(final TextJustifyFrameDefinitionField field) {
		return null;
	}

	@Override
	public StringPairFrameDefinitionField accept(final MenuItemFrameDefinitionField field) {
		return null;
	}

}

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

public class GetStringFieldVisitor implements FrameDefinitionFieldVisitor<String> {
	public static GetStringFieldVisitor INSTANCE = new GetStringFieldVisitor();

	@Override
	public String accept(final RepeatingFrameDefinitionField field) {
		return null;
	}

	@Override
	public String accept(final StringFrameDefinitionField field) {
		return field.getValue();
	}

	@Override
	public String accept(final StringPairFrameDefinitionField field) {
		return null;
	}

	@Override
	public String accept(final FloatFrameDefinitionField field) {
		return null;
	}

	@Override
	public String accept(final Vector3FrameDefinitionField field) {
		return null;
	}

	@Override
	public String accept(final Vector4FrameDefinitionField field) {
		return null;
	}

	@Override
	public String accept(final Vector2FrameDefinitionField field) {
		return null;
	}

	@Override
	public String accept(final FontFrameDefinitionField field) {
		return null;
	}

	@Override
	public String accept(final TextJustifyFrameDefinitionField field) {
		return null;
	}

	@Override
	public String accept(final MenuItemFrameDefinitionField field) {
		return null;
	}

}

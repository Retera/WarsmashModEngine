package com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor;

import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
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

public class GetTextJustifyFieldVisitor implements FrameDefinitionFieldVisitor<TextJustify> {
	public static GetTextJustifyFieldVisitor INSTANCE = new GetTextJustifyFieldVisitor();

	@Override
	public TextJustify accept(final RepeatingFrameDefinitionField field) {
		return null;
	}

	@Override
	public TextJustify accept(final StringFrameDefinitionField field) {
		return null;
	}

	@Override
	public TextJustify accept(final StringPairFrameDefinitionField field) {
		return null;
	}

	@Override
	public TextJustify accept(final FloatFrameDefinitionField field) {
		return null;
	}

	@Override
	public TextJustify accept(final Vector3FrameDefinitionField field) {
		return null;
	}

	@Override
	public TextJustify accept(final Vector4FrameDefinitionField field) {
		return null;
	}

	@Override
	public TextJustify accept(final Vector2FrameDefinitionField field) {
		return null;
	}

	@Override
	public TextJustify accept(final FontFrameDefinitionField field) {
		return null;
	}

	@Override
	public TextJustify accept(final TextJustifyFrameDefinitionField field) {
		return field.getValue();
	}

	@Override
	public TextJustify accept(final MenuItemFrameDefinitionField field) {
		return null;
	}

}

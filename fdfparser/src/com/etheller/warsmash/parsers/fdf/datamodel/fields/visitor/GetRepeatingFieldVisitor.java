package com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor;

import java.util.List;

import com.etheller.warsmash.parsers.fdf.datamodel.fields.FloatFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FontFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.FrameDefinitionFieldVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.MenuItemFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.RepeatingFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.StringPairFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.TextJustifyFrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector2FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector3FrameDefinitionField;
import com.etheller.warsmash.parsers.fdf.datamodel.fields.Vector4FrameDefinitionField;

public class GetRepeatingFieldVisitor implements FrameDefinitionFieldVisitor<List<FrameDefinitionField>> {
	public static GetRepeatingFieldVisitor INSTANCE = new GetRepeatingFieldVisitor();

	@Override
	public List<FrameDefinitionField> accept(final RepeatingFrameDefinitionField field) {
		return field.getFields();
	}

	@Override
	public List<FrameDefinitionField> accept(final StringFrameDefinitionField field) {
		return null;
	}

	@Override
	public List<FrameDefinitionField> accept(final StringPairFrameDefinitionField field) {
		return null;
	}

	@Override
	public List<FrameDefinitionField> accept(final FloatFrameDefinitionField field) {
		return null;
	}

	@Override
	public List<FrameDefinitionField> accept(final Vector3FrameDefinitionField field) {
		return null;
	}

	@Override
	public List<FrameDefinitionField> accept(final Vector4FrameDefinitionField field) {
		return null;
	}

	@Override
	public List<FrameDefinitionField> accept(final Vector2FrameDefinitionField field) {
		return null;
	}

	@Override
	public List<FrameDefinitionField> accept(final FontFrameDefinitionField field) {
		return null;
	}

	@Override
	public List<FrameDefinitionField> accept(final TextJustifyFrameDefinitionField field) {
		return null;
	}

	@Override
	public List<FrameDefinitionField> accept(final MenuItemFrameDefinitionField field) {
		return null;
	}

}

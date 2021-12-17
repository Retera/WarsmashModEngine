package com.etheller.warsmash.parsers.fdf.datamodel.fields.visitor;

import com.etheller.warsmash.parsers.fdf.datamodel.MenuItem;
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

public class GetMenuItemFieldVisitor implements FrameDefinitionFieldVisitor<MenuItem> {
	public static GetMenuItemFieldVisitor INSTANCE = new GetMenuItemFieldVisitor();

	@Override
	public MenuItem accept(final RepeatingFrameDefinitionField field) {
		return null;
	}

	@Override
	public MenuItem accept(final StringFrameDefinitionField field) {
		return null;
	}

	@Override
	public MenuItem accept(final StringPairFrameDefinitionField field) {
		return null;
	}

	@Override
	public MenuItem accept(final FloatFrameDefinitionField field) {
		return null;
	}

	@Override
	public MenuItem accept(final Vector3FrameDefinitionField field) {
		return null;
	}

	@Override
	public MenuItem accept(final Vector4FrameDefinitionField field) {
		return null;
	}

	@Override
	public MenuItem accept(final Vector2FrameDefinitionField field) {
		return null;
	}

	@Override
	public MenuItem accept(final FontFrameDefinitionField field) {
		return null;
	}

	@Override
	public MenuItem accept(final TextJustifyFrameDefinitionField field) {
		return null;
	}

	@Override
	public MenuItem accept(final MenuItemFrameDefinitionField field) {
		return field.getValue();
	}

}

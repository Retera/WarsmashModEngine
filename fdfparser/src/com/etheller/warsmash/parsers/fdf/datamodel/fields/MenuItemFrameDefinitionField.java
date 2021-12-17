package com.etheller.warsmash.parsers.fdf.datamodel.fields;

import com.etheller.warsmash.parsers.fdf.datamodel.MenuItem;

public class MenuItemFrameDefinitionField implements FrameDefinitionField {
	private final MenuItem value;

	public MenuItemFrameDefinitionField(final MenuItem value) {
		this.value = value;
	}

	public MenuItem getValue() {
		return this.value;
	}

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

}

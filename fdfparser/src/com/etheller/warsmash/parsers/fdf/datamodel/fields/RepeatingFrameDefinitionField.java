package com.etheller.warsmash.parsers.fdf.datamodel.fields;

import java.util.ArrayList;
import java.util.List;

public class RepeatingFrameDefinitionField implements FrameDefinitionField {
	private final List<FrameDefinitionField> fields = new ArrayList<>();

	@Override
	public <TYPE> TYPE visit(final FrameDefinitionFieldVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	public FrameDefinitionField get(final int index) {
		return this.fields.get(index);
	}

	public void add(final FrameDefinitionField field) {
		this.fields.add(field);
	}

	public int size() {
		return this.fields.size();
	}

	public List<FrameDefinitionField> getFields() {
		return this.fields;
	}

}

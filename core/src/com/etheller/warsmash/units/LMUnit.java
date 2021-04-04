package com.etheller.warsmash.units;

import java.util.LinkedHashMap;

public class LMUnit extends Element {

	public LMUnit(final String id, final DataTable table) {
		super(id, table);
		this.fields = new LinkedHashMap<>();
	}

}

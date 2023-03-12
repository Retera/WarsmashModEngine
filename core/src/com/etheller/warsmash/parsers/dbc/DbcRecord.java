package com.etheller.warsmash.parsers.dbc;

import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.units.DataTable;

public interface DbcRecord {
	void load(LongMap<String> stringsTable, DataTable output);
}

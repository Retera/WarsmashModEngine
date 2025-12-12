package com.etheller.warsmash.parsers.dbc;

import java.util.List;

import com.badlogic.gdx.utils.LongMap;

public class DbcTable<T> {
	private final LongMap<String> stringMap;
	private final List<T> records;

	public DbcTable(final LongMap<String> stringMap, final List<T> records) {
		this.stringMap = stringMap;
		this.records = records;
	}

	public LongMap<String> getStringMap() {
		return this.stringMap;
	}

	public List<T> getRecords() {
		return this.records;
	}

}

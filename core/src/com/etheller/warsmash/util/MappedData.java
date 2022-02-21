package com.etheller.warsmash.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A structure that holds mapped data from INI and SLK files.
 *
 * In the case of SLK files, the first row is expected to hold the names of the
 * columns.
 */
public class MappedData {
	private final Map<String, MappedDataRow> map = new HashMap<>();

	public MappedData() {
		this(null);
	}

	public MappedData(final String buffer) {
		if (buffer != null) {
			load(buffer);
		}
	}

	/**
	 * Load data from an SLK file or an INI file.
	 *
	 * Note that this may override previous properties!
	 */
	public void load(final String buffer) {
		if (buffer.startsWith("ID;")) {
			final SlkFile file = new SlkFile(buffer);
			final List<List<Object>> rows = file.rows;
			final List<Object> header = rows.get(0);
			int keyColumn = 0;
			for (int i = 1; i < header.size(); i++) {
				final Object headerColumnName = header.get(i);
				if ("AnimationEventCode".equals(headerColumnName)) {
					keyColumn = i;
				}
			}

			for (int i = 1, l = rows.size(); i < l; i++) {
				final List<Object> row = rows.get(i);
				if (row != null) {
					String name = (String) row.get(keyColumn);

					if (name != null) {
						name = name.toLowerCase();

						if (!this.map.containsKey(name)) {
							this.map.put(name, new MappedDataRow());
						}

						final MappedDataRow mapped = this.map.get(name);

						for (int j = 0, k = header.size(); j < k; j++) {
							final Object headerObj = header.get(j);
							String key = headerObj == null ? null : headerObj.toString();

							// UnitBalance.slk doesn't define the name of one row.
							if (key == null) {
								key = "column" + j;
							}

							mapped.put(key, j < row.size() ? row.get(j) : null);
						}
					}
				}
			}
		}
		else {
			final IniFile file = new IniFile(buffer);
			final Map<String, Map<String, String>> sections = file.sections;

			for (final Map.Entry<String, Map<String, String>> rowAndProperties : sections.entrySet()) {
				final String row = rowAndProperties.getKey();

				if (!this.map.containsKey(row)) {
					this.map.put(row, new MappedDataRow());
				}

				final MappedDataRow mapped = this.map.get(row);

				for (final Map.Entry<String, String> nameAndProperty : rowAndProperties.getValue().entrySet()) {
					mapped.put(nameAndProperty.getKey(), nameAndProperty.getValue());
				}
			}
		}
	}

	public MappedDataRow getRow(final String key) {
		return this.map.get(key.toLowerCase());
	}

	public Object getProperty(final String key, final String name) {
		return this.map.get(key.toLowerCase()).get(name);
	}

	public void setRow(final String key, final MappedDataRow values) {
		this.map.put(key.toLowerCase(), values);
	}
}

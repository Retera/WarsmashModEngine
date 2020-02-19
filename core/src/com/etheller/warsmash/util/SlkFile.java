package com.etheller.warsmash.util;

import java.util.ArrayList;
import java.util.List;

public class SlkFile {
	public List<List<Object>> rows = new ArrayList<>();

	public SlkFile(final String buffer) {
		if (buffer != null) {
			this.load(buffer);
		}
	}

	public void load(final String buffer) {
		if (!buffer.startsWith("ID")) {
			throw new RuntimeException("WrongMagicNumber");
		}

		int x = 0;
		int y = 0;
		for (final String line : buffer.split("\n")) {
			// The B command is supposed to define the total number of columns and rows,
			// however in UbetSplatData.slk it gives wrong information
			// Therefore, just ignore it, since JavaScript arrays grow as they want either
			// way
			if (line.charAt(0) != 'B') {
				for (final String token : line.split(";")) {
					if (token.isEmpty()) {
						continue;
					}
					final char op = token.charAt(0);
					final String valueString = token.substring(1).trim();
					final Object value;

					if (op == 'X') {
						x = Integer.parseInt(valueString, 10) - 1;
					}
					else if (op == 'Y') {
						y = Integer.parseInt(valueString, 10) - 1;
					}
					else if (op == 'K') {
						while (y >= this.rows.size()) {
							this.rows.add(null);
						}
						if (this.rows.get(y) == null) {
							this.rows.set(y, new ArrayList<>());
						}

						if (valueString.charAt(0) == '"') {
							value = valueString.substring(1, valueString.length() - 1);
						}
						else if ("TRUE".equals(valueString)) {
							value = true;
						}
						else if ("FALSE".equals(valueString)) {
							value = false;
						}
						else {
							value = Float.parseFloat(valueString);
						}

						final List<Object> row = this.rows.get(y);
						while (x >= row.size()) {
							row.add(null);
						}
						row.set(x, value);
					}
				}
			}
		}
	}
}

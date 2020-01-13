package com.etheller.warsmash.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IniFile {
	private static final Pattern NAME_PATTERN = Pattern.compile("^\\[(.+?)\\].*");
	private static final Pattern DATA_PATTERN = Pattern.compile("^(.+?)=(.*?)$");
	public final Map<String, String> properties = new HashMap<>();
	public final Map<String, Map<String, String>> sections = new HashMap<>();

	public IniFile(final String buffer) {
		if (buffer != null) {
			this.load(buffer);
		}
	}

	public void load(final String buffer) {
		// All properties added until a section is reached are added to the properties
		// map.
		// Once a section is reached, any further properties will be added to it until
		// matching another section, etc.
		Map<String, String> section = this.properties;

		// Below: using \n instead of \r\n because its not reading directly from the
		// actual file, but instead from a Java translated thing
		for (final String line : buffer.split("\n")) {
			// INI defines comments as starting with a semicolon ';'.
			// However, Warcraft 3 INI files use normal C comments '//'.
			// In addition, Warcraft 3 files have empty lines.
			// Therefore, ignore any line matching any of these conditions.

			if ((line.length() != 0) && !line.startsWith("//") && !line.startsWith(";")) {
				final Matcher matcher = NAME_PATTERN.matcher(line);

				if (matcher.matches()) {
					final String name = matcher.group(1).trim().toLowerCase();

					section = this.sections.get(name);

					if (section == null) {
						section = new HashMap<>();

						this.sections.put(name, section);
					}
				}
				else {
					final Matcher dataMatcher = DATA_PATTERN.matcher(line);
					if (dataMatcher.matches()) {
						section.put(dataMatcher.group(1).toLowerCase(), dataMatcher.group(2));
					}
				}
			}

		}

	}

	public String save() {
		final List<String> lines = new ArrayList<>();

		for (final Map.Entry<String, String> entry : this.properties.entrySet()) {
			lines.add(entry.getKey() + "=" + entry.getValue());
		}

		for (final Map.Entry<String, Map<String, String>> sectionData : this.sections.entrySet()) {
			lines.add("[" + sectionData.getKey() + "]");

			for (final Map.Entry<String, String> entry : sectionData.getValue().entrySet()) {
				lines.add(entry.getKey() + "=" + entry.getValue());
			}
		}

		return String.join("\r\n", lines);
	}

	public Map<String, String> getSection(final String name) {
		return this.sections.get(name.toLowerCase());
	}
}

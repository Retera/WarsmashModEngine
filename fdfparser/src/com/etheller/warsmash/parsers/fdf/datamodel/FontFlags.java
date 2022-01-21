package com.etheller.warsmash.parsers.fdf.datamodel;

import java.util.EnumSet;

public enum FontFlags {
	FIXEDSIZE,
	PASSWORDFIELD;

	public static EnumSet<FontFlags> parseFontFlags(final String cornerFlags) {
		final EnumSet<FontFlags> set = EnumSet.noneOf(FontFlags.class);
		for (final String flag : cornerFlags.split("\\|")) {
			if (!"".equals(flag)) {
				set.add(FontFlags.valueOf(flag));
			}
		}
		return set;
	}
}

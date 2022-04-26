package com.etheller.warsmash.parsers.fdf.datamodel;

import java.util.EnumSet;

public enum ControlStyle {
	AUTOTRACK,
	HIGHLIGHTONFOCUS,
	HIGHLIGHTONMOUSEOVER;

	public static EnumSet<ControlStyle> parseControlStyle(final String controlStyles) {
		final EnumSet<ControlStyle> set = EnumSet.noneOf(ControlStyle.class);
		for (final String flag : controlStyles.split("\\|")) {
			if (flag == null || !flag.isEmpty()) {
				set.add(ControlStyle.valueOf(flag));
			}
		}
		return set;
	}
}

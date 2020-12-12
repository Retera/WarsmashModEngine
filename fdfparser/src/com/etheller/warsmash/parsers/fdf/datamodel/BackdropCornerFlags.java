package com.etheller.warsmash.parsers.fdf.datamodel;

import java.util.EnumSet;

public enum BackdropCornerFlags {
	UL,
	UR,
	BL,
	BR,
	T,
	L,
	B,
	R;

	public static EnumSet<BackdropCornerFlags> parseCornerFlags(final String cornerFlags) {
		final EnumSet<BackdropCornerFlags> set = EnumSet.noneOf(BackdropCornerFlags.class);
		for (final String flag : cornerFlags.split("\\|")) {
			if (!"".equals(flag)) {
				set.add(BackdropCornerFlags.valueOf(flag));
			}
		}
		return set;
	}
}

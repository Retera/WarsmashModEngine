package com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing;

import java.util.EnumSet;

public enum CBuildingPathingType {
	BLIGHTED,
	UNBUILDABLE,
	UNFLYABLE,
	UNWALKABLE,
	UNAMPH,
	UNFLOAT;

	public static CBuildingPathingType parsePathingType(final String typeString) {
		if ("_".equals(typeString) || "".equals(typeString)) {
			return null;
		}
		return valueOf(typeString.toUpperCase());
	}

	public static EnumSet<CBuildingPathingType> parsePathingTypeListSet(final String pathingListString) {
		final EnumSet<CBuildingPathingType> types = EnumSet.noneOf(CBuildingPathingType.class);
		for (final String type : pathingListString.split(",")) {
			final CBuildingPathingType parsedType = parsePathingType(type);
			if (parsedType != null) {
				types.add(parsedType);
			}
		}
		return types;
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

import java.util.EnumSet;
import java.util.List;

import com.etheller.interpreter.ast.util.CHandle;

public enum CTargetType implements CHandle {
	AIR,
	ALIVE,
	ALLIES,
	DEAD,
	DEBRIS,
	ENEMIES,
	GROUND,
	HERO,
	INVULNERABLE,
	ITEM,
	MECHANICAL,
	NEUTRAL,
	NONE,
	NONHERO,
	NONSAPPER,
	NOTSELF,
	ORGANIC,
	PLAYERUNITS,
	SAPPER,
	SELF,
	STRUCTURE,
	TERRAIN,
	TREE,
	VULNERABLE,
	WALL,
	WARD,
	ANCIENT,
	NONANCIENT,
	FRIEND,
	BRIDGE,
	DECORATION,
	// BELOW: internal values:
	NON_MAGIC_IMMUNE,
	NON_ETHEREAL

	;

	public final static CTargetType[] VALUES = values();

	public static CTargetType parseTargetType(final String targetTypeString) {
		if (targetTypeString == null) {
			return null;
		}
		switch (targetTypeString.toLowerCase()) {
		case "air":
			return AIR;
		case "alive":
		case "aliv":
			return ALIVE;
		case "allies":
		case "alli":
		case "ally":
			return ALLIES;
		case "dead":
			return DEAD;
		case "debris":
		case "debr":
			return DEBRIS;
		case "enemies":
		case "enem":
		case "enemy":
			return ENEMIES;
		case "ground":
		case "grou":
			return GROUND;
		case "hero":
			return HERO;
		case "invulnerable":
		case "invu":
			return INVULNERABLE;
		case "item":
			return ITEM;
		case "mechanical":
		case "mech":
			return MECHANICAL;
		case "neutral":
		case "neut":
			return NEUTRAL;
		case "none":
			return NONE;
		case "nonhero":
		case "nonh":
			return NONHERO;
		case "nonsapper":
			return NONSAPPER;
		case "notself":
		case "nots":
			return NOTSELF;
		case "organic":
		case "orga":
			return ORGANIC;
		case "player":
		case "play":
			return PLAYERUNITS;
		case "sapper":
			return SAPPER;
		case "self":
			return SELF;
		case "structure":
		case "stru":
			return STRUCTURE;
		case "terrain":
		case "terr":
			return TERRAIN;
		case "tree":
			return TREE;
		case "vulnerable":
		case "vuln":
			return VULNERABLE;
		case "wall":
			return WALL;
		case "ward":
			return WARD;
		case "ancient":
			return ANCIENT;
		case "nonancient":
			return NONANCIENT;
		case "friend":
		case "frie":
			return FRIEND;
		case "bridge":
			return BRIDGE;
		case "decoration":
		case "deco":
			return DECORATION;
		default:
			return null;
		}
	}

	public static EnumSet<CTargetType> parseTargetTypeSet(final String targetTypeString) {
		final EnumSet<CTargetType> types = EnumSet.noneOf(CTargetType.class);
		for (final String type : targetTypeString.split(",")) {
			final CTargetType parsedType = parseTargetType(type);
			if (parsedType != null) {
				types.add(parsedType);
			}
		}
		return types;
	}

	public static EnumSet<CTargetType> parseTargetTypeSet(final List<String> targetTypeStrings) {
		final EnumSet<CTargetType> types = EnumSet.noneOf(CTargetType.class);
		for (final String type : targetTypeStrings) {
			final CTargetType parsedType = parseTargetType(type);
			if (parsedType != null) {
				types.add(parsedType);
			}
		}
		return types;
	}

	@Override
	public int getHandleId() {
		return ordinal();
	}
}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitRace;

public class ABRandomTypeHandler {
	private static List<CUnitType> landCritters = null;
	private static List<CUnitType> airCritters = null;
	private static List<CUnitType> seaCritters = null;

	private static List<CItemType> randomItems = null;

	private static void loadCritterLists(CSimulation game) {
		landCritters = new ArrayList<>();
		airCritters = new ArrayList<>();
		seaCritters = new ArrayList<>();
		Collection<CUnitType> types = game.getUnitData().getAllUnitTypesLightweight();
		String tileset = ("" + game.getTileset()).toUpperCase();

		for (CUnitType type : types) {
			if (type.getRace() == CUnitRace.CRITTERS
					&& ("*".equals(type.getTilesets()) || type.getTilesets().indexOf(tileset) >= 0)
					&& !type.isSpecial()) {
				switch (type.getMovementType()) {
				case AMPHIBIOUS:
					seaCritters.add(type);
					break;
				case FLY:
					airCritters.add(type);
					break;
				case FOOT:
				case FOOT_NO_COLLISION:
				case HORSE:
				case HOVER:
					landCritters.add(type);
					break;
				case FLOAT:
				case DISABLED:
				default:
					break;

				}
			}
		}
	}

	private static void loadItemLists(CSimulation game) {
		randomItems = new ArrayList<>();
		Collection<CItemType> types = game.getItemData().getAllItemTypes();

		for (CItemType type : types) {
			if (type.isIncludeAsRandomChoice()) {
				randomItems.add(type);
			}
		}
	}

	public static CUnitType getRandomMechanicalCritterType(CSimulation game) {
		if (landCritters == null) {
			loadCritterLists(game);
		}
		if (landCritters.size() > 0 || seaCritters.size() > 0) {
			int i = game.getSeededRandom().nextInt(landCritters.size() + seaCritters.size());
			if (i >= landCritters.size()) {
				return seaCritters.get(i - landCritters.size());
			} else {
				return landCritters.get(i);
			}
		} else if (airCritters.size() > 0) {
			int i = game.getSeededRandom().nextInt(airCritters.size());
			return airCritters.get(i);
		}
		return null;
	}

	public static CItemType getRandomItemType(CSimulation game) {
		if (randomItems == null) {
			loadItemLists(game);
		}
		int i = game.getSeededRandom().nextInt(randomItems.size());
		return randomItems.get(i);
	}

	public static CItemType getRandomItemType(CSimulation game, War3ID previousType, int level,
			boolean ignoreValidTargetField, boolean ignoreRandomPoolField) {
		List<CItemType> filtered = new ArrayList<>();
		for (CItemType type : game.getItemData().getAllItemTypes()) {
			if (!type.getTypeId().equals(previousType) && (level == -1 || level == type.getLevel())
					&& (ignoreValidTargetField || type.isValidTargetForTransformation())
					&& (ignoreRandomPoolField || type.isIncludeAsRandomChoice())) {
				filtered.add(type);
			}
		}
		if (filtered.size() == 0) {
			return null;
		}
		int i = game.getSeededRandom().nextInt(filtered.size());
		return filtered.get(i);
	}

}

package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.template;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityAbilityBuilderSimpleAuraTemplate extends AbilityGenericSingleIconPassiveAbility {

	private List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private Map<String, Object> localStore;

	private Set<CUnit> auraGroup;
	private int lastSeenLevel = 0;
	private int loopTick = 0;

	private static final Rectangle recycleRect = new Rectangle();
	private EnumSet<CTargetType> targetsAllowed = null;
	private float range = 0;

	private CBuff buff;

	private Map<Integer,List<War3ID>> abilityIdsToAddPerLevel;
	private List<War3ID> levellingAbilityIdsToAdd;

	private Map<Integer,List<CAbility>> abilitiesToAddPerLevel;
	private List<CLevelingAbility> levellingAbilitiesToAdd;
	
	private final int LEAVE_GROUP_TICKS = (int) (3 / WarsmashConstants.SIMULATION_STEP_TIME);
	private final int ENTER_GROUP_TICKS = (int) (0.4 / WarsmashConstants.SIMULATION_STEP_TIME);
	private final int RESET_GROUP_TICKS = LEAVE_GROUP_TICKS * 2;
	
	public CAbilityAbilityBuilderSimpleAuraTemplate(int handleId, War3ID code, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, Map<String, Object> localStore,
			Map<Integer,List<War3ID>> abilityIdsToAddPerLevel, List<War3ID> levellingAbilityIdsToAdd) {
		super(code, alias, handleId);
		this.levelData = levelData;
		this.localStore = localStore;
		this.abilityIdsToAddPerLevel = abilityIdsToAddPerLevel;
		this.levellingAbilityIdsToAdd = levellingAbilityIdsToAdd;
		
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
		targetsAllowed = levelData.get(getLevel()).getTargetsAllowed();
		range = levelData.get(getLevel()).getCastRange();
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		game.getAbilityData().createAbility(getAlias(), game.getHandleIdAllocator().createId());
		auraGroup = new HashSet<>();
		localStore.put(ABLocalStoreKeys.AURAGROUP, auraGroup);
		lastSeenLevel = getLevel();
		this.abilitiesToAddPerLevel = new HashMap<>();
		this.levellingAbilitiesToAdd = new ArrayList<>();
		if (abilityIdsToAddPerLevel != null) {
			for (Integer i : abilityIdsToAddPerLevel.keySet()) {
				List<CAbility> list = new ArrayList<>();
				abilitiesToAddPerLevel.put(i, list);
				for (War3ID abilityId : abilityIdsToAddPerLevel.get(i)) {
					list.add(game.getAbilityData().getAbilityType(abilityId)
							.createAbility(game.getHandleIdAllocator().createId()));
				}
			}
		}
		if (levellingAbilityIdsToAdd != null) {
			for (War3ID abilityId : levellingAbilityIdsToAdd) {
				CAbility abil = game.getAbilityData().getAbilityType(abilityId)
						.createAbility(game.getHandleIdAllocator().createId());
				if (abil instanceof CLevelingAbility) {
					levellingAbilitiesToAdd.add((CLevelingAbility) abil);
				}
			}
		}
		
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		emptyAura(game, unit);
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		if (loopTick % LEAVE_GROUP_TICKS == 0) {
			List<CUnit> unitList;
			if (lastSeenLevel != getLevel()) {
				unitList = new ArrayList<>(auraGroup);
				for (CUnit iter : unitList) {
					if (!(iter.canBeTargetedBy(game, unit, targetsAllowed) && unit.canReach(iter, range))) {
						updateLevelOfAura(game, iter, lastSeenLevel, getLevel());
					}
				}
				lastSeenLevel = getLevel();
			}
			unitList = new ArrayList<>(auraGroup);
			for (CUnit iter : unitList) {
				if (!(iter.canBeTargetedBy(game, unit, targetsAllowed) && unit.canReach(iter, range))) {
					removeUnitFromAura(game, iter);
				}
			}
		}
		if (loopTick % ENTER_GROUP_TICKS == 0) {
			recycleRect.set(unit.getX() - range, unit.getY() - range, range * 2, range * 2);
			game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
				@Override
				public boolean call(final CUnit enumUnit) {
					if (unit.canReach(enumUnit, range) && enumUnit.canBeTargetedBy(game, unit, targetsAllowed)
							&& !auraGroup.contains(enumUnit)) {
						addUnitToAura(game, enumUnit);
					}
					return false;
				}
			});
		}
		loopTick++;
		loopTick = loopTick % (RESET_GROUP_TICKS);

	}

	public void emptyAura(CSimulation game, CUnit unit) {
		List<CUnit> unitList = new ArrayList<>(auraGroup);
		for (CUnit iter : unitList) {
			removeUnitFromAura(game, iter);
		}
	}

	public void addUnitToAura(CSimulation game, CUnit unit) {
		auraGroup.add(unit);
		if (abilitiesToAddPerLevel != null) {
			for (CAbility ability : abilitiesToAddPerLevel.get(getLevel())) {
				unit.add(game, ability);
			}
		}
		if (levellingAbilitiesToAdd != null) {
			for (CAbility ability : levellingAbilitiesToAdd) {
				unit.add(game, ability);
			}
		}
		unit.add(game, buff);
	}

	public void updateLevelOfAura(CSimulation game, CUnit unit, int prevLevel, int curLevel) {
		if (abilitiesToAddPerLevel != null) {
			for (CAbility ability : abilitiesToAddPerLevel.get(prevLevel)) {
				unit.remove(game, ability);
			}
		}
		if (abilitiesToAddPerLevel != null) {
			for (CAbility ability : abilitiesToAddPerLevel.get(curLevel)) {
				unit.add(game, ability);
			}
		}
		if (levellingAbilitiesToAdd != null) {
			for (CLevelingAbility ability : levellingAbilitiesToAdd) {
				ability.setLevel(game, unit, curLevel);
			}
		}
	}

	public void removeUnitFromAura(CSimulation game, CUnit unit) {
		unit.remove(game, buff);
		if (abilitiesToAddPerLevel != null) {
			for (CAbility ability : abilitiesToAddPerLevel.get(getLevel())) {
				unit.remove(game, ability);
			}
		}
		if (levellingAbilitiesToAdd != null) {
			for (CAbility ability : levellingAbilitiesToAdd) {
				unit.remove(game, ability);
			}
		}
		auraGroup.remove(unit);
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
		emptyAura(game, unit);
	}

	@Override
	public boolean isUniversal() {
		return true;
	}

}

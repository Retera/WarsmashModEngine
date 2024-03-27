package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.template;

import java.util.ArrayList;
import java.util.EnumSet;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityAbilityBuilderAuraTemplate extends AbilityGenericSingleIconPassiveAbility {

	private List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private Map<String, Object> localStore;

	private Set<CUnit> auraGroup;
	private int lastSeenLevel = 0;
	private int loopTick = 0;

	private static final Rectangle recycleRect = new Rectangle();
	private EnumSet<CTargetType> targetsAllowed = null;
	private float range = 0;

	private CBuff buff;

	private List<ABAction> addToAuraActions;
	private List<ABAction> updateAuraLevelActions;
	private List<ABAction> removeFromAuraActions;
	
	private final int LEAVE_GROUP_TICKS = (int) (3 / WarsmashConstants.SIMULATION_STEP_TIME);
	private final int ENTER_GROUP_TICKS = (int) (0.4 / WarsmashConstants.SIMULATION_STEP_TIME);
	private final int RESET_GROUP_TICKS = LEAVE_GROUP_TICKS * 2;
	
	public CAbilityAbilityBuilderAuraTemplate(int handleId, War3ID code, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, Map<String, Object> localStore,
			List<ABAction> addToAuraActions, List<ABAction> updateAuraLevelActions,
			List<ABAction> removeFromAuraActions) {
		super(code, alias, handleId);
		this.levelData = levelData;
		this.localStore = localStore;
		this.addToAuraActions = addToAuraActions;
		this.updateAuraLevelActions = updateAuraLevelActions;
		this.removeFromAuraActions = removeFromAuraActions;
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
		auraGroup = new HashSet<>();
		localStore.put(ABLocalStoreKeys.AURAGROUP, auraGroup);
		lastSeenLevel = getLevel();
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
		if (addToAuraActions != null) {
			for (ABAction action : addToAuraActions) {
				action.runAction(game, unit, localStore, 0);
			}
		}
		unit.add(game, buff);
	}

	public void updateLevelOfAura(CSimulation game, CUnit unit, int prevLevel, int curLevel) {
		if (updateAuraLevelActions != null) {
			for (ABAction action : updateAuraLevelActions) {
				action.runAction(game, unit, localStore, 0);
			}
		}
	}

	public void removeUnitFromAura(CSimulation game, CUnit unit) {
		unit.remove(game, buff);
		if (removeFromAuraActions != null) {
			for (ABAction action : removeFromAuraActions) {
				action.runAction(game, unit, localStore, 0);
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

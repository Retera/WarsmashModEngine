package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.template;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABGenericAuraBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.template.ABMeleeRangeTargetOverride;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.template.ABStatBuffFromDataField;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABAbilityBuilderStatAuraTemplate extends AbilityGenericSingleIconPassiveAbility {

	private List<ABAbilityBuilderAbilityTypeLevelData> levelData;
	private ABLocalDataStore localStore;

	private Set<CUnit> auraGroup;
	private int loopTick = 0;

	private static final Rectangle recycleRect = new Rectangle();
	private EnumSet<CTargetType> targetsAllowed = null;
	private float range = 0;

	private War3ID buffId;
	private CBuff buff;

	private SimulationRenderComponent fx;

	private boolean targetMelee = false;
	private boolean targetRange = false;
	private ABMeleeRangeTargetOverride rangeOverride;

	private String auraStackingKey;

	private List<ABStatBuffFromDataField> statBuffDataFields;

	private final int LEAVE_GROUP_TICKS = (int) (3 / WarsmashConstants.SIMULATION_STEP_TIME);
	private final int ENTER_GROUP_TICKS = (int) (0.4 / WarsmashConstants.SIMULATION_STEP_TIME);
	private final int RESET_GROUP_TICKS = LEAVE_GROUP_TICKS * 2;
	private boolean leveled;

	public ABAbilityBuilderStatAuraTemplate(int handleId, War3ID code, War3ID alias,
			List<ABAbilityBuilderAbilityTypeLevelData> levelData, ABLocalDataStore localStore,
			List<ABStatBuffFromDataField> statBuffDataFields, ABMeleeRangeTargetOverride meleeRangeTargetOverride) {
		super(code, alias, handleId);
		this.levelData = levelData;
		this.localStore = localStore;
		this.rangeOverride = meleeRangeTargetOverride;
		this.targetsAllowed = levelData.get(getLevel() - 1).getTargetsAllowed();
		this.range = levelData.get(getLevel() - 1).getArea();
		this.auraStackingKey = "";

		this.statBuffDataFields = new ArrayList<>();
		for (ABStatBuffFromDataField statBuff : statBuffDataFields) {
			this.statBuffDataFields.add(new ABStatBuffFromDataField(statBuff));
		}

		if (!this.levelData.get(getLevel() - 1).getBuffs().isEmpty()) {
			buffId = this.levelData.get(getLevel() - 1).getBuffs().get(0);
			this.auraStackingKey = buffId.asStringValue();
		}

		for (ABStatBuffFromDataField statBuff : this.statBuffDataFields) {
			createNewBuffs(statBuff);
		}

		if (this.rangeOverride != null) {
			this.targetMelee = this.rangeOverride.isTargetMelee();
			this.targetRange = this.rangeOverride.isTargetRange();
		}

		this.leveled = levelData.size() > 1;
	}

	private void removeExistingBuffs(CSimulation game, ABStatBuffFromDataField statBuff) {
		for (CUnit unit : auraGroup) {
			unit.removeNonStackingStatBuff(game, statBuff.getBuff());
			if (statBuff.getSecondAtkBuff() != null) {
				unit.removeNonStackingStatBuff(game, statBuff.getSecondAtkBuff());
			}
		}
	}

	private void addNewBuffs(CSimulation game, ABStatBuffFromDataField statBuff) {
		for (CUnit unit : auraGroup) {
			unit.addNonStackingStatBuff(game, statBuff.getBuff());
			if (statBuff.getSecondAtkBuff() != null) {
				unit.addNonStackingStatBuff(game, statBuff.getSecondAtkBuff());
			}
		}
	}

	private void createNewBuffs(ABStatBuffFromDataField parsedBuff) {
		NonStackingStatBuffType type = parsedBuff.convertToNonStackingType(levelData.get(getLevel() - 1));
		NonStackingStatBuff newBuff = null;
		switch (type) {
		case RNGDATK:
		case RNGDATKPCT:
			newBuff = new NonStackingStatBuff(type, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setBuff(newBuff);
			targetRange = true;
			break;
		case MELEEATK:
		case MELEEATKPCT:
			newBuff = new NonStackingStatBuff(type, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setBuff(newBuff);
			targetMelee = true;
			break;
		case ALLATK:
			newBuff = new NonStackingStatBuff(NonStackingStatBuffType.MELEEATK, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setBuff(newBuff);
			newBuff = new NonStackingStatBuff(NonStackingStatBuffType.RNGDATK, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setSecondAtkBuff(newBuff);
			targetMelee = true;
			targetRange = true;
			break;
		case ALLATKPCT:
			newBuff = new NonStackingStatBuff(NonStackingStatBuffType.MELEEATKPCT, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setBuff(newBuff);
			newBuff = new NonStackingStatBuff(NonStackingStatBuffType.RNGDATKPCT, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setSecondAtkBuff(newBuff);
			targetMelee = true;
			targetRange = true;
			break;
		default:
			newBuff = new NonStackingStatBuff(type, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setBuff(newBuff);
			targetMelee = true;
			targetRange = true;
		}
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		this.buff.setLevel(game, unit, level);
		targetsAllowed = levelData.get(getLevel() - 1).getTargetsAllowed();
		range = levelData.get(getLevel() - 1).getArea();
		this.targetMelee = false;
		this.targetRange = false;
		for (ABStatBuffFromDataField statBuff : this.statBuffDataFields) {
			NonStackingStatBuffType type = statBuff.convertToNonStackingType(levelData.get(getLevel() - 1));
			if (type == null) {
				continue;
			}
			switch (type) {
			case ALLATK:
				if (statBuff.getSecondAtkBuff() == null) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
					addNewBuffs(game, statBuff);
				} else if (statBuff.getBuff().getBuffType() != NonStackingStatBuffType.MELEEATK) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
					addNewBuffs(game, statBuff);
				} else {
					statBuff.getBuff().setValue(Float.parseFloat(
							levelData.get(getLevel() - 1).getData().get(statBuff.getDataField().getIndex())));
					targetMelee = true;
					targetRange = true;
				}
				break;
			case ALLATKPCT:
				if (statBuff.getSecondAtkBuff() == null) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
					addNewBuffs(game, statBuff);
				} else if (statBuff.getBuff().getBuffType() != NonStackingStatBuffType.MELEEATKPCT) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
					addNewBuffs(game, statBuff);
				} else {
					statBuff.getBuff().setValue(Float.parseFloat(
							levelData.get(getLevel() - 1).getData().get(statBuff.getDataField().getIndex())));
					targetMelee = true;
					targetRange = true;
				}
				break;
			default:
				if (statBuff.getSecondAtkBuff() != null) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
					addNewBuffs(game, statBuff);
				} else if (type != statBuff.getBuff().getBuffType()) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
					addNewBuffs(game, statBuff);
				} else {
					float parsedFloat;
					try {
						parsedFloat = Float.parseFloat(
								levelData.get(getLevel() - 1).getData().get(statBuff.getDataField().getIndex()));
					} catch (NumberFormatException exc) {
						parsedFloat = 0;
					}
					statBuff.getBuff().setValue(parsedFloat);
					if (type == NonStackingStatBuffType.MELEEATK || type == NonStackingStatBuffType.MELEEATKPCT) {
						targetMelee = true;
					} else if (type == NonStackingStatBuffType.RNGDATK || type == NonStackingStatBuffType.RNGDATKPCT) {
						targetRange = true;
					} else {
						targetMelee = true;
						targetRange = true;
					}
				}
			}
			for (CUnit unitA : auraGroup) {
				if (statBuff.getBuff().getBuffType().isHeroStat()) {
					unitA.computeDerivedHeroFields(game, statBuff.getBuff().getBuffType());
				} else {
					unitA.computeDerivedFields(statBuff.getBuff().getBuffType());
				}
			}
		}
		if (this.rangeOverride != null) {
			this.targetMelee = this.rangeOverride.isTargetMelee();
			this.targetRange = this.rangeOverride.isTargetRange();
		}
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		if (this.buffId != null) {
			this.buff = new ABGenericAuraBuff(game.getHandleIdAllocator().createId(), this.buffId,
					localStore, this, unit,
					this.leveled, true);
		}
		game.getAbilityData().createAbility(getAlias(), game.getHandleIdAllocator().createId());
		auraGroup = new HashSet<>();
		localStore.put(ABLocalStoreKeys.AURAGROUP, auraGroup);
		this.fx = game.createPersistentSpellEffectOnUnit(unit, this.getAlias(), CEffectType.TARGET);
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		emptyAura(game, unit);
		this.fx.remove();
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		if (loopTick % LEAVE_GROUP_TICKS == 0) {
			List<CUnit> unitList = new ArrayList<>(auraGroup);
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
							&& !auraGroup.contains(enumUnit)
							&& ((targetMelee && enumUnit.isUnitType(CUnitTypeJass.MELEE_ATTACKER)
									|| (targetRange && enumUnit.isUnitType(CUnitTypeJass.RANGED_ATTACKER))))) {
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
		for (ABStatBuffFromDataField statBuff : this.statBuffDataFields) {
			unit.addNonStackingStatBuff(game, statBuff.getBuff());
			if (statBuff.getSecondAtkBuff() != null) {
				unit.addNonStackingStatBuff(game, statBuff.getSecondAtkBuff());
			}
		}
		if (buff != null) {
			unit.addNonStackingDisplayBuff(game, auraStackingKey, buff);
		}
		auraGroup.add(unit);
	}

	public void removeUnitFromAura(CSimulation game, CUnit unit) {
		if (buff != null) {
			unit.removeNonStackingDisplayBuff(game, auraStackingKey, buff);
		}
		for (ABStatBuffFromDataField statBuff : this.statBuffDataFields) {
			unit.removeNonStackingStatBuff(game, statBuff.getBuff());
			if (statBuff.getSecondAtkBuff() != null) {
				unit.removeNonStackingStatBuff(game, statBuff.getSecondAtkBuff());
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

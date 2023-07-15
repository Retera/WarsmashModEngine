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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABGenericAuraBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.MeleeRangeTargetOverride;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.StatBuffFromDataField;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.StatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityAbilityBuilderStatAuraTemplate extends AbstractGenericSingleIconActiveAbility {

	private List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private Map<String, Object> localStore;

	private Set<CUnit> auraGroup;
	private int loopTick = 0;

	private static final Rectangle recycleRect = new Rectangle();
	private EnumSet<CTargetType> targetsAllowed = null;
	private float range = 0;

	private War3ID buffId;
	private CBuff buff;

	private boolean targetMelee = false;
	private boolean targetRange = false;
	private MeleeRangeTargetOverride rangeOverride;

	private String auraStackingKey;

	private List<StatBuffFromDataField> statBuffDataFields;

	private final int LEAVE_GROUP_TICKS = (int) (3 / WarsmashConstants.SIMULATION_STEP_TIME);
	private final int ENTER_GROUP_TICKS = (int) (0.4 / WarsmashConstants.SIMULATION_STEP_TIME);
	private final int RESET_GROUP_TICKS = LEAVE_GROUP_TICKS * 2;

	public CAbilityAbilityBuilderStatAuraTemplate(int handleId, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, Map<String, Object> localStore,
			List<StatBuffFromDataField> statBuffDataFields, MeleeRangeTargetOverride meleeRangeTargetOverride) {
		super(handleId, alias);
		this.levelData = levelData;
		this.localStore = localStore;
		this.statBuffDataFields = statBuffDataFields;
		this.rangeOverride = meleeRangeTargetOverride;
		this.targetsAllowed = levelData.get(getLevel() - 1).getTargetsAllowed();
		this.range = levelData.get(getLevel() - 1).getArea();
		this.auraStackingKey = "";

		if (!this.levelData.get(getLevel() - 1).getBuffs().isEmpty()) {
			buffId = this.levelData.get(getLevel() - 1).getBuffs().get(0);
			this.auraStackingKey = buffId.asStringValue();
		}

		for (StatBuffFromDataField statBuff : statBuffDataFields) {
			createNewBuffs(statBuff);
		}

		if (this.rangeOverride != null) {
			this.targetMelee = this.rangeOverride.isTargetMelee();
			this.targetRange = this.rangeOverride.isTargetRange();
		}
	}

	private NonStackingStatBuffType convertToNonStackingType(StatBuffFromDataField statBuff) {
		boolean percentage = false;
		if (statBuff.isPercentageOverride() != null) {
			percentage = statBuff.isPercentageOverride();
		} else {
			if (statBuff.getPercentageBooleanField() != null) {
				percentage = Integer.parseInt(
						levelData.get(getLevel() - 1).getData().get(statBuff.getPercentageBooleanField().getIndex())) == 1;
			} else if (statBuff.getFlatBooleanField() != null) {
				percentage = !(Integer.parseInt(
						levelData.get(getLevel() - 1).getData().get(statBuff.getFlatBooleanField().getIndex())) == 1);
			}
		}
		if (statBuff.getType() == StatBuffType.ATK) {
			boolean targetMelee = false;
			boolean targetRange = false;
			System.out.println("Target Melee Field: " + statBuff.getTargetMeleeField());
			if (statBuff.getTargetMeleeField() != null) {
				targetMelee = Integer.parseInt(
						levelData.get(getLevel() - 1).getData().get(statBuff.getTargetMeleeField().getIndex())) == 1;
				System.out.println("Target Melee PreParse: " + levelData.get(getLevel() - 1).getData().get(statBuff.getTargetMeleeField().getIndex()));
			}
			System.out.println("Target Melee Value: " + targetMelee);
			System.out.println("Target Range Field: " + statBuff.getTargetRangeField());
			if (statBuff.getTargetRangeField() != null) {
				targetRange = Integer.parseInt(
						levelData.get(getLevel() - 1).getData().get(statBuff.getTargetRangeField().getIndex())) == 1;
				System.out.println("Target Range PreParse: " + levelData.get(getLevel() - 1).getData().get(statBuff.getTargetRangeField().getIndex()));
			}
			System.out.println("Target Range Value: " + targetRange);

			return statBuff.getType().toAtkNonStackingStatBuffType(percentage, targetMelee, targetRange);
		} else {
			return statBuff.getType().toNonStackingStatBuffType(percentage);
		}
	}

	private void removeExistingBuffs(StatBuffFromDataField statBuff) {
		for (CUnit unit : auraGroup) {
			unit.removeNonStackingBuff(statBuff.getBuff());
			if (statBuff.getSecondAtkBuff() != null) {
				unit.removeNonStackingBuff(statBuff.getSecondAtkBuff());
			}
		}
	}

	private void createNewBuffs(StatBuffFromDataField parsedBuff) {
		NonStackingStatBuffType type = convertToNonStackingType(parsedBuff);
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
	public void setLevel(int level) {
		super.setLevel(level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
		targetsAllowed = levelData.get(getLevel() - 1).getTargetsAllowed();
		range = levelData.get(getLevel() - 1).getArea();
		this.targetMelee = false;
		this.targetRange = false;
		for (StatBuffFromDataField statBuff : this.statBuffDataFields) {
			NonStackingStatBuffType type = convertToNonStackingType(statBuff);
			if (type == null) {
				continue;
			}
			switch (type) {
			case ALLATK:
				if (statBuff.getSecondAtkBuff() == null) {
					removeExistingBuffs(statBuff);
					createNewBuffs(statBuff);
				} else if (statBuff.getBuff().getBuffType() != NonStackingStatBuffType.MELEEATK) {
					removeExistingBuffs(statBuff);
					createNewBuffs(statBuff);
				} else {
					statBuff.getBuff().setValue(Float.parseFloat(
							levelData.get(getLevel() - 1).getData().get(statBuff.getDataField().getIndex())));
					targetMelee = true;
					targetRange = true;
				}
				break;
			case ALLATKPCT:
				if (statBuff.getSecondAtkBuff() == null) {
					removeExistingBuffs(statBuff);
					createNewBuffs(statBuff);
				} else if (statBuff.getBuff().getBuffType() != NonStackingStatBuffType.MELEEATKPCT) {
					removeExistingBuffs(statBuff);
					createNewBuffs(statBuff);
				} else {
					statBuff.getBuff().setValue(Float.parseFloat(
							levelData.get(getLevel() - 1).getData().get(statBuff.getDataField().getIndex())));
					targetMelee = true;
					targetRange = true;
				}
				break;
			default:
				if (statBuff.getSecondAtkBuff() != null) {
					removeExistingBuffs(statBuff);
					createNewBuffs(statBuff);
				} else if (type != statBuff.getBuff().getBuffType()) {
					removeExistingBuffs(statBuff);
					createNewBuffs(statBuff);
				} else {
					statBuff.getBuff().setValue(Float.parseFloat(
							levelData.get(getLevel() - 1).getData().get(statBuff.getDataField().getIndex())));
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
			for (CUnit unit : auraGroup) {
				unit.computeDerivedFields(statBuff.getBuff().getBuffType());
			}
		}
		if (this.rangeOverride != null) {
			this.targetMelee = this.rangeOverride.isTargetMelee();
			this.targetRange = this.rangeOverride.isTargetRange();
		}
		System.out.println("Levelled Aura " + this.getAlias().asStringValue() + " to level " + getLevel());
		System.out.println("Current Targets Melee? " + this.targetMelee);
		System.out.println("Current Targets Range? " + this.targetRange);
		System.out.println("Current Key: " + auraStackingKey);
		System.out.println("Current Range: " + range);
		System.out.println("Current Number of buffs: " + statBuffDataFields.size());
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		if (this.buffId != null) {
			this.buff = new ABGenericAuraBuff(game.getHandleIdAllocator().createId(), this.buffId, unit,
					this.getAlias());
		}
		game.getAbilityData().createAbility(getAlias(), game.getHandleIdAllocator().createId());
		auraGroup = new HashSet<>();
		localStore.put(ABLocalStoreKeys.AURAGROUP, auraGroup);

		System.out.println("Added Aura " + this.getAlias().asStringValue());
		System.out.println("Current Targets Melee? " + this.targetMelee);
		System.out.println("Current Targets Range? " + this.targetRange);
		System.out.println("Current Key: " + auraStackingKey);
		System.out.println("Current Range: " + range);
		System.out.println("Current Number of buffs: " + statBuffDataFields.size());
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		emptyAura(game, unit);
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
		auraGroup.add(unit);
		for (StatBuffFromDataField statBuff : this.statBuffDataFields) {
			unit.addNonStackingBuff(statBuff.getBuff());
		}
		unit.add(game, buff);
	}

	public void removeUnitFromAura(CSimulation game, CUnit unit) {
		unit.remove(game, buff);
		for (StatBuffFromDataField statBuff : this.statBuffDataFields) {
			unit.removeNonStackingBuff(statBuff.getBuff());
		}
		auraGroup.remove(unit);
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
		emptyAura(game, unit);
	}

	@Override
	public int getBaseOrderId() {
		return 0;
	}

	@Override
	public boolean isToggleOn() {
		return false;
	}

	// Unneeded Methods
	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		return null;
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

}

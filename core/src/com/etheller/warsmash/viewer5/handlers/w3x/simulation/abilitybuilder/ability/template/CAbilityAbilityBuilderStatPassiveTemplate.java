package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.template;

import java.util.List;
import java.util.Map;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABPermanentPassiveBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.StatBuffFromDataField;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;

public class CAbilityAbilityBuilderStatPassiveTemplate extends AbilityGenericSingleIconPassiveAbility {

	private List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private Map<String, Object> localStore;

	private War3ID buffId;
	private CBuff buff;

	private CUnit caster;

	private String auraStackingKey;

	private List<StatBuffFromDataField> statBuffDataFields;

	public CAbilityAbilityBuilderStatPassiveTemplate(int handleId, War3ID code, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, Map<String, Object> localStore,
			List<StatBuffFromDataField> statBuffDataFields) {
		super(code, alias, handleId);
		this.levelData = levelData;
		this.localStore = localStore;
		this.statBuffDataFields = statBuffDataFields;
		this.auraStackingKey = "";

		if (!this.levelData.get(getLevel() - 1).getBuffs().isEmpty()) {
			buffId = this.levelData.get(getLevel() - 1).getBuffs().get(0);
			this.auraStackingKey = buffId.asStringValue();
		}

		for (StatBuffFromDataField statBuff : statBuffDataFields) {
			createNewBuffs(statBuff);
		}
	}

	private void removeExistingBuffs(StatBuffFromDataField statBuff) {
		this.caster.removeNonStackingStatBuff(statBuff.getBuff());
		if (statBuff.getSecondAtkBuff() != null) {
			this.caster.removeNonStackingStatBuff(statBuff.getSecondAtkBuff());
		}
	}

	private void createNewBuffs(StatBuffFromDataField parsedBuff) {
		NonStackingStatBuffType type = parsedBuff.convertToNonStackingType(levelData.get(getLevel() - 1));
		NonStackingStatBuff newBuff = null;
		switch (type) {
		case RNGDATK:
		case RNGDATKPCT:
			newBuff = new NonStackingStatBuff(type, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setBuff(newBuff);
			break;
		case MELEEATK:
		case MELEEATKPCT:
			newBuff = new NonStackingStatBuff(type, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setBuff(newBuff);
			break;
		case ALLATK:
			newBuff = new NonStackingStatBuff(NonStackingStatBuffType.MELEEATK, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setBuff(newBuff);
			newBuff = new NonStackingStatBuff(NonStackingStatBuffType.RNGDATK, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setSecondAtkBuff(newBuff);
			break;
		case ALLATKPCT:
			newBuff = new NonStackingStatBuff(NonStackingStatBuffType.MELEEATKPCT, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setBuff(newBuff);
			newBuff = new NonStackingStatBuff(NonStackingStatBuffType.RNGDATKPCT, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setSecondAtkBuff(newBuff);
			break;
		default:
			newBuff = new NonStackingStatBuff(type, this.auraStackingKey, Float
					.parseFloat(levelData.get(getLevel() - 1).getData().get(parsedBuff.getDataField().getIndex())));
			parsedBuff.setBuff(newBuff);
		}
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
		for (StatBuffFromDataField statBuff : this.statBuffDataFields) {
			NonStackingStatBuffType type = statBuff.convertToNonStackingType(levelData.get(getLevel() - 1));
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
				}
			}
			caster.computeDerivedFields(statBuff.getBuff().getBuffType());
		}
		if (this.buff != null) {
			this.buff.setLevel(game, unit, level);
		}
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		if (this.buffId != null) {
			this.buff = new ABPermanentPassiveBuff(game.getHandleIdAllocator().createId(), this.buffId, localStore, null, null, 0);
			unit.addNonStackingDisplayBuff(game, auraStackingKey, buff);
		}
		this.caster = unit;
		game.getAbilityData().createAbility(getAlias(), game.getHandleIdAllocator().createId());

		for (StatBuffFromDataField statBuff : this.statBuffDataFields) {
			unit.addNonStackingStatBuff(statBuff.getBuff());
		}
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		removeBuffFromUnit(game, unit);
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
	}

	public void removeBuffFromUnit(CSimulation game, CUnit unit) {
		if (this.buff != null) {
			unit.removeNonStackingDisplayBuff(game, auraStackingKey, buff);
		}
		for (StatBuffFromDataField statBuff : this.statBuffDataFields) {
			unit.removeNonStackingStatBuff(statBuff.getBuff());
		}
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
		removeBuffFromUnit(game, unit);
	}

}

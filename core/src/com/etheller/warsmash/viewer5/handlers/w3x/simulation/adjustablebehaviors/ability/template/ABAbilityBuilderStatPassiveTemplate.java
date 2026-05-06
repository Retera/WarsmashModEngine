package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.template;

import java.util.List;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.ABPermanentPassiveBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.template.ABStatBuffFromDataField;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;

public class ABAbilityBuilderStatPassiveTemplate extends AbilityGenericSingleIconPassiveAbility {

	private List<ABAbilityBuilderAbilityTypeLevelData> levelData;
	private ABLocalDataStore localStore;

	private War3ID buffId;
	private CBuff buff;

	private CUnit caster;

	private String auraStackingKey;

	private List<ABStatBuffFromDataField> statBuffDataFields;

	public ABAbilityBuilderStatPassiveTemplate(int handleId, War3ID code, War3ID alias,
			List<ABAbilityBuilderAbilityTypeLevelData> levelData, ABLocalDataStore localStore,
			List<ABStatBuffFromDataField> statBuffDataFields) {
		super(code, alias, handleId);
		this.levelData = levelData;
		this.localStore = localStore;
		this.statBuffDataFields = statBuffDataFields;
		this.auraStackingKey = "";

		if (!this.levelData.get(getLevel() - 1).getBuffs().isEmpty()) {
			buffId = this.levelData.get(getLevel() - 1).getBuffs().get(0);
			this.auraStackingKey = buffId.asStringValue();
		}

		for (ABStatBuffFromDataField statBuff : statBuffDataFields) {
			createNewBuffs(statBuff);
		}
	}

	private void removeExistingBuffs(CSimulation game, ABStatBuffFromDataField statBuff) {
		this.caster.removeNonStackingStatBuff(game, statBuff.getBuff());
		if (statBuff.getSecondAtkBuff() != null) {
			this.caster.removeNonStackingStatBuff(game, statBuff.getSecondAtkBuff());
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
				} else if (statBuff.getBuff().getBuffType() != NonStackingStatBuffType.MELEEATK) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
				} else {
					statBuff.getBuff().setValue(Float.parseFloat(
							levelData.get(getLevel() - 1).getData().get(statBuff.getDataField().getIndex())));
				}
				break;
			case ALLATKPCT:
				if (statBuff.getSecondAtkBuff() == null) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
				} else if (statBuff.getBuff().getBuffType() != NonStackingStatBuffType.MELEEATKPCT) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
				} else {
					statBuff.getBuff().setValue(Float.parseFloat(
							levelData.get(getLevel() - 1).getData().get(statBuff.getDataField().getIndex())));
				}
				break;
			default:
				if (statBuff.getSecondAtkBuff() != null) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
				} else if (type != statBuff.getBuff().getBuffType()) {
					removeExistingBuffs(game, statBuff);
					createNewBuffs(statBuff);
				} else {
					statBuff.getBuff().setValue(Float.parseFloat(
							levelData.get(getLevel() - 1).getData().get(statBuff.getDataField().getIndex())));
				}
			}
			if (statBuff.getBuff().getBuffType().isHeroStat()) {
				caster.computeDerivedHeroFields(game, statBuff.getBuff().getBuffType());
			} else {
				caster.computeDerivedFields(statBuff.getBuff().getBuffType());
			}
		}
		if (this.buff != null) {
			this.buff.setLevel(game, unit, level);
		}
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		if (this.buffId != null) {
			this.buff = new ABPermanentPassiveBuff(game.getHandleIdAllocator().createId(), this.buffId, this, unit,
					localStore, null, null, 0, false, true);
			unit.addNonStackingDisplayBuff(game, auraStackingKey, buff);
		}
		this.caster = unit;
		game.getAbilityData().createAbility(getAlias(), game.getHandleIdAllocator().createId());

		for (ABStatBuffFromDataField statBuff : this.statBuffDataFields) {
			unit.addNonStackingStatBuff(game, statBuff.getBuff());
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
		for (ABStatBuffFromDataField statBuff : this.statBuffDataFields) {
			unit.removeNonStackingStatBuff(game, statBuff.getBuff());
		}
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
		removeBuffFromUnit(game, unit);
	}

}

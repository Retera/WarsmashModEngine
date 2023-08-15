package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.template;

import java.util.List;
import java.util.Map;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABGenericPassiveBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.StatBuffFromDataField;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityAbilityBuilderStatPassiveTemplate extends AbstractGenericSingleIconActiveAbility {

	private List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private Map<String, Object> localStore;

	private War3ID buffId;
	private CBuff buff;

	private CUnit caster;

	private String auraStackingKey;

	private List<StatBuffFromDataField> statBuffDataFields;

	public CAbilityAbilityBuilderStatPassiveTemplate(int handleId, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, Map<String, Object> localStore,
			List<StatBuffFromDataField> statBuffDataFields) {
		super(handleId, alias);
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
	public void setLevel(int level) {
		super.setLevel(level);
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
			this.buff.setLevel(level);
		}
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		if (this.buffId != null) {
			this.buff = new ABGenericPassiveBuff(game.getHandleIdAllocator().createId(), this.buffId);
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

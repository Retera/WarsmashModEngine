package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityAbilityBuilderActivePointTargetSimple extends CAbilityPointTargetSpellBase {

	List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private AbilityBuilderConfiguration config;
	private Map<String, Object> localStore;
	private int orderId;

	private int castId = 0;

	public CAbilityAbilityBuilderActivePointTargetSimple(int handleId, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, alias);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		orderId = OrderIdUtils.getOrderId(config.getCastId());
	}

	@Override
	public void setLevel(int level) {
		super.setLevel(level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
	}

	@Override
	public int getBaseOrderId() {
		return orderId;
	}

	@Override
	public void populateData(MutableGameObject worldEditorAbility, int level) {

	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit unit, AbilityTarget target) {
		this.castId++;
		if (config.getOnBeginCasting() != null) {
			for (ABAction action : config.getOnBeginCasting()) {
				action.runAction(simulation, unit, localStore, castId);
			}
		}
		return false;
	}

	@Override
	protected void innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		if (config.getExtraCastConditions() != null) {
			boolean result = true;
			for (ABCondition condition : config.getExtraCastConditions()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			if (result) {
				receiver.useOk();
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					receiver.activationCheckFailed(failReason);
				} else {
					receiver.unknownReasonUseNotOk();
				}
			}
		} else {
			receiver.useOk();
		}
	}

	@Override
	protected void innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (this.config.getExtraTargetConditions() != null) {
			localStore.put(ABLocalStoreKeys.ABILITYTARGETEDLOCATION+this.castId, target);
			boolean result = true;
			for (ABCondition condition : config.getExtraTargetConditions()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			if (result) {
				receiver.targetOk(target);
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					receiver.targetCheckFailed(failReason);
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THERE);
				}
			}
			localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDLOCATION+this.castId);
		} else {
			receiver.targetOk(target);
		}
	}

}

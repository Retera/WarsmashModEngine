package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;

public class CAbilityAbilityBuilderActivePointTargetSimple extends CAbilityPointTargetSpellBase {

	List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private AbilityBuilderConfiguration config;
	private Map<String, Object> localStore;
	private int orderId;

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
		if (config.getOnBeginCasting() != null) {
			for (ABAction action : config.getOnBeginCasting()) {
				action.runAction(simulation, unit, localStore);
			}
		}
		return false;
	}

}

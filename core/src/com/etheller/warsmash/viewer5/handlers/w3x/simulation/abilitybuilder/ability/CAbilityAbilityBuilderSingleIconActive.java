package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParser;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityAbilityBuilderSingleIconActive extends AbstractGenericSingleIconActiveAbility {

	List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private AbilityBuilderParser parser;
	private Map<String, Object> localStore;

	public CAbilityAbilityBuilderSingleIconActive(int handleId, War3ID alias, List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderParser parser, Map<String, Object> localStore) {
		super(handleId, alias);
		this.levelData = levelData;
		this.parser = parser;
		this.localStore = localStore;
	}
	
	@Override
	public void setLevel(int level) {
		super.setLevel(level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
	}

	@Override
	public int getBaseOrderId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isToggleOn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void innerCheckCanSmartTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		// TODO Auto-generated method stub
		
	}

}

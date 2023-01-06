package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParser;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityAbilityBuilderPassive extends AbstractGenericSingleIconActiveAbility {

	List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private AbilityBuilderParser parser;
	private Map<String, Object> localStore;

	public CAbilityAbilityBuilderPassive(int handleId, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderParser parser,
			Map<String, Object> localStore) {
		super(handleId, alias);
		this.levelData = levelData;
		this.parser = parser;
		this.localStore = localStore;
	}

	@Override
	public void setLevel(int level) {
		super.setLevel(level);
		System.err.println("Set passive level to " + level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		System.err.println("Added ability");
		if (parser.getOnAddAbility() != null) {
			System.err.println("looping over " + parser.getOnAddAbility().size() + " items");
			for (ABAction action : parser.getOnAddAbility()) {
				action.runAction(game, unit, localStore);
			}
		}
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		if (parser.getOnRemoveAbility() != null) {
			for (ABAction action : parser.getOnRemoveAbility()) {
				action.runAction(game, unit, localStore);
			}
		}
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		if (parser.getOnTickPreCast() != null) {
			for (ABAction action : parser.getOnTickPreCast()) {
				action.runAction(game, unit, localStore);
			}
		}
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
		if (parser.getOnDeathPreCast() != null) {
			for (ABAction action : parser.getOnDeathPreCast()) {
				action.runAction(game, unit, localStore);
			}
		}
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

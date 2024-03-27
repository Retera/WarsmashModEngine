package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

@Deprecated
public class CAbilityAbilityBuilderActiveUnitTargetSimple extends CAbilityTargetSpellBase {

	List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private AbilityBuilderConfiguration config;
	private Map<String, Object> localStore;
	private int orderId;
	private int autoCastOnId = 0;
	private int autoCastOffId = 0;
	private boolean autocasting = false;
	private boolean initialized;

	private int castId = 0;

	public CAbilityAbilityBuilderActiveUnitTargetSimple(int handleId, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, alias);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		orderId = OrderIdUtils.getOrderId(config.getCastId());
		if (config.getAutoCastOnId() != null) {
			autoCastOnId = OrderIdUtils.getOrderId(config.getAutoCastOnId());
		}
		if (config.getAutoCastOffId() != null) {
			autoCastOffId = OrderIdUtils.getOrderId(config.getAutoCastOffId());
		}
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
	}

	@Override
	public void populateData(GameObject worldEditorAbility, int level) {
		if (this.initialized) {
			if (config.getOnLevelChange() != null) {
				CSimulation game = (CSimulation) this.localStore.get(ABLocalStoreKeys.GAME);
				CUnit unit = (CUnit) this.localStore.get(ABLocalStoreKeys.THISUNIT);
				for (ABAction action : config.getOnLevelChange()) {
					action.runAction(game, unit, this.localStore, castId);
				}
			}
		}
		this.initialized = true;
	}

	@Override
	public int getBaseOrderId() {
		return this.orderId;
	}

	public List<CAbilityTypeAbilityBuilderLevelData> getLevelData() {
		return this.levelData;
	}

	public AbilityBuilderConfiguration getConfig() {
		return this.config;
	}

	public Map<String, Object> getLocalStore() {
		return this.localStore;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		super.onAdd(game, unit);
		localStore.put(ABLocalStoreKeys.GAME, game);
		localStore.put(ABLocalStoreKeys.THISUNIT, unit);
		if (config.getOnAddAbility() != null) {
			for (ABAction action : config.getOnAddAbility()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit unit, AbilityTarget target) {
		this.castId++;
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId, targetUnit);
			if (this.config.getOnBeginCasting() != null) {
				for (ABAction action : this.config.getOnBeginCasting()) {
					action.runAction(simulation, unit, this.localStore, castId);
				}
			}
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
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
	protected void innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target instanceof CUnit) {
			final CUnit targetUnit = (CUnit) target;
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId, targetUnit);

			if (this.config.getExtraTargetConditions() != null) {
				boolean result = true;
				for (ABCondition condition : config.getExtraTargetConditions()) {
					result = result && condition.evaluate(game, unit, localStore, castId);
				}
				if (result) {
					receiver.targetOk(targetUnit);
				} else {
					String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
					if (failReason != null) {
						receiver.targetCheckFailed(failReason);
					} else {
						receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT);
					}
				}
			} else {
				receiver.targetOk(targetUnit);
			}
		}
	}

	@Override
	public int getAutoCastOnOrderId() {
		return this.autoCastOnId;
	}

	@Override
	public int getAutoCastOffOrderId() {
		return this.autoCastOffId;
	}

	@Override
	public boolean isAutoCastOn() {
		return this.autocasting;
	}

	@Override
	public void setAutoCastOn(final CUnit caster, final boolean autoCastOn) {
		this.autocasting = autoCastOn;
	}

}

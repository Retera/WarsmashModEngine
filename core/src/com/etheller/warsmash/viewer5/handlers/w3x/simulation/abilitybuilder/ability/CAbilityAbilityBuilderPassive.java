package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;

public class CAbilityAbilityBuilderPassive extends AbilityGenericSingleIconPassiveAbility implements AbilityBuilderPassiveAbility {

	protected List<CAbilityTypeAbilityBuilderLevelData> levelData;
	protected AbilityBuilderConfiguration config;
	protected Map<String, Object> localStore;

	protected CItem item = null;
	
	protected float cooldown = 0;
	protected float area = 0;
	protected float range = 0;

	private War3ID onTooltipOverride = null;

	public CAbilityAbilityBuilderPassive(int handleId, War3ID code, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(code, alias, handleId);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		localStore.put(ABLocalStoreKeys.ABILITY, this);
	}

	protected void setSpellFields(CSimulation game, CUnit unit) {
		if (this.levelData.size() > 0) {
			CAbilityTypeAbilityBuilderLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
			this.cooldown = levelDataLevel.getCooldown();
			this.area = levelDataLevel.getArea();
			this.range = levelDataLevel.getCastRange();
		}
		if (this.config.getOverrideFields() != null) {
			if (this.config.getOverrideFields().getAreaOverride() != null) {
				this.area = this.config.getOverrideFields().getAreaOverride().callback(game, unit, localStore, 0);
			}
			if (this.config.getOverrideFields().getRangeOverride() != null) {
				this.range = this.config.getOverrideFields().getRangeOverride().callback(game, unit, localStore,
						0);
			}
			if (this.config.getOverrideFields().getCooldownOverride() != null) {
				this.cooldown = this.config.getOverrideFields().getCooldownOverride().callback(game, unit, localStore,
						0);
			}
			if (this.config.getOverrideFields().getOnTooltipOverride() != null) {
				this.onTooltipOverride = this.config.getOverrideFields().getOnTooltipOverride().callback(game, unit,
						localStore, 0);
			}
		}
	}
	
	@Override
	public int getAbilityIntField(String field) {
		GameObject editorData = (GameObject) localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA);
		return editorData.getFieldValue(field);
	}

	@Override
	public List<CAbilityTypeAbilityBuilderLevelData> getLevelData() {
		return this.levelData;
	}

	@Override
	public AbilityBuilderConfiguration getConfig() {
		return this.config;
	}

	@Override
	public Map<String, Object> getLocalStore() {
		return this.localStore;
	}

	@Override
	public float getArea() {
		return area;
	}

	@Override
	public float getCastRange() {
		return range;
	}

	@Override
	public float getCooldown() {
		return cooldown;
	}

	@Override
	public void startCooldown(CSimulation game, CUnit unit) {
		War3ID cdID = getCooldownId();
		if (cdID != War3ID.NONE) {
			unit.beginCooldown(game, cdID, this.cooldown);
		}
	}

	@Override
	public float getCooldownRemainingTicks(CSimulation game, CUnit unit) {
		War3ID cdID = getCooldownId();
		if (cdID != War3ID.NONE) {
			return unit.getCooldownRemainingTicks(game, cdID);
		}
		return unit.getCooldownRemainingTicks(game, this.getCode());
	}

	@Override
	public void resetCooldown(CSimulation game, CUnit unit) {
		War3ID cdID = getCooldownId();
		if (cdID != War3ID.NONE) {
			unit.beginCooldown(game, cdID, 0);
		}
	}

	private War3ID getCooldownId() {
		if (this.item != null) {
			if (item.getItemType().isIgnoreCooldown()) {
				return War3ID.NONE;
			} else {
				if (item.getItemType().getCooldownGroup() != null) {
					return item.getItemType().getCooldownGroup();
				}
			}
		}
		return getCode();
	}

	@Override
	public void setItemAbility(final CItem item, int slot) {
		this.item = item;
		this.localStore.put(ABLocalStoreKeys.ITEM, item);
		this.localStore.put(ABLocalStoreKeys.ITEMSLOT, slot);
	}

	@Override
	public CItem getItem() {
		return this.item;
	}

	@Override
	public War3ID getOnTooltipOverride() {
		return onTooltipOverride;
	}
	
	

	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
		setSpellFields(game, unit);
		if (config.getOnLevelChange() != null) {
			for (ABAction action : config.getOnLevelChange()) {
				action.runAction(game, unit, localStore, 0);
			}
		}
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		localStore.put(ABLocalStoreKeys.GAME, game);
		localStore.put(ABLocalStoreKeys.THISUNIT, unit);
		if (config.getOnAddAbility() != null) {
			for (ABAction action : config.getOnAddAbility()) {
				action.runAction(game, unit, localStore, 0);
			}
		}
	}

	@Override
	public void onAddDisabled(CSimulation game, CUnit unit) {
		localStore.put(ABLocalStoreKeys.GAME, game);
		localStore.put(ABLocalStoreKeys.THISUNIT, unit);
		setSpellFields(game, unit);
		if (config.getOnAddDisabledAbility() != null) {
			for (ABAction action : config.getOnAddDisabledAbility()) {
				action.runAction(game, unit, localStore, 0);
			}
		}
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		if (config.getOnRemoveAbility() != null) {
			for (ABAction action : config.getOnRemoveAbility()) {
				action.runAction(game, unit, localStore, 0);
			}
		}
	}

	@Override
	public void onRemoveDisabled(CSimulation game, CUnit unit) {
		if (config.getOnRemoveDisabledAbility() != null) {
			for (ABAction action : config.getOnRemoveDisabledAbility()) {
				action.runAction(game, unit, localStore, 0);
			}
		}
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
		if (config.getOnDeathPreCast() != null) {
			for (ABAction action : config.getOnDeathPreCast()) {
				action.runAction(game, unit, localStore, 0);
			}
		}
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public void checkRequirementsMet(CSimulation game, CUnit unit, AbilityActivationReceiver receiver) {
		if (this.levelData.size() > 0) {
			List<CUnitTypeRequirement> reqs = this.levelData.get(this.getLevel() - 1).getRequirements();
			CPlayer player = game.getPlayer(unit.getPlayerIndex());
			if (reqs != null) {
				for (final CUnitTypeRequirement requirement : reqs) {
					if (player.getTechtreeUnlocked(requirement.getRequirement()) < requirement.getRequiredLevel()) {
						receiver.missingRequirement(requirement.getRequirement(), requirement.getRequiredLevel());
					}
				}
			}
		}
	}

	@Override
	public boolean isRequirementsMet(CSimulation game, CUnit unit) {
		if (this.levelData.size() > 0) {
			List<CUnitTypeRequirement> reqs = this.levelData.get(this.getLevel() - 1).getRequirements();
			CPlayer player = game.getPlayer(unit.getPlayerIndex());
			boolean requirementsMet = player.isTechtreeAllowedByMax(this.getAlias());
			if (reqs != null) {
				for (final CUnitTypeRequirement requirement : reqs) {
					if (player.getTechtreeUnlocked(requirement.getRequirement()) < requirement.getRequiredLevel()) {
						requirementsMet = false;
					}
				}
			}
			return requirementsMet;
		}
		return true;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		final int cooldownRemaining = unit.getCooldownRemainingTicks(game, getCooldownId());

		if (cooldownRemaining > 0) {
			float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getCooldownId())
					* WarsmashConstants.SIMULATION_STEP_TIME;
			receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME,
					cooldownLengthDisplay);
		}
		
		super.innerCheckCanUse(game, unit, orderId, receiver);
	}

}

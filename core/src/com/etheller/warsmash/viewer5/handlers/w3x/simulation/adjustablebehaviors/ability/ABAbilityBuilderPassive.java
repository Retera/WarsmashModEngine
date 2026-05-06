package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;

public class ABAbilityBuilderPassive extends AbilityGenericSingleIconPassiveAbility
		implements ABAbilityBuilderPassiveAbility {

	protected List<ABAbilityBuilderAbilityTypeLevelData> levelData;
	protected ABAbilityBuilderConfiguration config;
	protected ABLocalDataStore localStore;

	protected CItem item = null;

	protected float cooldown = 0;
	protected float area = 0;
	protected float range = 0;
	private float castTime = 0;

	private War3ID onTooltipOverride = null;

	protected Set<String> uniqueFlags = null;

	private int visibleMenuId = 0;

	public ABAbilityBuilderPassive(int handleId, War3ID code, War3ID alias,
			List<ABAbilityBuilderAbilityTypeLevelData> levelData, ABAbilityBuilderConfiguration config,
			ABLocalDataStore localStore) {
		super(code, alias, handleId);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		localStore.originAbility = this;
		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
		final int levels = editorData.getFieldAsInteger(AbilityFields.LEVELS, 0);
		localStore.put(ABLocalStoreKeys.ISABILITYLEVELED, levels > 1);
	}

	private void addInitialUniqueFlags(CSimulation game, CUnit unit) {
		if (this.config.getInitialUniqueFlags() != null && !this.config.getInitialUniqueFlags().isEmpty()) {
			this.uniqueFlags = new HashSet<>();
			for (ABStringCallback flag : this.config.getInitialUniqueFlags()) {
				this.uniqueFlags.add(flag.callback(unit, localStore, ABConstants.NO_CAST_ID));
			}
		}
	}

	protected void setSpellFields(CSimulation game, CUnit unit) {
		if (this.levelData.size() > 0) {
			ABAbilityBuilderAbilityTypeLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
			this.cooldown = levelDataLevel.getCooldown();
			this.area = levelDataLevel.getArea();
			this.range = levelDataLevel.getCastRange();
		}
		if (this.config.getOverrideFields() != null) {
			if (this.config.getOverrideFields().getAreaOverride() != null) {
				this.area = this.config.getOverrideFields().getAreaOverride().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getRangeOverride() != null) {
				this.range = this.config.getOverrideFields().getRangeOverride().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getCastTimeOverride() != null) {
				this.castTime = this.config.getOverrideFields().getCastTimeOverride().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getCooldownOverride() != null) {
				this.cooldown = this.config.getOverrideFields().getCooldownOverride().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getOnTooltipOverride() != null) {
				this.onTooltipOverride = this.config.getOverrideFields().getOnTooltipOverride().callback(unit,
						localStore, ABConstants.NO_CAST_ID);
			}
		}
	}

	@Override
	public int getAbilityIntField(String field) {
		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
		return editorData.getFieldValue(field);
	}

	@Override
	public float getAbilityFloatField(String field) {
		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
		return editorData.getFieldFloatValue(field);
	}

	@Override
	public String getAbilityStringField(String field) {
		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
		return editorData.getField(field);
	}

	@Override
	public boolean getAbilityBooleanField(String field) {
		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
		return editorData.getFieldValue(field) != 0;
	}

	@Override
	public List<ABAbilityBuilderAbilityTypeLevelData> getLevelData() {
		return this.levelData;
	}

	@Override
	public ABAbilityBuilderConfiguration getConfig() {
		return this.config;
	}

	@Override
	public ABLocalDataStore getLocalStore() {
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

	public float getCastTime() {
		return castTime;
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
		this.localStore.originItem = item;
		this.localStore.put(ABLocalStoreKeys.ITEMSLOT, slot);
	}

	@Override
	public CItem getItem() {
		return this.item;
	}

	@Override
	public boolean hasUniqueFlag(String flag) {
		if (this.uniqueFlags != null) {
			return this.uniqueFlags.contains(flag);
		}
		return false;
	}

	public void addUniqueFlag(String flag) {
		if (this.uniqueFlags == null) {
			this.uniqueFlags = new HashSet<>();
		}
		this.uniqueFlags.add(flag);
	}

	public void removeUniqueFlag(String flag) {
		if (this.uniqueFlags != null) {
			this.uniqueFlags.remove(flag);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUniqueValue(String key, Class<T> cls) {
		Object o = this.localStore.get(ABLocalStoreKeys.combineUniqueValueKey(key, this.getHandleId()));
		if (o != null && o.getClass() == cls) {
			return (T) o;
		}
		return null;
	}

	public void addUniqueValue(Object item, String key) {
		this.localStore.put(ABLocalStoreKeys.combineUniqueValueKey(key, this.getHandleId()), item);
	}

	public void removeUniqueValue(String key) {
		this.localStore.remove(ABLocalStoreKeys.combineUniqueValueKey(key, this.getHandleId()));
	}

	@Override
	public War3ID getOnTooltipOverride() {
		return onTooltipOverride;
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		setSpellFields(game, unit);
		if (config.getOnLevelChange() != null) {
			for (ABAction action : config.getOnLevelChange()) {
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
			}
		}
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		localStore.game = game;
		localStore.originUnit = unit;
		localStore.originPlayer = game.getPlayer(unit.getPlayerIndex());
		if (config.getOnAddAbility() != null) {
			for (ABAction action : config.getOnAddAbility()) {
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
			}
		}
	}

	@Override
	public void onAddDisabled(CSimulation game, CUnit unit) {
		localStore.game = game;
		localStore.originUnit = unit;
		localStore.originPlayer = game.getPlayer(unit.getPlayerIndex());
		addInitialUniqueFlags(game, unit);
		setSpellFields(game, unit);
		if (config.getOnAddDisabledAbility() != null) {
			for (ABAction action : config.getOnAddDisabledAbility()) {
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
			}
		}
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		if (config.getOnRemoveAbility() != null) {
			for (ABAction action : config.getOnRemoveAbility()) {
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
			}
		}
	}

	@Override
	public void onRemoveDisabled(CSimulation game, CUnit unit) {
		if (config.getOnRemoveDisabledAbility() != null) {
			for (ABAction action : config.getOnRemoveDisabledAbility()) {
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
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
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
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

	@Override
	public int getIconVisibleMenuId() {
		return this.visibleMenuId;
	}

	@Override
	public void setIconVisibleMenuId(int menu) {
		this.visibleMenuId = menu;
	}

}

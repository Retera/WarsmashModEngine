package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityAbilityBuilderNoIcon extends AbstractGenericNoIconAbility implements AbilityBuilderPassiveAbility {

	protected List<CAbilityTypeAbilityBuilderLevelData> levelData;
	protected AbilityBuilderConfiguration config;
	protected Map<String, Object> localStore;

	protected CItem item = null;
	
	protected float cooldown = 0;
	protected float area = 0;
	protected float range = 0;
	protected float castTime = 0;
	
	protected Set<String> uniqueFlags = null;

	private int visibleMenuId = 0;

	public CAbilityAbilityBuilderNoIcon(int handleId, War3ID code, War3ID alias, List<CAbilityTypeAbilityBuilderLevelData> levelData,
			AbilityBuilderConfiguration config, Map<String, Object> localStore) {
		super(handleId, code, alias);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		localStore.put(ABLocalStoreKeys.ABILITY, this);
		GameObject editorData = (GameObject) localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA);
		final int levels = editorData.getFieldAsInteger(AbilityFields.LEVELS, 0);
		localStore.put(ABLocalStoreKeys.ISABILITYLEVELED, levels > 1);
		localStore.put(ABLocalStoreKeys.ISABILITYMAGIC, false);
		localStore.put(ABLocalStoreKeys.ISABILITYPHYSICAL, false);
	}
	
	private void addInitialUniqueFlags(CSimulation game, CUnit unit) {
		if (this.config.getInitialUniqueFlags() != null && !this.config.getInitialUniqueFlags().isEmpty()) {
			this.uniqueFlags = new HashSet<>();
			for (ABStringCallback flag : this.config.getInitialUniqueFlags()) {
				this.uniqueFlags.add(flag.callback(game, unit, localStore, 0));
			}
		}
	}

	protected void setSpellFields(CSimulation game, CUnit unit) {
		CAbilityTypeAbilityBuilderLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
		this.cooldown = levelDataLevel.getCooldown();
		this.area = levelDataLevel.getArea();
		this.range = levelDataLevel.getCastRange();
		if (this.config.getOverrideFields() != null) {
			if (this.config.getOverrideFields().getAreaOverride() != null) {
				this.area = this.config.getOverrideFields().getAreaOverride().callback(game, unit, localStore, 0);
			}
			if (this.config.getOverrideFields().getRangeOverride() != null) {
				this.range = this.config.getOverrideFields().getRangeOverride().callback(game, unit, localStore,
						0);
			}
			if (this.config.getOverrideFields().getCastTimeOverride() != null) {
				this.castTime = this.config.getOverrideFields().getCastTimeOverride().callback(game, unit, localStore,
						0);
			}
			if (this.config.getOverrideFields().getCooldownOverride() != null) {
				this.cooldown = this.config.getOverrideFields().getCooldownOverride().callback(game, unit, localStore,
						0);
			}
		}
	}
	
	@Override
	public int getAbilityIntField(String field) {
		GameObject editorData = (GameObject) localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA);
		return editorData.getFieldValue(field);
	}
	
	@Override
	public float getAbilityFloatField(String field) {
		GameObject editorData = (GameObject) localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA);
		return editorData.getFieldFloatValue(field);
	}
	
	@Override
	public String getAbilityStringField(String field) {
		GameObject editorData = (GameObject) localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA);
		return editorData.getField(field);
	}
	
	@Override
	public boolean getAbilityBooleanField(String field) {
		GameObject editorData = (GameObject) localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA);
		return editorData.getFieldValue(field) != 0;
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
		this.localStore.put(ABLocalStoreKeys.ITEM, item);
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
			return (T)o;
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
		addInitialUniqueFlags(game, unit);
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

	@Override
	public boolean isRequirementsMet(CSimulation game, CUnit unit) {
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

	@Override
	public int getIconVisibleMenuId() {
		return this.visibleMenuId;
	}
	
	@Override
	public void setIconVisibleMenuId(int menu) {
		this.visibleMenuId = menu;
	}
	
	
	
	
	

	// Unneeded Methods
	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId, boolean autoOrder) {
		return null;
	}

	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	public void checkCanTarget(CSimulation game, CUnit unit, int orderId, boolean autoOrder,
			CWidget target, AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(CSimulation game, CUnit unit, int orderId, boolean autoOrder,
			AbilityPointTarget target, AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			boolean autoOrder, AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public War3ID getOnTooltipOverride() {
		return null;
	}
}
